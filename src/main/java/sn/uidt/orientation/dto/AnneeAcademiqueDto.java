package sn.uidt.orientation.dto;

import java.util.List;
import java.util.stream.Collectors;

import sn.uidt.orientation.model.student.InscriptionAnnuelle;

public record AnneeAcademiqueDto(
    Long inscriptionId,
    int annee,
    String cycle,       // LICENCE
    String decision,    // ADMIS, REDOUBLANT
    Double moyenneAnnuelle,
    List<SemestreDto> semestres // Nouvelle structure hiérarchique
) {
    public static AnneeAcademiqueDto fromEntity(InscriptionAnnuelle ia) {
        List<SemestreDto> semestresDto = ia.getInscriptionsSemestrielles().stream()
            .map(SemestreDto::fromEntity)
            .collect(Collectors.toList());
            
        return new AnneeAcademiqueDto(
            ia.getId(),
            ia.getAnneeAcademique(),
            ia.getCycle(),
            ia.getDecisionConseil(),
            ia.getMoyenneAnnuelle(), // Champ renommé dans le modèle
            semestresDto
        );
    }
}