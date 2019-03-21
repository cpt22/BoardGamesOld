package com.cptingle.BoardGames.framework;

public abstract class Gameboard {
	
	protected Game game;
	
	public Gameboard(Game game) {
		this.game = game;
	}
	
	public Game getGame() {
		return game;
	}

}
