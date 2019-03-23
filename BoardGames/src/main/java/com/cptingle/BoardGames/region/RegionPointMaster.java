package com.cptingle.BoardGames.region;

import com.cptingle.BoardGames.games.PointCategory;

public enum RegionPointMaster implements RegionPoint {
	P1(PointCategory.POINT),
	P2(PointCategory.POINT);

	private PointCategory category;

	RegionPointMaster(PointCategory cat) {
		this.category = cat;
	}

	@Override
	public PointCategory getCategory() {
		return category;
	}

	public static RegionPoint matchString(String s) {
		s = s.replaceAll("-", "_");
		for (RegionPointMaster t : RegionPointMaster.values()) {
			if (t.toString().equalsIgnoreCase(s))
				return t;
		}
		return null;
	}
	
	public String configName() {
		return name().toLowerCase().replaceAll("_", "-");
	}

}
