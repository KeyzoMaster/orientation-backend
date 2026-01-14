package sn.uidt.orientation.dto;

import java.util.List;

public record RecommandationDto(
    // Section Licence 3
    String specialiteL3,        // Ex: "Génie Logiciel" ou "N/A"
    String messageL3,           // Ex: "Orientation inutile, vous êtes déjà en L3."

    // Section Master (Liste détaillée)
    List<PredictionMaster> statsMaster, 
    String messageMaster,       // Ex: "Voici vos chances..." ou "Déjà en Master"

    // Section Globale
    String conseilTrajectoire,
    List<String> matieresACorriger
) {
    public record PredictionMaster(
        String specialite, // "Génie Logiciel", "Réseaux"
        String type,       // "Public", "Prive"
        double probabilite
    ) {}
}