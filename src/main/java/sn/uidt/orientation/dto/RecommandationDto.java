package sn.uidt.orientation.dto;

import java.util.List;

public record RecommandationDto(
	    String specialiteL3,
	    double probabiliteMasterPublic,
	    double probabiliteMasterPrive,
	    String conseilTrajectoire,
	    List<String> matieresACorriger
	) {}
