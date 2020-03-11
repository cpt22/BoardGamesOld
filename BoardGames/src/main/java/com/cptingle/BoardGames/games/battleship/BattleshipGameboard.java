/**
 * 
 */
package com.cptingle.BoardGames.games.battleship;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.cptingle.BoardGames.framework.Game;
import com.cptingle.BoardGames.framework.Gameboard;
import com.cptingle.BoardGames.region.RegionPoint;

/**
 * @author christiantingle
 *
 */
public class BattleshipGameboard extends Gameboard {

	public BattleshipGameboard(BattleshipGame game) {
		super(game, BattleshipRegionPoint);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void initMaterials() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isLocationInBoard(Location l) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean blockClicked(Location l, Player p) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void init() {
		// TODO Auto-generated method stub
		
	}

}
