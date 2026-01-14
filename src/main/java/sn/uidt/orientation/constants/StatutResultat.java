package sn.uidt.orientation.constants;

public enum StatutResultat {
    EN_COURS,
    VALIDE,      // Note >= 10
    COMPENSE,    // Note < 10 mais validé par moyenne générale
    AJOURNE,     // Non validé
    ACQUIS_ANTERIEUR // Déjà validé l'année précédente (Capitalisation)
}
