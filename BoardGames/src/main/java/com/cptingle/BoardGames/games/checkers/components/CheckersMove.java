package com.cptingle.BoardGames.games.checkers.components;

import com.cptingle.BoardGames.util.GridPoint2D;

public class CheckersMove {
	
	private GridPoint2D fromPoint;
	private GridPoint2D toPoint;
	
	public CheckersMove(GridPoint2D fromPoint, GridPoint2D toPoint) {
		this.fromPoint = fromPoint;
		this.toPoint = toPoint;
	}
	
	public GridPoint2D getFromPoint() {
		return fromPoint;
	}
	
	public GridPoint2D getToPoint() {
		return toPoint;
	}
}
