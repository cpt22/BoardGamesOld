package com.cptingle.BoardGames.games.minesweeper.components;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;

import com.cptingle.BoardGames.games.minesweeper.MinesweeperGameboard;
import com.cptingle.BoardGames.util.GridPoint2D;

public class MSBoardSquare {
	private MinesweeperGameboard gameboard;
	private GridPoint2D point;
	private Location location;
	private boolean mine;
	private boolean checked;
	private boolean flagged;
	private int value;
	private Set<MSBoardSquare> surroundingSquares;

	public MSBoardSquare(MinesweeperGameboard gameboard, GridPoint2D point, Location location) {
		this.gameboard = gameboard;
		this.point = point;
		this.location = location;
		this.mine = false;
		this.checked = false;
		this.flagged = false;
		this.value = 0;
		this.surroundingSquares = new HashSet<>();
	}

	public boolean isFlagged() {
		return flagged;
	}

	public void setFlagged(boolean val) {
		flagged = val;
		recalculateBlock();
	}

	public void setSurroundingSquares(Set<MSBoardSquare> vals) {
		this.surroundingSquares = vals;
	}

	public Set<MSBoardSquare> getSurroundingSquares() {
		return this.surroundingSquares;
	}

	public GridPoint2D getPoint() {
		return point;
	}

	public Location getLocation() {
		return location;
	}

	public int getIntValue() {
		return value;
	}

	public void setIntValue(int v) {
		this.value = v;
	}

	public void incrementValue() {
		this.value += 1;
	}

	public boolean isChecked() {
		return this.checked;
	}

	public boolean isEmpty() {
		return !isChecked() && !isMine();
	}

	public void setMine() {
		mine = true;
	}

	public boolean isMine() {
		return mine;
	}

	public void checkSquare() {
		checked = true;
		if (isMine()) {
			reveal();
			return;
		} else if (value > 0) {
			reveal();
		} else if (value == 0) {
			checkSurroundingSquares();
			reveal();
		}
	}

	public void checkSurroundingSquares() {
		for (MSBoardSquare sq : surroundingSquares) {
			if (sq.isEmpty())
				sq.checkSquare();
		}
	}

	public void reveal() {
		recalculateBlock();
	}

	public void recalculateBlock() {
		Material m = null;
		if (isChecked() && !isMine()) {
			if (value > 0)
				m = gameboard.getMaterial(SweeperMaterial.valueOf("N" + value));
			else
				m = Material.CYAN_WOOL;// SweeperMaterial.UNCOVERED;
		} else if (!isChecked() && isFlagged()) {
			m = Material.LIGHT_GRAY_WOOL;// SweeperMaterial.FLAG;
		} else if (isChecked() && isMine()) {
			m = Material.PURPLE_WOOL;// SweeperMaterial.MINE;
		} else {
			m = Material.SMOOTH_STONE;// SweeperMaterial.COVERED;
		}

		if (m != null)
			location.getBlock().setType(m);
	}

}
