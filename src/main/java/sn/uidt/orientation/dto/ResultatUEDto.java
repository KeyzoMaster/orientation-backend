package sn.uidt.orientation.dto;

import java.util.List;
import java.util.stream.Collectors;

import sn.uidt.orientation.constants.TypeInscriptionUE;
import sn.uidt.orientation.model.student.ResultatUE;

public record ResultatUEDto(
	    String codeUE,
	    String libelle,
	    int credits,
	    Double moyenne,
	    String statut, // VALIDE, AJOURNE
	    boolean isDette,
	    List<NoteDto> notes
	) {
	    public static ResultatUEDto fromEntity(ResultatUE r) {
	        List<NoteDto> notesDto = r.getNotesEC().stream()
	            .map(NoteDto::fromEntity)
	            .collect(Collectors.toList());

	        boolean isDette = r.getTypeInscription() != null 
	                          && r.getTypeInscription() != TypeInscriptionUE.STANDARD;

	        return new ResultatUEDto(
	            r.getUe().getCode(),
	            r.getUe().getLibelle(),
	            r.getUe().getCredits(),
	            r.getMoyenne(),
	            (r.getStatut() != null) ? r.getStatut().name() : "EN_COURS",
	            isDette,
	            notesDto
	        );
	    }
	}
