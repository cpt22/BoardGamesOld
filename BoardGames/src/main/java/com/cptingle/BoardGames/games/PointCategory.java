package com.cptingle.BoardGames.games;

public enum PointCategory {
	SPAWN("spawns"),
	POINT("locations"),
	POINT_DIR("directions");
	
	private String path;
	
	PointCategory(String path) {
		this.path = path;
	}
	
	public String getPath() {
		return path;
	}
}
