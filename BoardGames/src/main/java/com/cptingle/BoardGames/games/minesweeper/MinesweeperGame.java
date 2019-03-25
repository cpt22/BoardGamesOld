package com.cptingle.BoardGames.games.minesweeper;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.cptingle.BoardGames.BoardGames;
import com.cptingle.BoardGames.framework.Game;
import com.cptingle.BoardGames.games.GameType;
import com.cptingle.BoardGames.games.PlayerType;
import com.cptingle.BoardGames.games.minesweeper.components.MinesweeperRegionPoint;
import com.cptingle.BoardGames.region.RegionPoint;



public class MinesweeperGame extends Game {
	private static final String RESOURCE_PACK_URL = "https://christmasonevergreen.com/resourcepacks/minesweeper-13_2.zip";
	private static final String RESOURCE_PACK_EMPTY_URL = "https://christmasonevergreen.com/resourcepacks/emptypack.zip";
	// Gameboard
	private MinesweeperGameboard gameboard;
	private BossBar bossBar;

	public MinesweeperGame(BoardGames plugin, ConfigurationSection section, String name, World world) {
		super(plugin, section, GameType.MINESWEEPER, name, world);
		this.listener = new MinesweeperListener(plugin, this);
		this.bossBar = plugin.getServer().createBossBar(new NamespacedKey(plugin, name + "-minesleft"), "Mines Left:", BarColor.RED, BarStyle.SOLID);
	}

	public MinesweeperGameboard getGameboard() {
		return gameboard;
	}

	@Override
	public boolean canJoin(Player p) {
		return players.size() == 0;
	}

	@Override
	public boolean playerJoin(Player p) {
		if (gm.getGameWithPlayer(p) != null) {
			messenger.tell(p, "You are already in a game!");
			return false;
		}

		if (players.size() == 0 && playerMap.get(PlayerType.PLAYER_ONE) == null) {
			p.setResourcePack(RESOURCE_PACK_URL);
			super.playerJoin(p);
			playerMap.put(PlayerType.PLAYER_ONE, p);
			p.teleport(getSpawnForPlayer(PlayerType.PLAYER_ONE));
			messenger.tell(p, "You joined game " + name + ".");
			bossBar.addPlayer(p);
			begin();
			return true;
		} else {
			messenger.tell(p, "An error occurred joing game '" + name + "'!");
		}

		return false;
	}

	@Override
	public boolean playerLeave(Player p) {
		boolean result = super.playerLeave(p);
		if (result) {
			bossBar.removePlayer(p);
			p.setResourcePack(RESOURCE_PACK_EMPTY_URL);
		}
		return result;
	}

	@Override
	protected void begin() {
		running = true;
		bossBar.setProgress(1.0);
		bossBar.setTitle("Mines Remaining: " + gameboard.getNumMines());
	}
	
	protected BossBar getBossBar() {
		return bossBar;
	}

	@Override
	public void resetGame() {
		super.resetGame();
		if (!plugin.isDisabling()) {
			gameboard.reset();
		}
	}
	
	@Override
	public void doWin(PlayerType pt) {
		running = false;
		final Game gme = this;
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				gme.end();
			}
		}, 10L);
	}
	
	public void lose() {
		Player p = playerMap.get(PlayerType.PLAYER_ONE);
		messenger.tell(p, "You lose!");
		messenger.tell(p, "You will automatically leave the game in 10 seconds!");
		running = false;
		final Game gme = this;
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				gme.end();
			}
		}, 200L);
	}

	@Override
	public void nextTurn() {}
	
	@Override
	public void initialize() {
		this.gameboard = new MinesweeperGameboard(this);

		Location spawn = getRegion().getSpawn(MinesweeperRegionPoint.SPAWN);
		playerSpawns.put(PlayerType.PLAYER_ONE, spawn);
	}

	@Override
	public RegionPoint getRegionPointFromString(String s) {
		return ((RegionPoint) MinesweeperRegionPoint.matchString(s));
	}

	@Override
	public RegionPoint[] getAllRegionPoints() {
		return ((RegionPoint[]) MinesweeperRegionPoint.values());
	}

	@Override
	public RegionPoint getRegionPointFromCommonName(String name) {
		return super.getRegionPointFromCommonName(MinesweeperRegionPoint.values(), name);
	}

}
