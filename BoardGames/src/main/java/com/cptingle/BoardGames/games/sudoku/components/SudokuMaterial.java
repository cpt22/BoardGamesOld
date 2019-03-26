package com.cptingle.BoardGames.games.sudoku.components;

import org.bukkit.Material;

import com.cptingle.BoardGames.games.MaterialType;

public enum SudokuMaterial implements MaterialType {
	BLANK(Material.DIAMOND_ORE, 0),
	DIVIDER(Material.BLACK_WOOL, -1),
	N1(Material.WHITE_WOOL, 1),
	N2(Material.ORANGE_WOOL, 2),
	N3(Material.MAGENTA_WOOL, 3),
	N4(Material.LIGHT_BLUE_WOOL, 4),
	N5(Material.YELLOW_WOOL, 5),
	N6(Material.LIME_WOOL, 6),
	N7(Material.PINK_WOOL, 7),
	N8(Material.GRAY_WOOL, 8),
	N9(Material.LIGHT_GRAY_WOOL, 9),
	N1P(Material.CYAN_WOOL, 11),
	N2P(Material.PURPLE_WOOL, 12),
	N3P(Material.BLUE_WOOL, 13),
	N4P(Material.BROWN_WOOL, 14),
	N5P(Material.GREEN_WOOL, 15),
	N6P(Material.RED_WOOL, 16),
	N7P(Material.GOLD_ORE, 17),
	N8P(Material.IRON_ORE, 18),
	N9P(Material.COAL_ORE, 19);

	private Material material;
	private int val;
	
	private SudokuMaterial(Material mat, int val) {
		this.material = mat;
		this.val = val;
	}
	
	@Override
	public Material getMaterial() {
		return material;
	}

	@Override
	public String configName() {
		return null;
	}
	
	public int getVal() {
		return val;
	}
	
	public static SudokuMaterial matchByMaterial(Material toMatch) {
		for (SudokuMaterial m : values()) {
			if (m.getMaterial() == toMatch)
				return m;
		}
		return null;
	}
	
	public static SudokuMaterial matchNumber(int num) {
		for (SudokuMaterial m : values()) {
			if (m.getVal() == num)
				return m;
		}
		return null;
	}

}
