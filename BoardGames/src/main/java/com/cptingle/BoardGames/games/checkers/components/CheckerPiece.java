package com.cptingle.BoardGames.games.checkers.components;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.cptingle.BoardGames.framework.Game;
import com.cptingle.BoardGames.games.MaterialType;
import com.cptingle.BoardGames.games.PlayerType;
import com.cptingle.BoardGames.games.checkers.CheckersGameboard;
import com.cptingle.BoardGames.util.Direction;
import com.cptingle.BoardGames.util.GridPoint2D;

public class CheckerPiece {
	private Map<PlayerType, Material> typeMap;

	private Game game;
	private CheckersGameboard gameboard;
	private PlayerType player;
	private boolean king;
	private Location location;
	private GridPoint2D point;

	public CheckerPiece(Game game, CheckersGameboard gameboard/* , Location location */, PlayerType player,
			GridPoint2D point) {
		this.game = game;
		this.gameboard = gameboard;
		this.location = gameboard.getLocationFromPoint(point).clone().add(0, 1, 0);
		this.player = player;
		this.point = point;

		this.typeMap = new HashMap<>();
		typeMap.put(PlayerType.PLAYER_ONE, gameboard.getMaterial(MaterialType.P1_PIECE));
		typeMap.put(PlayerType.PLAYER_TWO, gameboard.getMaterial(MaterialType.P2_PIECE));
		this.king = false;

		set();
	}

	public PlayerType getPlayer() {
		return player;
	}

	public boolean isKing() {
		return king;
	}

	public void setKing(boolean value) {
		this.king = value;
		reset();
	}

	public GridPoint2D getPoint() {
		return point;
	}

	public void setGridPoint(GridPoint2D pt) {
		this.point = pt;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location l) {
		this.location = l;
	}

	public void move(Location loc, GridPoint2D pt) {
		this.point = pt;
		unset();
		this.location = loc;
		set();
	}

	public Block getTopBlock() {
		return game.getWorld().getBlockAt(location.getBlockX(), location.getBlockY() + 1, location.getBlockZ());
	}

	public void unset() {
		this.location.getBlock().setType(Material.AIR);
		getTopBlock().setType(Material.AIR);
	}

	public void set() {
		this.location.getBlock().setType(typeMap.get(player));
		if (king) {
			getTopBlock().setType(gameboard.getMaterial(MaterialType.KING));
		} else {
			getTopBlock().setType(Material.AIR);
		}
	}

	public void reset() {
		unset();
		set();
	}

	public void setKingIfKing(Direction d) {
		if (d == Direction.X) {
			if (player == PlayerType.PLAYER_ONE && point.X() == 7)
				setKing(true);
			else if (player == PlayerType.PLAYER_TWO && point.X() == 0)
				setKing(true);
		} else if (d == Direction.Z) {
			if (player == PlayerType.PLAYER_ONE && point.Z() == 7)
				setKing(true);
			else if (player == PlayerType.PLAYER_TWO && point.Z() == 0)
				setKing(true);
		}
	}

	public void select() {
		this.location.getBlock().setType(gameboard.getMaterial(MaterialType.SELECTED));
	}

	public void unselect() {
		this.location.getBlock().setType(typeMap.get(player));
	}

	public void relocate(CheckerSquare toSquare) {
		toSquare.setPiece(this);
		this.move(toSquare.getLocation().clone().add(0,1,0), toSquare.getPoint());
	}
}
