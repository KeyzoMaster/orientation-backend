package sn.uidt.orientation.dto;

import sn.uidt.orientation.model.student.NoteEC;

public record NoteDto(
	    String nomEC,
	    double valeur,
	    String session // NORMALE, RATTRAPAGE
	) {
	    public static NoteDto fromEntity(NoteEC n) {
	        return new NoteDto(
	            n.getEc().getLibelle(),
	            n.getNote(),
	            n.getSession().name()
	        );
	    }
	}
