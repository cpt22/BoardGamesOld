package com.cptingle.BoardGames.games;

public enum MaterialTypeOld {
	// CHECKERS
	RED_SQUARE("red-square-block"),
	BLACK_SQUARE("black-square-block"),
	KING("king-block"),
	SELECTED("selected-block"),
	// SHARED
	SQUARE("square-block"),
	P1_PIECE("p1-piece-block"),
	P2_PIECE("p2-piece-block");
	
	private String path;
	
	MaterialTypeOld(String p) {
		this.path = p;
	}
	
	public String getPath() {
		return path;
	}
	
}
