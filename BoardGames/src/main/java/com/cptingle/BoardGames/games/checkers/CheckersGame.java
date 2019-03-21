package com.cptingle.BoardGames.games.checkers;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.cptingle.BoardGames.BoardGames;
import com.cptingle.BoardGames.framework.Game;
import com.cptingle.BoardGames.games.GameType;
import com.cptingle.BoardGames.games.checkers.components.PlayerType;
import com.cptingle.BoardGames.messaging.Msg;
import com.cptingle.BoardGames.util.config.ConfigUtils;

public class CheckersGame extends Game {

	// Board
	protected CheckersGameboard gameboard;
	protected PlayerType turn;

	// Teleports
	protected Location p1Spawn;
	protected Location p2Spawn;

	public CheckersGame(BoardGames plugin, ConfigurationSection section, String name, World world) {
		super(plugin, section, GameType.CHECKERS, name, world);
		this.listener = new CheckersListener(plugin, this);

		this.p1Spawn = ConfigUtils.parseLocation(section, "coords.p1spawn", world);
		this.p2Spawn = ConfigUtils.parseLocation(section, "coords.p2spawn", world);

		gameboard = new CheckersGameboard(this);

		players.clear();
	}

	public PlayerType whoseTurn() {
		return turn;
	}

	public void setTurn(PlayerType p) {
		turn = p;
		if (gameboard.isDoubleJumpTurn()) {
			messenger.tell(playerMap.get(p), "Make a double jump!");
		} else {
			messenger.tell(playerMap.get(p), Msg.GAME_YOUR_TURN);
		}
	}

	public CheckersGameboard getGameboard() {
		return gameboard;
	}

	public void nextTurn() {
		if (turn == PlayerType.PLAYER_ONE) {
			setTurn(PlayerType.PLAYER_TWO);
		} else {
			setTurn(PlayerType.PLAYER_ONE);
		}
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	@Override
	public void forceEnd() {
		running = false;
		cleanAndResetGame();
	}

	private void cleanAndResetGame() {

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
				if (p1Spawn != null)
					p.teleport(p1Spawn);
				messenger.tell(p, "You have joined game " + name + ". You are player 1");
				return true;
			} else if (players.size() == 1 && playerMap.get(PlayerType.PLAYER_TWO) == null) {
				super.playerJoin(p);
				playerMap.put(PlayerType.PLAYER_TWO, p);
				if (p2Spawn != null)
					p.teleport(p2Spawn);

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
