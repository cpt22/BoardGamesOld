package com.cptingle.BoardGames.games;

import org.bukkit.Material;

public interface MaterialType {
	/**
	 * Gets the associated material
	 * @return
	 */
	public Material getMaterial();
	
	/**
	 * Gets the config name of this material type
	 * @return
	 */
	public String configName();
}
