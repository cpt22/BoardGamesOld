package com.cptingle.BoardGames.games.tictactoe;

import org.bukkit.Location;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.cptingle.BoardGames.BoardGames;
import com.cptingle.BoardGames.framework.Game;
import com.cptingle.BoardGames.framework.GameListener;

public class TicTacToeListener extends GameListener {

	public TicTacToeListener(BoardGames plugin, Game game) {
		super(plugin, game);
	}

	@Override
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (game.inEditMode())
			return;

		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Location blockLocation = event.getClickedBlock().getLocation();
			if (game.getRegion().contains(blockLocation) && game.isRunning()) {
				event.setCancelled(true);
			}

			if (game.isRunning()) {
				((TicTacToeGame) game).getGameboard().blockClicked(blockLocation, event.getPlayer());
			}
		}
	}

}
