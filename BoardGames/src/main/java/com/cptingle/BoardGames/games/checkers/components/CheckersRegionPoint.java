package com.cptingle.BoardGames.games.checkers.components;

import com.cptingle.BoardGames.games.PointCategory;
import com.cptingle.BoardGames.region.RegionPoint;

public enum CheckersRegionPoint implements RegionPoint {
	BOARD(PointCategory.POINT_DIR),
	P1_SPAWN(PointCategory.SPAWN),
	P2_SPAWN(PointCategory.SPAWN);
	
	private PointCategory category;

	CheckersRegionPoint(PointCategory cat) {
		this.category = cat;
	}

	@Override
	public PointCategory getCategory() {
		return category;
	}
	
	public static RegionPoint matchString(String s) {
		s = s.replaceAll("-", "_");
		for(CheckersRegionPoint t : CheckersRegionPoint.values()) {
			if (t.toString().equalsIgnoreCase(s))
				return t;
		}
		return null;
	}
	
	public String configName() {
		return name().toLowerCase().replaceAll("_", "-");
	}
	
}
