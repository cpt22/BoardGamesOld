package com.cptingle.BoardGames.games.battleship;

import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.cptingle.BoardGames.BoardGames;
import com.cptingle.BoardGames.framework.Game;
import com.cptingle.BoardGames.games.GameType;
import com.cptingle.BoardGames.region.RegionPoint;

public class BattleshipGame extends Game {

	public BattleshipGame(BoardGames plugin, ConfigurationSection section, String name, World world) {
		super(plugin, section, GameType.BATTLESHIP, name, world);
		this.listener = new BattleshipListener(plugin, this);
		
		type = GameType.BATTLESHIP; 
		// TODO Auto-generated constructor stub
	}

	public void init() {
		// TODO Auto-generated method stub
		
	}

	public void forceEnd() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean canJoin(Player p) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean playerJoin(Player p) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void begin() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void nextTurn() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public RegionPoint getRegionPointFromString(String s) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RegionPoint[] getAllRegionPoints() {
		// TODO Auto-generated method stub
		return null;
	}

}
