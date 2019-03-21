package com.cptingle.BoardGames.games.checkers;

import org.bukkit.Location;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.cptingle.BoardGames.BoardGames;
import com.cptingle.BoardGames.framework.Game;
import com.cptingle.BoardGames.framework.GameListener;

public class CheckersListener extends GameListener {

	public CheckersListener(BoardGames plugin, Game game) {
		super(plugin, game);
	}

	@Override
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (game.inEditMode()) {
			return;
		}

		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Location blockLocation = event.getClickedBlock().getLocation();
			if (game.getRegion().contains(blockLocation) && game.isRunning()) {
				event.setCancelled(true);
			}

			if (game.isRunning()) {

				if (((CheckersGame) game).getGameboard().isValidBoardClick(blockLocation)) {

					((CheckersGame) game).getGameboard().blockClicked(blockLocation, event.getPlayer());
				}
			}
		}

	}

}
