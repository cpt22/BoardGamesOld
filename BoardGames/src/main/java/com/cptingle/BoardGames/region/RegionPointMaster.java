package com.cptingle.BoardGames.region;

import org.bukkit.Material;

public enum RegionPointMaster implements RegionPoint {
	R1(PointCategory.POINT, "r1", Material.RED_WOOL), R2(PointCategory.POINT, "r2", Material.RED_WOOL);

	private PointCategory category;
	private String common;
	private Material showMaterial;

	RegionPointMaster(PointCategory cat, String common, Material showMaterial) {
		this.category = cat;
		this.common = common;
		this.showMaterial = showMaterial;
	}

	@Override
	public PointCategory getCategory() {
		return category;
	}

	@Override
	public String commonName() {
		return common;
	}

	@Override
	public String configName() {
		return name().toLowerCase().replaceAll("_", "-");
	}

	@Override
	public Material getShowMaterial() {
		return showMaterial;
	}

	public static RegionPoint getFromCommonName(String cn) {
		for (RegionPoint rp : RegionPointMaster.values()) {
			if (rp.commonName().equals(cn))
				return rp;
		}
		return null;
	}

	public static RegionPoint matchString(String s) {
		s = s.replaceAll("-", "_");
		for (RegionPointMaster t : RegionPointMaster.values()) {
			if (t.toString().equalsIgnoreCase(s))
				return t;
		}
		return null;
	}

}
