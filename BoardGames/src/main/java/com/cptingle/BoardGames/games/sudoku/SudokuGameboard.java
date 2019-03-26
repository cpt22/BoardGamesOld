package com.cptingle.BoardGames.games.sudoku;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.cptingle.BoardGames.framework.Game;
import com.cptingle.BoardGames.framework.Gameboard;
import com.cptingle.BoardGames.games.PlayerType;
import com.cptingle.BoardGames.games.sudoku.components.SudokuMaterial;
import com.cptingle.BoardGames.games.sudoku.components.SudokuRegionPoint;
import com.cptingle.BoardGames.util.GridPoint2D;

public class SudokuGameboard extends Gameboard {
	// Consts
	private final int SIDE = 9;
	private final int NUM_TO_REMOVE = 45;
	private int SRN;

	// Board
	private int[][] board;
	private int[][] workingBoard;
	private boolean[][] mask;

	public SudokuGameboard(Game game) {
		super(game, SudokuRegionPoint.BOARD);

		// Compute square root of SIDE
		Double SRNd = Math.sqrt(SIDE);
		this.SRN = SRNd.intValue();

		this.board = new int[SIDE][SIDE];
		this.workingBoard = new int[SIDE][SIDE];
		this.mask = new boolean[SIDE][SIDE];

		init();
		reset();
	}

	@Override
	protected void initMaterials() {
		for (SudokuMaterial m : SudokuMaterial.values())
			gameMaterials.put(m, m.getMaterial());
	}

	@Override
	public void reset() {
		create();
	}

	private void create() {
		clear();
		for (int x = 0; x < SIDE + 2; x++) {
			for (int z = 0; z < SIDE + 2; z++) {

				int modZ = direction.getModX() != 0 ? direction.getModX() : direction.getModZ();
				int modX = modZ;
				if (direction == BlockFace.NORTH || direction == BlockFace.SOUTH)
					modX = -modX;

				Location l = new Location(anchorPoint.getWorld(), anchorPoint.getBlockX() + modX * x,
						anchorPoint.getBlockY(), anchorPoint.getBlockZ() + modZ * z);
				GridPoint2D p = new GridPoint2D(x, z);

				if (x == 3 || x == 7 || z == 3 || z == 7) {
					l.getBlock().setType(getMaterial(SudokuMaterial.DIVIDER));
				} else {

					if (x > 7) {
						// l.subtract(modX * 2, 0, 0);
						p = p.translate(-2, 0);
					} else if (x > 3) {
						// l.subtract(modX, 0, 0);
						p = p.translate(-1, 0);
					}

					if (z > 7) {
						// l.subtract(0, 0, modZ * 2);
						p = p.translate(0, -2);
					} else if (z > 3) {
						// l.subtract(0, 0, modZ);
						p = p.translate(0, -1);
					}
					locationPointMap.put(p, l);
				}

			}
		}

		// Fill the diagonal of SRN x SRN matrices
		fillDiagonal();

		// Fill remaining blocks
		fillRemaining(0, SRN);

		workingBoard = board.clone();
		// Remove Randomly NUM_TO_REMOVE digits to make game
		removeKDigits();

		loadBlocks();
	}

	//
	// Filling code
	//
	/**
	 * Fill the diagonal SRN number of SRN x SRN matrices
	 */
	private void fillDiagonal() {

		for (int i = 0; i < SIDE; i = i + SRN)

			// for diagonal box, start coordinates->i==j
			fillBox(i, i);
	}

	/**
	 * Returns false if given 3 x 3 block contains num.
	 * 
	 * @param rowStart
	 * @param colStart
	 * @param num
	 * @return
	 */
	boolean unUsedInBox(int rowStart, int colStart, int num) {
		for (int i = 0; i < SRN; i++)
			for (int j = 0; j < SRN; j++)
				if (board[rowStart + i][colStart + j] == num)
					return false;

		return true;
	}

	/**
	 * Fill a 3 x 3 matrix.
	 * 
	 * @param row
	 * @param col
	 */
	void fillBox(int row, int col) {
		int num;
		for (int i = 0; i < SRN; i++) {
			for (int j = 0; j < SRN; j++) {
				do {
					num = randomGenerator(SIDE);
				} while (!unUsedInBox(row, col, num));

				board[row + i][col + j] = num;
			}
		}
	}

	/**
	 * Random generator
	 * 
	 * @param num
	 * @return
	 */
	int randomGenerator(int num) {
		return (int) Math.floor((Math.random() * num + 1));
	}

	/**
	 * Check if safe to put in cell
	 * 
	 * @param i
	 * @param j
	 * @param num
	 * @return
	 */
	boolean CheckIfSafe(int i, int j, int num) {
		return (unUsedInRow(i, num) && unUsedInCol(j, num) && unUsedInBox(i - i % SRN, j - j % SRN, num));
	}

	/**
	 * check in the row for existence
	 * 
	 * @param i
	 * @param num
	 * @return
	 */
	boolean unUsedInRow(int i, int num) {
		for (int j = 0; j < SIDE; j++)
			if (board[i][j] == num)
				return false;
		return true;
	}

	/**
	 * check in the row for existence
	 * 
	 * @param j
	 * @param num
	 * @return
	 */
	boolean unUsedInCol(int j, int num) {
		for (int i = 0; i < SIDE; i++)
			if (board[i][j] == num)
				return false;
		return true;
	}

	/**
	 * A recursive function to fill remaining matrix
	 * 
	 * @param i
	 * @param j
	 * @return
	 */
	boolean fillRemaining(int i, int j) {
		if (j >= SIDE && i < SIDE - 1) {
			i = i + 1;
			j = 0;
		}
		if (i >= SIDE && j >= SIDE)
			return true;

		if (i < SRN) {
			if (j < SRN)
				j = SRN;
		} else if (i < SIDE - SRN) {
			if (j == (int) (i / SRN) * SRN)
				j = j + SRN;
		} else {
			if (j == SIDE - SRN) {
				i = i + 1;
				j = 0;
				if (i >= SIDE)
					return true;
			}
		}

		for (int num = 1; num <= SIDE; num++) {
			if (CheckIfSafe(i, j, num)) {
				board[i][j] = num;
				workingBoard[i][j] = num;
				if (fillRemaining(i, j + 1))
					return true;
				workingBoard[i][j] = 0;
				board[i][j] = 0;
			}
		}
		return false;
	}

	/**
	 * Remove the NUM_TO_REMOVE no. of digits to complete game
	 */
	public void removeKDigits() {
		int count = NUM_TO_REMOVE;
		while (count != 0) {
			/*
			 * int cellId = randomGenerator(SIDE * SIDE);
			 * 
			 * // System.out.println(cellId); // extract coordinates i and j int i = (cellId
			 * / SIDE); int j = cellId % 9; if (j != 0) j = j - 1;
			 */
			Random rand = new Random();
			int i = rand.nextInt(SIDE);
			int j = rand.nextInt(SIDE);

			// System.out.println(i+" "+j);
			if (workingBoard[i][j] != 0) {
				count--;
				workingBoard[i][j] = 0;
			}
		}
	}

	public boolean checkSolved() {
		boolean toReturn = true;
		for (int r = 0; r < SIDE; r++) {
			for (int c = 0; c < SIDE; c++) {
				if (workingBoard[r][c] == 0) {
					return false;
				}

				if (!((board[r][c] == workingBoard[r][c]) || (board[r][c] == (workingBoard[r][c] - 10)))) {
					return false;
				}
			}
		}
		return toReturn;
	}

	private Material getMaterialFromNumber(int num) {
		if (num == 0)
			return SudokuMaterial.BLANK.getMaterial();
		String toGet = "N";
		if (num > 0 && num < 10) {
			toGet = toGet + num;
		} else if (num > 10 && num < 20) {
			toGet = toGet + (num - 10) + "P";
		}
		return SudokuMaterial.valueOf(toGet).getMaterial();
	}

	private void loadBlocks() {
		for (int r = 0; r < SIDE; r++) {
			for (int c = 0; c < SIDE; c++) {
				Location loc = locationPointMap.get(new GridPoint2D(r, c));

				if (loc != null)
					loc.getBlock().setType(getMaterialFromNumber(workingBoard[r][c]));
			}
		}
	}

	@Override
	protected void clear() {
		super.clear();
		board = new int[SIDE][SIDE];
		workingBoard = new int[SIDE][SIDE];
		for (int r = 0; r < SIDE; r++) {
			for (int c = 0; c < SIDE; c++) {
				board[r][c] = 0;
				workingBoard[r][c] = 0;
				mask[r][c] = false;
			}
		}
	}

	@Override
	protected void init() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isLocationInBoard(Location l) {
		if (l.getBlockY() != anchorPoint.getBlockY()) {
			return false;
		}
		GridPoint2D gp = new GridPoint2D(l.getBlockX() - anchorPoint.getBlockX(),
				l.getBlockZ() - anchorPoint.getBlockZ());

		if (gp.X() > 7) {
			gp = gp.translate(-2, 0);
		} else if (gp.X() > 4) {
			gp = gp.translate(-1, 0);
		}

		if (gp.Z() > 7) {
			gp = gp.translate(0, -2);
		} else if (gp.Z() > 4) {
			gp = gp.translate(0, -1);
		}

		return locationPointMap.containsKey(gp);
	}

	@Override
	public boolean blockClicked(Location l, Player p) {
		return false;
	}

	public boolean leftClickBlock(Location l, Player p) {
		if (!isLocationInBoard(l))
			return false;

		GridPoint2D point = locationPointMap.inverse().get(l);
		if (point != null) {
			int sqr = workingBoard[point.X()][point.Z()];
			if (sqr > 10 && sqr < 20)
				workingBoard[point.X()][point.Z()] = 0;

			loadBlocks();
			if (checkSolved()) {
				game.doWin(PlayerType.PLAYER_ONE);
			}
		}
		return true;

	}

	public boolean rightClickBlock(Location l, Player p) {
		if (!isLocationInBoard(l))
			return false;

		ItemStack stack = p.getInventory().getItemInMainHand();
		GridPoint2D point = locationPointMap.inverse().get(l);
		if (point != null) {
			SudokuMaterial mat = SudokuMaterial.matchByMaterial(stack.getType());
			if (mat != null) {
				int val = mat.getVal();
				int sqr = workingBoard[point.X()][point.Z()];
				if (sqr == 0 || (sqr > 10 && sqr < 20))
					workingBoard[point.X()][point.Z()] = val;
			}

			loadBlocks();
			if (checkSolved()) {
				game.doWin(PlayerType.PLAYER_ONE);
			}
		}
		return true;
	}

}
