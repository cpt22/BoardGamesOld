package com.cptingle.BoardGames.games.tictactoe.components;

import com.cptingle.BoardGames.games.PointCategory;
import com.cptingle.BoardGames.region.RegionPoint;

public enum TicTacToeRegionPoint implements RegionPoint {
	BOARD(PointCategory.POINT_DIR), 
	SPAWN(PointCategory.SPAWN);

	private PointCategory category;

	TicTacToeRegionPoint(PointCategory cat) {
		this.category = cat;
	}

	@Override
	public PointCategory getCategory() {
		return category;
	}

	public static RegionPoint matchString(String s) {
		s = s.replaceAll("-", "_");
		for (TicTacToeRegionPoint t : TicTacToeRegionPoint.values()) {
			if (t.toString().equalsIgnoreCase(s))
				return t;
		}
		return null;
	}
	
	public String configName() {
		return name().toLowerCase().replaceAll("_", "-");
	}

}
