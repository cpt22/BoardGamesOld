package com.cptingle.BoardGames.games.tictactoe.components;

import org.bukkit.Material;

import com.cptingle.BoardGames.region.PointCategory;
import com.cptingle.BoardGames.region.RegionPoint;

public enum TicTacToeRegionPoint implements RegionPoint {
	BOARD(PointCategory.POINT_DIR, "board", Material.PINK_WOOL), SPAWN(PointCategory.SPAWN, "spawn",
			Material.BLUE_WOOL);

	private PointCategory category;
	private String common;
	private Material showMaterial;

	TicTacToeRegionPoint(PointCategory cat, String common, Material showMaterial) {
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

	public static RegionPoint getFromCommonName(String cn) {
		for (RegionPoint rp : TicTacToeRegionPoint.values()) {
			if (rp.commonName().equals(cn))
				return rp;
		}
		return null;
	}

	public static RegionPoint matchString(String s) {
		s = s.replaceAll("-", "_");
		for (TicTacToeRegionPoint t : TicTacToeRegionPoint.values()) {
			if (t.toString().equalsIgnoreCase(s))
				return t;
		}
		return null;
	}
}
