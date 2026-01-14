:- use_module(library(http/json)).
:- use_module(library(lists)).

% Configuration
:- style_check(-singleton).
:- style_check(-discontiguous).

% ==============================================================================
% 0. UTILITAIRES GENERIQUES
% ==============================================================================

moyenne_liste([], 0.0) :- !.
moyenne_liste(L, M) :- sum_list(L, S), length(L, N), N > 0, M is S / N.

take(N, List, Taken) :- 
    length(List, Len), 
    (Len =< N -> Taken = List ; length(Taken, N), append(Taken, _, List)).

derniere_specialite(Id, CodeSpec) :-
    findall(Annee-Spec, annee_academique(Id, Annee, _, _, _, Spec, _), L),
    sort(L, Triee), last(Triee, _-CodeSpec).

nom_spec('l3_gl', "Licence 3 GL") :- !.
nom_spec('l3_rt', "Licence 3 RT") :- !.
nom_spec('m_gl', "Master GL") :- !.
nom_spec('m_rt', "Master RT") :- !.
nom_spec(_, "Inconnu").

% ==============================================================================
% 1. ANALYSE RELATIVE
% ==============================================================================

moyenne_promo_semestre(Annee, Spec, Semestre, MoyennePromo) :-
    findall(MoyEtu, (
        annee_academique(Id, Annee, _, _, _, Spec, _),
        moyenne_semestre(Id, Annee, Semestre, MoyEtu),
        MoyEtu > 0
    ), Liste),
    moyenne_liste(Liste, MoyennePromo).

get_ratio_global_semestriel(Id, Cycle, RatioGlobal) :-
    findall(Ratio, (
        annee_academique(Id, Annee, _, _, Cycle, Spec, _),
        moyenne_semestre(Id, Annee, Semestre, MaMoy),
        MaMoy > 0,
        moyenne_promo_semestre(Annee, Spec, Semestre, MoyPromo),
        MoyPromo > 0,
        Ratio is MaMoy / MoyPromo
    ), Ratios),
    moyenne_liste(Ratios, RatioGlobal).

get_statut_relatif(Id, Cycle, Statut) :-
    get_ratio_global_semestriel(Id, Cycle, Ratio),
    ( Ratio >= 1.10 -> Statut = 'Leader'
    ; Ratio >= 1.05 -> Statut = 'Solide'
    ; Ratio >= 0.95 -> Statut = 'Standard'
    ; Statut = 'Suiveur' ).

% ==============================================================================
% 2. ANALYSE MICROSCOPIQUE
% ==============================================================================

get_signature_notes(Id, Cycle, Signature) :-
    findall(Code-Note, (
        annee_academique(Id, An, _, _, Cycle, _, _),
        note_ec(Id, Code, Note, _, An)
    ), ListeBrute),
    sort(ListeBrute, Signature).

calculer_distance_notes(Sig1, Sig2, Distance) :-
    calculer_delta_carre(Sig1, Sig2, SommeCarres, NbCommuns),
    (NbCommuns > 5 -> 
        Distance is sqrt(SommeCarres) / NbCommuns
    ;   Distance is 9999).

calculer_delta_carre([], _, 0, 0).
calculer_delta_carre([C-N1|R1], Sig2, Somme, Nb) :-
    member(C-N2, Sig2), !,
    Delta is N1 - N2,
    calculer_delta_carre(R1, Sig2, S_Reste, N_Reste),
    Somme is S_Reste + (Delta * Delta),
    Nb is N_Reste + 1.
calculer_delta_carre([_|R1], Sig2, Somme, Nb) :-
    calculer_delta_carre(R1, Sig2, Somme, Nb).

trouver_voisins_precis(IdCible, Cycle, VoisinsFinaux) :-
    get_statut_relatif(IdCible, Cycle, MonStatut),
    get_signature_notes(IdCible, Cycle, MaSig),
    derniere_specialite(IdCible, MaSpec),

    findall(IdCand, (
        est_ancien(IdCand),
        annee_academique(IdCand, _, _, _, Cycle, MaSpec, _),
        get_statut_relatif(IdCand, Cycle, MonStatut)
    ), CandidatsBruts),
    list_to_set(CandidatsBruts, CandidatsUniques),

    findall(Dist-IdCand, (
        member(IdCand, CandidatsUniques),
        get_signature_notes(IdCand, Cycle, SigCand),
        calculer_distance_notes(MaSig, SigCand, Dist),
        Dist < 5
    ), CandidatsDistances),
    
    keysort(CandidatsDistances, Tries),
    take(20, Tries, TopK_Pairs),
    findall(Id, member(_-Id, TopK_Pairs), VoisinsFinaux).

% ==============================================================================
% 3. OUTILS STATISTIQUES
% ==============================================================================

a_choisi_spec(Spec, Id) :- annee_academique(Id, _, _, _, 'licence', Spec, _).
a_ete_admis(Spec, Type, Id) :- candidature_historique(Id, _, Spec, Type, 'accepte').
est_admis_tout_type(Spec, Id) :- candidature_historique(Id, _, Spec, _, 'accepte').
a_eu_diplome(Spec, Id) :- annee_academique(Id, _, _, _, _, Spec, D), member(D, ['admis','passage_conditionnel']).

calculer_pourcentage(NbOk, Total, Taux) :-
    Total > 0, !,
    Taux is round((NbOk / Total) * 100).
calculer_pourcentage(_, _, 0).

calculer_taux_brut([], _, 0) :- !.
calculer_taux_brut(Population, Condition, Taux) :-
    length(Population, Total),
    Total > 0,
    include(Condition, Population, Reussites),
    length(Reussites, NbOk),
    calculer_pourcentage(NbOk, Total, Taux).

get_masters_cibles(SpecActuelle, Masters) :-
    (SpecActuelle == 'l3_gl' -> Masters = ['m_gl'] ;
     SpecActuelle == 'l3_rt' -> Masters = ['m_rt'] ;
     Masters = ['m_gl', 'm_rt']).

% ==============================================================================
% 4. ANALYSE L3
% ==============================================================================

analyse_l3(Id, 'l3', "N/A", "Orientation ignorée : Déjà en spécialité L3.") :- !.
analyse_l3(Id, 'master', "N/A", "Orientation ignorée : Déjà en Master.") :- !.

analyse_l3(Id, _, Rec, Msg) :-
    get_statut_relatif(Id, 'licence', Statut),
    trouver_voisins_precis(Id, 'licence', Voisins),
    length(Voisins, Nb),

    calculer_taux_brut(Voisins, a_choisi_spec('l3_gl'), TauxChoixGL),
    include(a_choisi_spec('l3_gl'), Voisins, VoisinsGL),
    calculer_taux_brut(VoisinsGL, a_eu_diplome('l3_gl'), TauxReussiteGL),

    calculer_taux_brut(Voisins, a_choisi_spec('l3_rt'), TauxChoixRT),
    include(a_choisi_spec('l3_rt'), Voisins, VoisinsRT),
    calculer_taux_brut(VoisinsRT, a_eu_diplome('l3_rt'), TauxReussiteRT),

    ((Nb == 0) -> 
        Rec="Indéterminé", Msg="Pas assez de données microscopiques similaires." 
    ;
        (TauxReussiteGL >= TauxReussiteRT -> Rec="Génie Logiciel" ; Rec="Réseaux & Télécoms"),
        format(atom(Msg), "Parmi vos ~d 'jumeaux' académiques (~w) : GL (Choix: ~d%, Réussite: ~d%) vs RT (Choix: ~d%, Réussite: ~d%)", [Nb, Statut, TauxChoixGL, TauxReussiteGL, TauxChoixRT, TauxReussiteRT])
    ).

% ==============================================================================
% 5. ANALYSE MASTER (GRANULARITÉ SPÉCIALITÉ)
% ==============================================================================

analyse_master(Id, 'master', [], "Orientation ignorée : Déjà inscrit en Master.") :- !.
analyse_master(Id, _, Stats, Message) :-
    get_statut_relatif(Id, 'licence', Statut),
    trouver_voisins_precis(Id, 'licence', Voisins),
    length(Voisins, NbVoisins),

    derniere_specialite(Id, Spec),
    get_masters_cibles(Spec, Masters),

    findall(
        json([
            specialite=Nom, 
            probabiliteAdmissionPublic=TauxPublic, 
            probabiliteAdmissionPrive=TauxPrive, 
            probabiliteReussite=TauxReussiteGlobal
        ]),
        (
            member(CodeMaster, Masters), 
            nom_spec(CodeMaster, Nom),
            
            % 1. Admission Public
            include(a_ete_admis(CodeMaster, 'PUBLIQUE'), Voisins, AdmisPublic),
            length(AdmisPublic, NbAdmisPublic),
            calculer_pourcentage(NbAdmisPublic, NbVoisins, TauxPublic),
            
            % 2. Admission Prive
            include(a_ete_admis(CodeMaster, 'PRIVEE'), Voisins, AdmisPrive),
            length(AdmisPrive, NbAdmisPrive),
            calculer_pourcentage(NbAdmisPrive, NbVoisins, TauxPrive),

            % 3. Réussite (Sur l'ensemble des admis, Public + Privé)
            include(est_admis_tout_type(CodeMaster), Voisins, TousAdmis),
            length(TousAdmis, NbTousAdmis),
            include(a_eu_diplome(CodeMaster), TousAdmis, Diplomes),
            length(Diplomes, NbDiplomes),
            calculer_pourcentage(NbDiplomes, NbTousAdmis, TauxReussiteGlobal)
        ),
        Stats
    ),
    format(atom(Message), "Basé sur les ~d anciens étudiants ayant le parcours de notes le plus proche du vôtre (et statut '~w').", [NbVoisins, Statut]).

% ==============================================================================
% 6. MAIN
% ==============================================================================

a_commence_cours(Id, Annee) :- note_ec(Id, _, _, _, Annee), !.
niveau_actif(Id, 'master') :- annee_academique(Id, A, _, _, 'master', _, _), a_commence_cours(Id, A), !.
niveau_actif(Id, 'l3')     :- annee_academique(Id, A, _, _, 'licence', S, _), member(S, ['l3_gl','l3_rt']), a_commence_cours(Id, A), !.
niveau_actif(_, 'l1_l2').

consultation_json(Id) :-
    (niveau_actif(Id, N) -> Niv=N ; Niv='l1_l2'),
    analyse_l3(Id, Niv, RL3, ML3),
    analyse_master(Id, Niv, StatsM, MsgM),
    json_write(current_output, json([
        specialiteL3 = RL3, messageL3 = ML3,
        statsMaster = StatsM, messageMaster = MsgM,
        conseilTrajectoire = "Analyse Microscopique (Notes) x Relative (Semestres)"
    ])), nl.