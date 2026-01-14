package sn.uidt.orientation.dto;

import java.util.List;
import java.util.stream.Collectors;

import sn.uidt.orientation.model.student.InscriptionAnnuelle;

public record ParcoursDto(
	    Long etudiantId,
	    String nomComplet,
	    List<AnneeAcademiqueDto> annees
	) {
	    public static ParcoursDto fromEntity(Long etudiantId, String nom, List<InscriptionAnnuelle> inscriptions) {
	        List<AnneeAcademiqueDto> anneesDto = inscriptions.stream()
	            .map(AnneeAcademiqueDto::fromEntity)
	            .collect(Collectors.toList());
	        return new ParcoursDto(etudiantId, nom, anneesDto);
	    }
	}