package com.cptingle.BoardGames.games.checkers.components;

import org.bukkit.Location;
import org.bukkit.Material;

import com.cptingle.BoardGames.framework.Game;
import com.cptingle.BoardGames.games.PlayerType;
import com.cptingle.BoardGames.games.checkers.components.exceptions.PieceNotFoundException;
import com.cptingle.BoardGames.util.GridPoint2D;

public class CheckerSquare {
	private Game game;

	private Material type;
	private CheckerPiece piece;
	private Location location;
	private GridPoint2D point;
	private SquareType sqType;

	public CheckerSquare(Game game, Location location, GridPoint2D point) {
		this.game = game;

		this.location = location;
		this.point = point;
		this.piece = null;
		this.sqType = SquareType.EMPTY;
	}

	public Game getGame() {
		return game;
	}

	public Material getBlock() {
		return type;
	}

	public Location getLocation() {
		return location;
	}

	public GridPoint2D getPoint() {
		return point;
	}

	public CheckerPiece getPiece() {
		return piece;
	}

	public SquareType getType() {
		return sqType;
	}

	public void setType(SquareType t) {
		this.sqType = t;
	}

	public void setPiece(CheckerPiece piece) {
		if (piece == null)
			this.sqType = SquareType.EMPTY;
		else 
			this.sqType = SquareType.PIECE;
		this.piece = piece;
	}

	public void setBlock(Material type) {
		this.type = type;
		this.location.getBlock().setType(type);
	}

	/*public void setLocation(Location location) {
		this.location = location;
	}*/

	/*public void setPoint(GridPoint2D point) {
		this.point = point;
	}*/

	public PlayerType getPlayer() {
		if (piece == null)
			return null;

		return piece.getPlayer();
	}

	public boolean isKing() {
		if (piece != null)
			return piece.isKing();
		return false;
	}

	public void unselect() {
		if (piece != null)
			piece.unselect();
	}

	public void select() {
		if (piece != null)
			piece.select();
	}

	public CheckerPiece removePiece() {
		CheckerPiece temp = piece;
		if (temp == null)
			throw new PieceNotFoundException();
		piece.unset();
		piece = null;
		setType(SquareType.EMPTY);
		return temp;
	}

	public void transferPiece(CheckerSquare toSquare) {
		piece.relocate(toSquare);
		piece = null;
		setType(SquareType.EMPTY);
	}
}
