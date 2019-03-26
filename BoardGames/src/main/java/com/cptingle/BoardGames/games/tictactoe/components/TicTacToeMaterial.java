package com.cptingle.BoardGames.games.tictactoe.components;

import org.bukkit.Material;

import com.cptingle.BoardGames.games.MaterialType;

public enum TicTacToeMaterial implements MaterialType {
	SQUARE("square-block"), 
	P1_PIECE("p1-piece-block"), 
	P2_PIECE("p2-piece-block");

	private String configName;

	TicTacToeMaterial(String p) {
		this.configName = p;
	}

	@Override
	public Material getMaterial() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String configName() {
		// TODO Auto-generated method stub
		return configName;
	}

}
