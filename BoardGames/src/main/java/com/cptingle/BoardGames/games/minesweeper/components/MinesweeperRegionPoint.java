package com.cptingle.BoardGames.games.minesweeper.components;

import org.bukkit.Material;

import com.cptingle.BoardGames.games.PointCategory;
import com.cptingle.BoardGames.region.RegionPoint;

public enum MinesweeperRegionPoint implements RegionPoint {
	BOARD_POINT_1(PointCategory.POINT_CUBOID, "board1", Material.PINK_WOOL),
	BOARD_POINT_2(PointCategory.POINT_CUBOID, "board2", Material.PINK_WOOL),
	SPAWN(PointCategory.SPAWN, "spawn", Material.BLUE_WOOL);

	private PointCategory category;
	private String common;
	private Material showMaterial;

	MinesweeperRegionPoint(PointCategory cat, String common, Material showMaterial) {
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
		for (RegionPoint rp : MinesweeperRegionPoint.values()) {
			if (rp.commonName().equals(cn))
				return rp;
		}
		return null;
	}

	public static RegionPoint matchString(String s) {
		s = s.replaceAll("-", "_");
		for (MinesweeperRegionPoint t : MinesweeperRegionPoint.values()) {
			if (t.toString().equalsIgnoreCase(s))
				return t;
		}
		return null;
	}
}
