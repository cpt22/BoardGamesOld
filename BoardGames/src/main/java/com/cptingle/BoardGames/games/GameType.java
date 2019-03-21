package com.cptingle.BoardGames.games;

public enum GameType {
	CHECKERS("checkers"), BATTLESHIP("battleship");

	private String text;

	GameType(String text) {
		this.text = text;
	}

	public String toString() {
		return this.text;
	}

	public String configName() {
		return toString();
	}

	public String getText() {
		return this.text;
	}

	public static GameType fromString(String text) {
		for (GameType t : GameType.values()) {
			if (t.getText().equalsIgnoreCase(text)) {
				return t;
			}
		}
		return null;
	}

}
