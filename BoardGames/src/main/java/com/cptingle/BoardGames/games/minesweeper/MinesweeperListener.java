package com.cptingle.BoardGames.games.minesweeper;

import org.bukkit.Location;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.cptingle.BoardGames.BoardGames;
import com.cptingle.BoardGames.framework.Game;
import com.cptingle.BoardGames.framework.GameListener;

public class MinesweeperListener extends GameListener {

	public MinesweeperListener(BoardGames plugin, Game game) {
		super(plugin, game);
	}

	@Override
	public void onBlockBreak(BlockBreakEvent event) {
		if (game.inEditMode())
			return;

		Location blockLocation = event.getBlock().getLocation();

		if (game.getRegion().contains(blockLocation))
			event.setCancelled(true);

		if (game.isRunning()) {
			((MinesweeperGame) game).getGameboard().blockLeftClicked(blockLocation, event.getPlayer());
		}

	}

	@Override
	public void onBlockPlace(BlockPlaceEvent event) {
		if (game.inEditMode())
			return;

		Location blockLocation = event.getBlock().getLocation().add(0, -1, 0);

		if (game.getRegion().contains(blockLocation))
			event.setCancelled(true);

		if (game.isRunning()) {
			((MinesweeperGame) game).getGameboard().blockRightClicked(blockLocation, event.getPlayer());
		}

	}

	@Override
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (game.inEditMode())
			return;

		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Location blockLocation = event.getClickedBlock().getLocation();
			if (game.isRunning()) {
				((MinesweeperGame) game).getGameboard().blockRightClicked(blockLocation, event.getPlayer());
				event.setCancelled(true);
			}
		}
	}

}
