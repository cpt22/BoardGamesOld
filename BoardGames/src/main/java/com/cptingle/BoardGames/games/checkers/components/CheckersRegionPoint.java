package com.cptingle.BoardGames.games.checkers.components;

import org.bukkit.Material;

import com.cptingle.BoardGames.region.PointCategory;
import com.cptingle.BoardGames.region.RegionPoint;

public enum CheckersRegionPoint implements RegionPoint {
	BOARD(PointCategory.POINT_DIR, "board", Material.PINK_WOOL),
	P1_SPAWN(PointCategory.SPAWN, "p1spawn", Material.BLUE_WOOL),
	P2_SPAWN(PointCategory.SPAWN, "p2spawn", Material.RED_WOOL);

	private PointCategory category;
	private String common;
	private Material showMaterial;

	CheckersRegionPoint(PointCategory cat, String common, Material showMaterial) {
		this.category = cat;
		this.common = common;
		this.showMaterial = showMaterial;
	}

	@Override
	public PointCategory getCategory() {
		return category;
	}

	@Override
	public String configName() {
		return name().toLowerCase().replaceAll("_", "-");
	}

	@Override
	public String commonName() {
		return common;
	}

	@Override
	public Material getShowMaterial() {
		return showMaterial;
	}

	public static RegionPoint matchString(String s) {
		s = s.replaceAll("-", "_");
		for (CheckersRegionPoint t : CheckersRegionPoint.values()) {
			if (t.toString().equalsIgnoreCase(s))
				return t;
		}
		return null;
	}

	public static RegionPoint getFromCommonName(String cn) {
		for (RegionPoint rp : CheckersRegionPoint.values()) {
			if (rp.commonName().equals(cn))
				return rp;
		}
		return null;
	}

}
