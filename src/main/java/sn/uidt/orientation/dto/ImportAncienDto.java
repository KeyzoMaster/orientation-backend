package sn.uidt.orientation.dto;

import java.util.List;

public record ImportAncienDto(
	    String email,
	    String prenom,
	    String nom,
	    List<HistoriqueAnneeDto> parcours
	) {
	    public record HistoriqueAnneeDto(
	        int annee,
	        String niveau, // L_S1
	        String decision,
	        String specialiteCode, // GL, RT
	        List<HistoriqueNoteDto> notes
	    ) {}
	    
	    public record HistoriqueNoteDto(
	        String codeUE,
	        double moyenne,
	        boolean validee
	    ) {}
	}