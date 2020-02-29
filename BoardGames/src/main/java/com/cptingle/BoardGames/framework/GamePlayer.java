package com.cptingle.BoardGames.framework;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GamePlayer {

	private Player player;
	private ItemStack[] inventory;
	private Location returnLocation;

	public GamePlayer(Player p) {
		this.player = p;
		this.inventory = p.getInventory().getContents();
		this.returnLocation = p.getLocation();
	}

	public Player getPlayer() {
		return player;
	}

	public ItemStack[] getInventory() {
		return inventory;
	}

	public Location getReturnLocation() {
		return returnLocation;
	}

}
