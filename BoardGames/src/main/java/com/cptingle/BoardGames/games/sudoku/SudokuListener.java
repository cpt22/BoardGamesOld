package com.cptingle.BoardGames.games.sudoku;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.cptingle.BoardGames.BoardGames;
import com.cptingle.BoardGames.framework.Game;
import com.cptingle.BoardGames.framework.GameListener;

public class SudokuListener extends GameListener {

	public SudokuListener(BoardGames plugin, Game game) {
		super(plugin, game);
	}

	@Override
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (game.inEditMode())
			return;

		/*
		 * if (event.getClickedBlock() != null) if
		 * (!game.getRegion().contains(event.getClickedBlock().getLocation()))
		 * event.setCancelled(true);
		 */
		if (event.getClickedBlock() != null) {

			Location l = event.getClickedBlock().getLocation();

			if (game.isRunning()) {
				if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
					((SudokuGame) game).getGameboard().leftClickBlock(l, event.getPlayer());
				} else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
					((SudokuGame) game).getGameboard().rightClickBlock(l, event.getPlayer());
				}
			}
			if (game.getRegion().contains(l))
				event.setCancelled(true);
		}
	}

	@Override
	public void onBlockPlace(BlockPlaceEvent event) {
		if (game.inEditMode())
			return;

		Location l = event.getBlock().getLocation();

		if (game.getRegion().contains(l))
			event.setCancelled(true);

		if (game.isRunning()) {
			((SudokuGame) game).getGameboard().rightClickBlock(l.clone().add(0, -1, 0), event.getPlayer());

		}

	}

	@Override
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		if (game.inGame(event.getPlayer()))
			event.setCancelled(true);
	}

	@Override
	public void onInventoryOpen(InventoryOpenEvent event) {
		game.tellAllPlayers("inventoryOpen");
		if (game.inGame((Player) event.getPlayer()))
			event.setCancelled(true);
	}

	@Override
	public void onInventoryInteract(InventoryInteractEvent event) {
		if (game.inGame((Player) event.getWhoClicked()))
			event.setCancelled(true);
	}
}
