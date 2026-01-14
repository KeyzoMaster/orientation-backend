package sn.uidt.orientation.dto;

public record InscriptionAdministrativeDto(
    Long etudiantId,
    int annee,         // 2024
    Long specialiteId  // ID de la spé (ex: L1 Tronc commun)
) {}