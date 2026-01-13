
:- use_module(library(http/json)).

moyenne_par_domaine(EtudiantId, Domaine, Moyenne) :-
    findall(Note, (
        note_ec(EtudiantId, CodeEC, Note, _, _),
        ec_domain(CodeEC, Domaine) 
    ), Notes),
    calculer_moyenne(Notes, Moyenne).


moyenne_generale(EtudiantId, Annee, Moyenne) :-
    findall(Note, note_ec(EtudiantId, _, Note, _, Annee), Notes),
    calculer_moyenne(Notes, Moyenne).

calculer_moyenne([], 0).
calculer_moyenne(L, M) :- sum_list(L, S), length(L, N), N > 0, M is S / N.


recommander_l3(EtudiantId, Resultat) :-
    moyenne_par_domaine(EtudiantId, dev, MDev),
    moyenne_par_domaine(EtudiantId, reseau, MRes),
    ( MDev > MRes + 1 -> Resultat = "Génie Logiciel"
    ; MRes > MDev + 1 -> Resultat = "Réseaux et Télécommunications"
    ; Resultat = "Tronc Commun (Choix libre)"
    ).


predire_master(EtudiantId, ProbPub, ProbPriv) :-
    moyenne_generale(EtudiantId, _, MG),
    % Logique simplifiée : plus la moyenne est haute, plus la probabilité augmente
    ( MG >= 14 -> ProbPub = 90, ProbPriv = 95
    ; MG >= 12 -> ProbPub = 60, ProbPriv = 85
    ; MG >= 10 -> ProbPub = 20, ProbPriv = 60
    ; ProbPub = 5, ProbPriv = 30
    ).

consultation_json(EtudiantId) :-
    recommander_l3(EtudiantId, RecoL3),
    predire_master(EtudiantId, PPub, PPriv),
    
    findall(UE, (statut_ue(EtudiantId, UE, _, _, false)), Dettes),

    Response = json([
        specialiteL3 = RecoL3,
        probabiliteMasterPublic = PPub,
        probabiliteMasterPrive = PPriv,
        matieresACorriger = Dettes,
        conseilTrajectoire = "Analyse effectuee par le systeme expert UFR SET"
    ]),

    json_write(current_output, Response),
    nl.