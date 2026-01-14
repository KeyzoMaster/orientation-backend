:- use_module(library(http/json)).
:- use_module(library(lists)).

% Configuration
:- style_check(-singleton).
:- style_check(-discontiguous).

% ==============================================================================
% 1. MOTEUR D'ANALYSE (ABSOLU + RELATIF)
% ==============================================================================

% --- A. CALCULS DE BASE ---

% Moyenne étudiant pour une année donnée
moyenne_annuelle_etudiant(Id, Annee, Moyenne) :-
    findall(Note, note_ec(Id, _, Note, _, Annee), Notes),
    moyenne_liste(Notes, Moyenne).

moyenne_liste([], 0.0).
moyenne_liste(L, M) :- sum_list(L, S), length(L, N), N > 0, M is S / N.

% Moyenne de la classe (Cohorte)
moyenne_promo(Annee, Spec, MoyennePromo) :-
    findall(MoyEtu, (
        annee_academique(Id, Annee, _, _, _, Spec, _),
        moyenne_annuelle_etudiant(Id, Annee, MoyEtu),
        MoyEtu > 0
    ), ListeMoyennes),
    moyenne_liste(ListeMoyennes, MoyennePromo).

% --- B. DIMENSION 1 : PROFIL ABSOLU (Niveau brut) ---
get_cluster_absolu(M, 'Excellent') :- M >= 16.
get_cluster_absolu(M, 'Tres Bon')  :- M >= 14, M < 16.
get_cluster_absolu(M, 'Bon')       :- M >= 12, M < 14.
get_cluster_absolu(M, 'Moyen')     :- M >= 10, M < 12.
get_cluster_absolu(M, 'Fragile')   :- M < 10.

% --- C. DIMENSION 2 : PROFIL RELATIF (Positionnement classe) ---
get_position_relative(Ratio, 'Leader')   :- Ratio >= 1.10. % +10% au dessus de la classe
get_position_relative(Ratio, 'Solide')   :- Ratio >= 1.05, Ratio < 1.10.
get_position_relative(Ratio, 'Standard') :- Ratio >= 0.95, Ratio < 1.05.
get_position_relative(Ratio, 'Suiveur')  :- Ratio < 0.95.

% --- D. PROFIL CROISÉ (LA SYNTHÈSE) ---
% On identifie l'étudiant par un triplet : profil(Absolu, Relatif, Specialite)
get_profil_croise(Id, Cycle, profil(Cluster, Position, Spec)) :-
    % 1. Trouver la dernière année active
    findall(An-S, annee_academique(Id, An, _, _, Cycle, S, _), L),
    sort(L, Triee), last(Triee, Annee-Spec),
    
    % 2. Calculer les metrics
    moyenne_annuelle_etudiant(Id, Annee, MaMoy),
    moyenne_promo(Annee, Spec, MoyPromo),
    (MoyPromo > 0 -> Ratio is MaMoy / MoyPromo ; Ratio is 1.0),
    
    % 3. Déduire les profils
    get_cluster_absolu(MaMoy, Cluster),
    get_position_relative(Ratio, Position).

% ==============================================================================
% 2. MOTEUR DE RECHERCHE (FILTRAGE DOUBLE)
% ==============================================================================

trouver_semblables_croises(ProfilCible, Cycle, ListeIds) :-
    ProfilCible = profil(ClusterVise, PositionVisee, SpecVisee),
    
    findall(Id, (
        est_ancien(Id),
        % Le candidat doit matcher SUR LES DEUX TABLEAUX (Absolu ET Relatif)
        get_profil_croise(Id, Cycle, profil(ClusterVise, PositionVisee, SpecVisee))
    ), ListeIdsBrute),
    sort(ListeIdsBrute, ListeIds).

% ==============================================================================
% 3. OUTILS STATISTIQUES (LISSAGE)
% ==============================================================================

calculer_taux_brut([], _, 0).
calculer_taux_brut(Population, Condition, Taux) :-
    length(Population, Total),
    include(Condition, Population, Reussites),
    length(Reussites, NbOk),
    Taux is round((NbOk / Total) * 100).

% Lissage intelligent : Plus l'échantillon "double profil" est petit, plus on élargit
lisser_resultat(Nb, TauxLoc, TauxGlob, Final) :-
    (Nb >= 10 -> 
        Final = TauxLoc % Echantillon riche : on fait confiance au profil croisé
    ; 
        % Echantillon faible : on mixe avec la tendance globale
        Poids is 10 - Nb, 
        Score is ((TauxLoc * Nb) + (TauxGlob * Poids)) / 10,
        Final is round(Score)
    ).

% Prédicats Helpers (ID en dernier pour include/3 !)
a_ete_admis(Spec, Type, Id) :- candidature_historique(Id, _, Spec, Type, 'accepte').
a_eu_diplome(Spec, Id) :- annee_academique(Id, _, _, _, _, Spec, D), member(D, ['admis','passage_conditionnel']).

nom_spec('l3_gl', "Licence 3 GL") :- !.
nom_spec('l3_rt', "Licence 3 RT") :- !.
nom_spec('m_gl', "Master GL") :- !.
nom_spec('m_rt', "Master RT") :- !.
nom_spec(_, "Inconnu").

% ==============================================================================
% 4. ANALYSE L3
% ==============================================================================

analyse_l3(Id, 'l3', Rec, Msg) :- Rec = "N/A", Msg = "Déjà en L3.", !.
analyse_l3(Id, 'master', Rec, Msg) :- Rec = "N/A", Msg = "Déjà en Master.", !.
analyse_l3(Id, _, Rec, Msg) :-
    get_profil_croise(Id, 'licence', Profil),
    Profil = profil(Clust, Pos, _),
    
    trouver_semblables_croises(Profil, 'licence', Semblables),
    length(Semblables, Nb),

    calculer_taux_brut(Semblables, a_eu_diplome('l3_gl'), TGL),
    calculer_taux_brut(Semblables, a_eu_diplome('l3_rt'), TRT),

    ((TGL=0, TRT=0) -> 
        Rec="Indéterminé", Msg="Pas assez de profils croisés similaires." 
    ;
        (TGL >= TRT -> Rec="Génie Logiciel", T=TGL ; Rec="Réseaux & Télécoms", T=TRT),
        % CORRECTION : Utilisation de ~w et ~d
        format(atom(Msg), "Profil croisé '~w' + '~w' : ~d% de réussite historique sur ~d cas.", [Clust, Pos, T, Nb])
    ).

% ==============================================================================
% 5. ANALYSE MASTER
% ==============================================================================

analyse_master(Id, 'master', [], "Déjà en Master.") :- !.
analyse_master(Id, _, Stats, Message) :-
    get_profil_croise(Id, 'licence', Profil),
    Profil = profil(Clust, Pos, _),

    % 1. Trouver les Jumeaux Parfaits (Même niveau, Même dynamique)
    trouver_semblables_croises(Profil, 'licence', Semblables),
    length(Semblables, Nb),

    % 2. Population de contrôle (Même spécialité mais profil différent)
    findall(Other, (est_ancien(Other), not(member(Other, Semblables))), Reste),

    findall(
        json([specialite=Nom, type=Type, probabiliteAdmission=FinalAdm, probabiliteReussite=FinalReu]),
        (
            member(Code, ['m_gl', 'm_rt']), member(Type, ['PUBLIQUE', 'PRIVEE']), nom_spec(Code, Nom),
            
            % Admission
            calculer_taux_brut(Semblables, a_ete_admis(Code, Type), LocAdm),
            calculer_taux_brut(Reste, a_ete_admis(Code, Type), GlobAdm),
            lisser_resultat(Nb, LocAdm, GlobAdm, BrutAdm),
            (BrutAdm > 95 -> FinalAdm=95 ; (BrutAdm<5 -> FinalAdm=5 ; FinalAdm=BrutAdm)),
            
            % Réussite
            calculer_taux_brut(Semblables, a_eu_diplome(Code), LocReu),
            calculer_taux_brut(Reste, a_eu_diplome(Code), GlobReu),
            lisser_resultat(Nb, LocReu, GlobReu, FinalReu)
        ),
        Stats
    ),
    % CORRECTION : Utilisation de ~w et ~d
    format(atom(Message), "Analyse croisée : Niveau '~w' combiné au statut '~w' (basé sur ~d parcours).", [Clust, Pos, Nb]).

% ==============================================================================
% 6. MAIN & HELPERS
% ==============================================================================
consultation_json(Id) :-
    (niveau_actif(Id, N) -> Niv=N ; Niv='l1_l2'),
    analyse_l3(Id, Niv, RL3, ML3),
    analyse_master(Id, Niv, StatsM, MsgM),
    json_write(current_output, json([
        specialiteL3 = RL3, messageL3 = ML3,
        statsMaster = StatsM, messageMaster = MsgM,
        conseilTrajectoire = "Croisement Profil Absolu x Position Relative"
    ])), nl.

a_commence_cours(Id, Annee) :- note_ec(Id, _, _, _, Annee), !.
niveau_actif(Id, 'master') :- annee_academique(Id, A, _, _, 'master', _, _), a_commence_cours(Id, A), !.
niveau_actif(Id, 'l3')     :- annee_academique(Id, A, _, _, 'licence', S, _), member(S, ['l3_gl','l3_rt']), a_commence_cours(Id, A), !.
niveau_actif(_, 'l1_l2').