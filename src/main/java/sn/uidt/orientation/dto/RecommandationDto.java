package sn.uidt.orientation.dto;

import java.util.List;

public record RecommandationDto(
    String specialiteL3,
    String messageL3,
    
    List<PredictionMaster> statsMaster, 
    String messageMaster,
    
    String conseilTrajectoire
) {
    public record PredictionMaster(
        String specialite,
        String type,
        double probabiliteAdmission, // "Chances d'être retenu"
        double probabiliteReussite   // "Réussite académique si retenu"
    ) {}
}