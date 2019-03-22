package com.cptingle.BoardGames.framework;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.cptingle.BoardGames.games.MaterialType;
import com.cptingle.BoardGames.util.Direction;
import com.cptingle.BoardGames.util.GridPoint2D;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public abstract class Gameboard {
	// General Stuff
	protected Game game;

	// Location
	protected BiMap<GridPoint2D, Location> locationPointMap;

	// Locations stuff
	protected Location anchorPoint;
	protected Location directionPoint;
	protected Direction direction;

	// Materials stuff
	protected Map<MaterialType, Material> gameMaterials;

	public Gameboard(Game game) {
		this.game = game;
		this.locationPointMap = HashBiMap.create();
		init();
		
		this.anchorPoint = game.getRegion().getB1();
		this.directionPoint = game.getRegion().getB2();

		orient();
		
		gameMaterials = new HashMap<>();
		initMaterials();

		if (anchorPoint != null && directionPoint != null) {
			//reset();
		} else {
			game.getPlugin().getLogger().severe("ERROR, GAME " + game.getName() + " COULD NOT BE ENABLED");
			if (anchorPoint == null)
				game.getPlugin().getLogger().severe("ERROR, GAME " + game.getName() + " ANCHOR POINT NULL");
			if (directionPoint == null)
				game.getPlugin().getLogger().severe("ERROR, GAME " + game.getName() + " DIR POINT NULL");
			game.setEnabled(false);
		}

	}

	public Game getGame() {
		return game;
	}

	public Material getMaterial(MaterialType m) {
		return gameMaterials.get(m);
	}

	public Material getMaterial(MaterialType t, String dflt) {
		String configMaterial = game.getSpecificSettings().getString(t.getPath());

		if (configMaterial == null || configMaterial.trim().equals(""))
			configMaterial = dflt;
		return Material.matchMaterial(configMaterial);
	}

	public Direction getDirection() {
		return direction;
	}

	public Location getAnchorPoint() {
		return anchorPoint;
	}

	/**
	 * Orient board
	 */
	protected void orient() {
		if (anchorPoint == null || directionPoint == null) {
			game.setEnabled(false);
			game.getPlugin().getLogger().warning("Points missing for game " + game.getName());
			direction = Direction.X;
			return;
		}

		if (anchorPoint.getBlockX() == directionPoint.getBlockX()) {
			if (anchorPoint.getBlockZ() < directionPoint.getBlockZ()) {
				direction = Direction.Z;
			}
		} else if (anchorPoint.getBlockZ() == directionPoint.getBlockZ()) {
			if (anchorPoint.getBlockX() < directionPoint.getBlockX()) {
				direction = Direction.X;
			}
		} else {
			direction = Direction.X;
		}
	}

	/**
	 * Clear maps and collections
	 */
	protected void clear() {
		locationPointMap.clear();
	}

	/**
	 * Get a world location from 2D grid point
	 * 
	 * @param p
	 * @return Corresponding world location
	 */
	public Location getLocationFromPoint(GridPoint2D p) {
		return locationPointMap.get(p);
	}

	/**
	 * Get the grid point that corresponds to a given world location
	 * 
	 * @param l
	 * @return Corresponding grid point
	 */
	public GridPoint2D getPointFromLocation(Location l) {
		return new GridPoint2D(l.getBlockX() - anchorPoint.getBlockX(), l.getBlockZ() - anchorPoint.getBlockZ());
	}

	// Abstract Methods
	protected abstract void initMaterials();

	/**
	 * Tells if a given location falls within this games board boundaries. AKA a
	 * valid block click
	 * 
	 * @param l
	 * @return true if block is within board
	 */
	public abstract boolean isLocationInBoard(Location l);

	/**
	 * Handles block click on board
	 * 
	 * @param l
	 * @param p
	 * @return
	 */
	public abstract boolean blockClicked(Location l, Player p);

	/**
	 * Resets gameboard
	 */
	public abstract void reset();

	/**
	 * Initialize things that need to happen before parent class is finished
	 * constructing
	 */
	protected abstract void init();

}
