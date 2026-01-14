:- use_module(library(http/json)).
:- use_module(library(lists)).

:- style_check(-singleton).
:- style_check(-discontiguous).

% ==============================================================================
% 1. UTILITAIRES & CONTEXTE
% ==============================================================================

% Vérifie si l'étudiant a des notes pour une année donnée
a_commence_cours(EtudiantId, Annee) :-
    note_ec(EtudiantId, _, _, _, Annee), !.

% DÉTERMINATION DU NIVEAU ACTUEL (Avec la logique "Inscription + Notes")
niveau_actuel(Id, master) :-
    annee_academique(Id, Annee, _, _, 'master', _, _),
    a_commence_cours(Id, Annee), !.

niveau_actuel(Id, l3) :-
    annee_academique(Id, Annee, _, _, 'licence', Spec, _),
    (Spec == 'l3_gl' ; Spec == 'l3_rt'),
    a_commence_cours(Id, Annee), !.

niveau_actuel(_, l1_l2).

% Trouve la dernière année académique enregistrée pour l'étudiant
derniere_annee(EtudiantId, AnneeMax) :-
    findall(A, annee_academique(EtudiantId, A, _, _, _, _, _), Annees),
    sort(Annees, Sorted),
    last(Sorted, AnneeMax).

% Récupère la dernière spécialité (pour la cohérence du parcours)
derniere_specialite(EtudiantId, CodeSpec) :-
    findall(Annee-Spec, annee_academique(EtudiantId, Annee, _, _, _, Spec, _), Liste),
    sort(Liste, ListeTriee),
    last(ListeTriee, _-CodeSpec).
    
% Calcul Moyenne Générale
moyenne_generale(EtudiantId, Moyenne) :-
    findall(Note, note_ec(EtudiantId, _, Note, _, _), Notes),
    calculer_moyenne_liste(Notes, Moyenne).

calculer_moyenne_liste([], 0.0).
calculer_moyenne_liste(L, M) :- sum_list(L, S), length(L, N), N > 0, M is S / N.

% FILTRAGE DES DETTES : Uniquement celles de la dernière année
lister_dettes(EtudiantId, Dettes) :-
    derniere_annee(EtudiantId, LastYear),
    % Le 3ème argument de statut_ue est l'année
    findall(UE, statut_ue(EtudiantId, UE, LastYear, _, false), ListeBrute),
    sort(ListeBrute, Dettes).

% ==============================================================================
% 2. LOGIQUE L3 (Orientation)
% ==============================================================================

analyse_l3(EtudiantId, NiveauActuel, Recommandation, Message) :-
    ( NiveauActuel == master ; NiveauActuel == l3 ) 
    -> (
        Recommandation = "N/A",
        format(atom(Message), "Orientation L3 ignoree : Vous etes deja actif en ~w.", [NiveauActuel])
    )
    ; (
        % Logique de prédiction simplifiée (à enrichir avec les règles domaines)
        derniere_specialite(EtudiantId, Spec),
        ( Spec == 'l_info' -> 
            ( moyenne_generale(EtudiantId, M), M > 12 -> 
                Recommandation = "Genie Logiciel", Message = "Vos resultats sont solides."
              ; Recommandation = "Reseaux & Telecoms", Message = "Profil equilibre pour les systemes."
            )
          ; Recommandation = "Continuité Parcours", Message = "Poursuivez dans votre filiere."
        )
    ).

% ==============================================================================
% 3. LOGIQUE MASTER (Probabilités Détaillées)
% ==============================================================================

analyse_master(EtudiantId, NiveauActuel, Stats, Message) :-
    NiveauActuel == master 
    -> (
        Stats = [],
        Message = "Orientation Master ignoree : Vous etes deja actif en Master."
    )
    ; (
        Message = "Voici vos probabilites d'admission par parcours.",
        calculer_toutes_probas_master(EtudiantId, Stats)
    ).

% Génère la liste des objets JSON pour chaque couple Spécialité/Type
calculer_toutes_probas_master(EtudiantId, Stats) :-
    findall(
        json([specialite=SpecNom, type=TypeFormation, probabilite=Proba]),
        (
            member(SpecCode, ['gl', 'rt']),
            member(TypeFormation, ['Public', 'Prive']),
            calculer_score_specifique(EtudiantId, SpecCode, TypeFormation, Proba),
            nom_specialite(SpecCode, SpecNom)
        ),
        Stats
    ).

nom_specialite('gl', "Genie Logiciel").
nom_specialite('rt', "Reseaux & Telecoms").

% Calcul du score pour une combinaison précise
calculer_score_specifique(EtudiantId, SpecVisee, Type, Proba) :-
    derniere_specialite(EtudiantId, SpecActuelle),
    moyenne_generale(EtudiantId, MG),
    lister_dettes(EtudiantId, Dettes),
    length(Dettes, NbDettes),

    % 1. Score de base selon les notes
    ( MG >= 14, NbDettes == 0 -> Base = 90
    ; MG >= 12, NbDettes == 0 -> Base = 70
    ; MG >= 11 -> Base = 50
    ; Base = 20
    ),

    % 2. Bonus/Malus selon cohérence (Ex: L3 GL -> Master RT est difficile)
    ( verifier_coherence(SpecActuelle, SpecVisee) -> BonusCoh = 0 ; BonusCoh = -40 ),

    % 3. Bonus Privé (Plus facile d'accès)
    ( Type == 'Prive' -> BonusType = 20 ; BonusType = 0 ),

    Resultat is Base + BonusCoh + BonusType,
    Proba is max(0, min(100, Resultat)).

verifier_coherence(SpecActuelle, SpecVisee) :-
    (SpecActuelle == 'l_info') ;
    (SpecActuelle == 'l3_gl', SpecVisee == 'gl') ;
    (SpecActuelle == 'l3_rt', SpecVisee == 'rt').

% ==============================================================================
% 4. POINT D'ENTREE JSON
% ==============================================================================

consultation_json(EtudiantId) :-
    niveau_actuel(EtudiantId, Niveau),
    
    analyse_l3(EtudiantId, Niveau, RecL3, MsgL3),
    analyse_master(EtudiantId, Niveau, StatsMaster, MsgMaster),
    lister_dettes(EtudiantId, Dettes),

    Response = json([
        specialiteL3 = RecL3,
        messageL3 = MsgL3,
        statsMaster = StatsMaster,
        messageMaster = MsgMaster,
        conseilTrajectoire = "Analyse complete effectuee.",
        matieresACorriger = Dettes
    ]),

    json_write(current_output, Response),
    nl.