package com.cptingle.BoardGames.games.sudoku;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.cptingle.BoardGames.BoardGames;
import com.cptingle.BoardGames.framework.Game;
import com.cptingle.BoardGames.games.GameType;
import com.cptingle.BoardGames.games.PlayerType;
import com.cptingle.BoardGames.games.sudoku.components.SudokuMaterial;
import com.cptingle.BoardGames.games.sudoku.components.SudokuRegionPoint;
import com.cptingle.BoardGames.region.RegionPoint;

public class SudokuGame extends Game {
	private static final String RESOURCE_PACK_URL = "https://christmasonevergreen.com/resourcepacks/sudoku-13_2.zip";
	private static final String RESOURCE_PACK_EMPTY_URL = "https://christmasonevergreen.com/resourcepacks/emptypack.zip";
	// Gameboard
	private SudokuGameboard gameboard;

	public SudokuGame(BoardGames plugin, ConfigurationSection section, String name, World world) {
		super(plugin, section, GameType.SUDOKU, name, world);
		this.listener = new SudokuListener(plugin, this);
	}

	public SudokuGameboard getGameboard() {
		return gameboard;
	}

	@Override
	public boolean canJoin(Player p) {
		return players.isEmpty();
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
			p.setResourcePack(RESOURCE_PACK_EMPTY_URL);
		}
		return result;
	}

	@Override
	protected void begin() {
		playerMap.get(PlayerType.PLAYER_ONE).getInventory().clear();
		ItemStack[] items = new ItemStack[9];
		for (int i = 11; i < 20; i++) {
			items[i - 11] = new ItemStack(SudokuMaterial.matchNumber(i).getMaterial(), 1);
		}
		playerMap.get(PlayerType.PLAYER_ONE).getInventory().setContents(items);
		running = true;
	}

	@Override
	public void nextTurn() {

	}

	@Override
	public void initialize() {
		this.gameboard = new SudokuGameboard(this);

		Location spawn = getRegion().getSpawn(SudokuRegionPoint.SPAWN);
		playerSpawns.put(PlayerType.PLAYER_ONE, spawn);
	}

	@Override
	public RegionPoint getRegionPointFromString(String s) {
		return ((RegionPoint) SudokuRegionPoint.matchString(s));
	}

	@Override
	public RegionPoint[] getAllRegionPoints() {
		return ((RegionPoint[]) SudokuRegionPoint.values());
	}

	@Override
	public RegionPoint getRegionPointFromCommonName(String name) {
		return super.getRegionPointFromCommonName(SudokuRegionPoint.values(), name);
	}

}
