package com.cptingle.BoardGames.games.tictactoe;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.cptingle.BoardGames.BoardGames;
import com.cptingle.BoardGames.framework.Game;
import com.cptingle.BoardGames.framework.Gameboard;
import com.cptingle.BoardGames.games.GameType;
import com.cptingle.BoardGames.games.PlayerType;
import com.cptingle.BoardGames.util.config.ConfigUtils;

public class TicTacToeGame extends Game {
	// Board
	protected TicTacToeGameboard gameboard;
	protected PlayerType turn;

	public TicTacToeGame(BoardGames plugin, ConfigurationSection section, String name, World world) {
		super(plugin, section, GameType.TICTACTOE, name, world);

		this.listener = new TicTacToeListener(plugin, this);
		this.gameboard = new TicTacToeGameboard(this);

		Location spawn = ConfigUtils.parseLocation(section, "coords.spawn", world);

		if (spawn == null) {
			setEnabled(false);
		} else {
			playerSpawns.put(PlayerType.PLAYER_ONE, spawn);
			playerSpawns.put(PlayerType.PLAYER_TWO, spawn);
		}
	}

	public Gameboard getGameboard() {
		return gameboard;
	}

	@Override
	public boolean canJoin(Player p) {
		if (players.size() < 2)
			return true;

		return false;
	}

	@Override
	public boolean playerJoin(Player p) {
		// check if player is in a game
		if (gm.getGameWithPlayer(p) != null) {
			messenger.tell(p, "You are already in a game!");
			return false;
		}

		if (players.size() == 0 && playerMap.get(PlayerType.PLAYER_ONE) == null) {
			super.playerJoin(p);
			playerMap.put(PlayerType.PLAYER_ONE, p);
			p.teleport(getSpawnForPlayer(PlayerType.PLAYER_ONE));
			messenger.tell(p, "You have joined game " + name + ". You are player 1");
			return true;
		} else if (players.size() == 1 && playerMap.get(PlayerType.PLAYER_TWO) == null) {
			super.playerJoin(p);
			playerMap.put(PlayerType.PLAYER_TWO, p);
			p.teleport(getSpawnForPlayer(PlayerType.PLAYER_TWO));
			messenger.tell(p, "You have joined game " + name + ". You are player 2");
			tellAllPlayers("Game is now starting!");
			begin();
			return true;
		} else {
			messenger.tell(p, "An error occurred joing game '" + name + "'!");
		}

		return false;
	}

	@Override
	protected void begin() {
		setTurn(PlayerType.PLAYER_ONE);
		running = true;
	}
	
	@Override
	public void resetGame() {
		super.resetGame();
		if (!plugin.isDisabling()) {
			gameboard.reset();
		}
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	@Override
	public void nextTurn() {
		if (turn() == PlayerType.PLAYER_ONE)
			setTurn(PlayerType.PLAYER_TWO);
		else
			setTurn(PlayerType.PLAYER_ONE);
	}

}
