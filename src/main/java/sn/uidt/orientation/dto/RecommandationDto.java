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
        double probabiliteAdmissionPublic,
        double probabiliteAdmissionPrive,
        double probabiliteReussite         
    ) {}
}