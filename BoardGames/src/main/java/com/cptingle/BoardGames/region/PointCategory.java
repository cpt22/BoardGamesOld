package com.cptingle.BoardGames.region;

public enum PointCategory {
	SPAWN("spawns"),
	POINT("locations"),
	POINT_DIR("directions"),
	POINT_CUBOID("locations");
	
	private String path;
	
	PointCategory(String path) {
		this.path = path;
	}
	
	public String getPath() {
		return path;
	}
}
