package com.cptingle.BoardGames.region;

import org.bukkit.Material;

public interface RegionPoint {
	/**
	 * Gets the category/type of region point that the point is
	 * 
	 * @return
	 */
	public PointCategory getCategory();

	/**
	 * Gets the config name for a region point where underscores are replaced with
	 * dashes
	 * 
	 * @return
	 */
	public String configName();

	/**
	 * Gets the common name for the point
	 * 
	 * @return
	 */
	public String commonName();

	/**
	 * Gets the material to be used when showing this point in setup mode
	 * 
	 * @return
	 */
	public Material getShowMaterial();

}
