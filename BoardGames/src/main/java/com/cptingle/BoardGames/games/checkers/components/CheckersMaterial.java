package com.cptingle.BoardGames.games.checkers.components;

import org.bukkit.Material;

import com.cptingle.BoardGames.games.MaterialType;

public enum CheckersMaterial implements MaterialType {
	// CHECKERS
	RED_SQUARE("red-square-block"), BLACK_SQUARE("black-square-block"), KING("king-block"), SELECTED("selected-block"),
	P1_PIECE("p1-piece-block"), P2_PIECE("p2-piece-block");

	private String configName;

	CheckersMaterial(String p) {
		this.configName = p;
	}

	@Override
	public String configName() {
		return configName;
	}

	@Override
	public Material getMaterial() {
		return null;
	}
}
