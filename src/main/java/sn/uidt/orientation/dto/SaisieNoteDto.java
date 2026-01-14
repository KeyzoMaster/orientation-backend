package sn.uidt.orientation.dto;

public record SaisieNoteDto(
	    Long inscriptionId, // L'ID de l'année scolaire de l'étudiant
	    String codeUE,      // "INF111"
	    String nomEC,       // "Algèbre"
	    double note,
	    String session      // "NORMALE" ou "RATTRAPAGE"
	) {}
