package com.cptingle.BoardGames.games;

public enum GameType {
	CHECKERS("checkers", "&c[Checkers]"), BATTLESHIP("battleship", "&b[Battleship]");

	private String text;
	private String prefix;

	GameType(String text, String prefix) {
		this.text = text;
		this.prefix = prefix;
	}

	public String toString() {
		return this.text;
	}

	public String configName() {
		return toString();
	}
	
	public String defaultPrefix() {
		return this.prefix;
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
