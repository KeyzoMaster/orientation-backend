package sn.uidt.orientation.dto;

import java.util.List;
import java.util.stream.Collectors;

import sn.uidt.orientation.model.student.InscriptionSemestrielle;

public record SemestreDto(
    Long id,
    String codeSemestre, // L_S1
    Double moyenne,
    int credits,
    boolean valide,
    List<ResultatUEDto> ues
) {
    public static SemestreDto fromEntity(InscriptionSemestrielle is) {
        List<ResultatUEDto> uesDto = is.getResultatsUE().stream()
            .map(ResultatUEDto::fromEntity)
            .collect(Collectors.toList());

        return new SemestreDto(
            is.getId(),
            (is.getSemestre() != null) ? is.getSemestre().name() : "INCONNU",
            is.getMoyenneSemestre(),
            is.getCreditsObtenus(),
            is.isEstValide(),
            uesDto
        );
    }
}