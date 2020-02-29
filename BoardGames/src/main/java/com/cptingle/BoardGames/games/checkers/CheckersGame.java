package com.cptingle.BoardGames.games.checkers;

import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.cptingle.BoardGames.BoardGames;
import com.cptingle.BoardGames.framework.Game;
import com.cptingle.BoardGames.games.GameType;
import com.cptingle.BoardGames.games.PlayerType;
import com.cptingle.BoardGames.games.checkers.components.CheckersRegionPoint;
import com.cptingle.BoardGames.messaging.Msg;
import com.cptingle.BoardGames.region.RegionPoint;

public class CheckersGame extends Game {
	// Board
	protected CheckersGameboard gameboard;

	public CheckersGame(BoardGames plugin, ConfigurationSection section, String name, World world) {
		super(plugin, section, GameType.CHECKERS, name, world);

		this.listener = new CheckersListener(plugin, this);
	}

	@Override
	public void setTurn(PlayerType p) {
		super.setTurn(p);
		if (gameboard.isDoubleJumpTurn()) {
			messenger.tell(playerMap.get(p), "Make a double jump! ");
		} else {
			messenger.tell(playerMap.get(p), Msg.GAME_YOUR_TURN);
		}
	}

	public CheckersGameboard getGameboard() {
		return gameboard;
	}

	@Override
	public void nextTurn() {
		if (turn() == PlayerType.PLAYER_ONE) {
			setTurn(PlayerType.PLAYER_TWO);
		} else {
			setTurn(PlayerType.PLAYER_ONE);
		}
	}

	@Override
	public void initialize() {
		gameboard = new CheckersGameboard(this);

		playerSpawns.put(PlayerType.PLAYER_ONE, getRegion().getSpawn(CheckersRegionPoint.P1_SPAWN));
		playerSpawns.put(PlayerType.PLAYER_TWO, getRegion().getSpawn(CheckersRegionPoint.P2_SPAWN));
		/*
		 * Location p1Spawn = getRegion().getSpawn(CheckersRegionPoint.P1_SPAWN);
		 * Location p2Spawn = getRegion().getSpawn(CheckersRegionPoint.P2_SPAWN);
		 * 
		 * if (p1Spawn == null || p2Spawn == null) { setEnabled(false); } else {
		 * 
		 * }
		 */
	}

	@Override
	public RegionPoint getRegionPointFromString(String s) {
		return CheckersRegionPoint.matchString(s);
	}

	@Override
	public RegionPoint[] getAllRegionPoints() {
		return ((RegionPoint[]) CheckersRegionPoint.values());
	}

	@Override
	public RegionPoint getRegionPointFromCommonName(String name) {
		return super.getRegionPointFromCommonName(CheckersRegionPoint.values(), name);
	}

	@Override
	public void resetGame() {
		super.resetGame();
		if (!plugin.isDisabling()) {
			gameboard.reset();
		}
	}

	@Override
	public boolean canJoin(Player p) {
		if (players.size() < 2) {
			return true;
		}
		return false;
	}

	@Override
	public boolean playerJoin(Player p) {
		if (gm.getGameWithPlayer(p) == null) {
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
		} else {
			messenger.tell(p, "You are already in a game!");
		}
		return false;
	}

	@Override
	public boolean playerLeave(Player p) {
		return super.playerLeave(p);
	}

	@Override
	protected void begin() {
		setTurn(PlayerType.PLAYER_ONE);
		running = true;
	}

}
