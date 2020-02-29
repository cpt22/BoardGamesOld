package com.cptingle.BoardGames.games;

public enum PlayerType {
	PLAYER_ONE(1), PLAYER_TWO(-1), NONE(0);

	private final int value;

	private PlayerType(int value) {
		this.value = value;
	}

	public int getDirection() {
		return value;
	}
}
