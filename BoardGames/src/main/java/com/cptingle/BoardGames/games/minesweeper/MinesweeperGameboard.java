package com.cptingle.BoardGames.games.minesweeper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import com.cptingle.BoardGames.framework.Game;
import com.cptingle.BoardGames.games.PlayerType;
import com.cptingle.BoardGames.games.minesweeper.components.MSBoardSquare;
import com.cptingle.BoardGames.games.minesweeper.components.MinesweeperRegionPoint;
import com.cptingle.BoardGames.games.minesweeper.components.SweeperMaterial;
import com.cptingle.BoardGames.util.GridPoint2D;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class MinesweeperGameboard {
	private final double MINE_FACTOR = 0.150;
	// General Stuff
	private MinesweeperGame game;

	// Board Locations
	private Location b1, b2;
	private int boardWidth, boardLength;

	// Board
	private MSBoardSquare[][] board;
	private Set<MSBoardSquare> mines;
	private int numMines, theoreticalNumMinesRemaining;

	// Location
	private BiMap<GridPoint2D, Location> locationPointMap;

	// Locations stuff
	private Location anchorPoint;

	// SweeperMaterial stuff
	private Map<SweeperMaterial, Material> gameMaterials;

	public MinesweeperGameboard(MinesweeperGame game) {
		this.game = game;
		this.locationPointMap = HashBiMap.create();
		this.mines = new HashSet<>();

		game.getRegion().fixIfNeedFixing(MinesweeperRegionPoint.BOARD_POINT_1, MinesweeperRegionPoint.BOARD_POINT_2);
		b1 = game.getRegion().getPoint(MinesweeperRegionPoint.BOARD_POINT_1);
		b2 = game.getRegion().getPoint(MinesweeperRegionPoint.BOARD_POINT_2);
		
		this.anchorPoint = b1;

		boardWidth = b2.getBlockX() - b1.getBlockX();
		boardLength = b2.getBlockZ() - b1.getBlockZ();

		board = new MSBoardSquare[boardWidth][boardLength];

		gameMaterials = new HashMap<>();
		initMaterials();

		if (!game.getRegion().isSetup()) {
			game.setEnabled(false);
			return;
		}

		reset();
	}

	public void reset() {
		create();
	}

	/**
	 * Creates the game
	 */
	protected void create() {
		numMines = theoreticalNumMinesRemaining = (int) Math.floor((boardWidth * boardLength) * MINE_FACTOR);
		clear();
		for (int x = 0; x < boardWidth; x++) {
			for (int z = 0; z < boardLength; z++) {
				GridPoint2D p = new GridPoint2D(x, z);

				Location l = new Location(anchorPoint.getWorld(), anchorPoint.getBlockX() + x, anchorPoint.getBlockY(),
						anchorPoint.getBlockZ() + z);

				board[x][z] = new MSBoardSquare(this, p, l);
				locationPointMap.put(p, l);
			}
		}

		distributeMines();

		forEach(square -> square.recalculateBlock());
		forEach(square -> square
				.setSurroundingSquares(getSurroundingSquares(square.getPoint().X(), square.getPoint().Z())));

	}

	private void distributeMines() {
		Random random = new Random();
		int i = 0;
		while (i < numMines) {
			int xPos = random.nextInt(boardWidth);
			int zPos = random.nextInt(boardLength);
			MSBoardSquare bs = board[xPos][zPos];
			if (!bs.isMine()) {
				i++;
				bs.setMine();
				mines.add(bs);

				for (int r = -1; r <= 1; r++) {
					if (xPos + r >= 0 && xPos + r < boardWidth) {
						for (int c = -1; c <= 1; c++) {
							if (zPos + c >= 0 && zPos + c < boardLength) {
								if (!board[xPos + r][zPos + c].isMine())
									board[xPos + r][zPos + c].incrementValue();
							}
						}
					}
				}
			}
		}
	}

	private void forEach(Consumer<MSBoardSquare> consumer) {
		Stream.of(board).forEach(row -> Stream.of(row).forEach(consumer));
	}

	public int getNumMines() {
		return numMines;
	}

	public int getWidth() {
		return boardWidth;
	}

	public int getLength() {
		return boardLength;
	}

	private Set<MSBoardSquare> getSurroundingSquares(int x, int z) {
		Set<MSBoardSquare> result = new HashSet<>();
		for (int r = -1; r <= 1; r++) {
			if (x + r >= 0 && x + r < boardWidth) {
				for (int c = -1; c <= 1; c++) {
					if (r == 0 && c == 0) {
						continue;
					} else if (z + c >= 0 && z + c < boardLength && board[x + r][z + c].isEmpty()) {
						result.add(board[x + r][z + c]);
					}
				}
			}
		}
		result.remove(board[x][z]);
		return result;
	}

	public void updateBossBar() {
		BossBar bb = game.getBossBar();
		bb.setProgress((theoreticalNumMinesRemaining * 1.0) / numMines);
		bb.setTitle("Mines Remaining: " + theoreticalNumMinesRemaining + "/" + numMines);
	}

	public Game getGame() {
		return game;
	}

	public Material getMaterial(SweeperMaterial m) {
		return gameMaterials.get(m);
	}

	public Location getAnchorPoint() {
		return anchorPoint;
	}

	/**
	 * Clear maps and collections
	 */
	protected void clear() {
		locationPointMap.clear();
	}

	/**
	 * Get a world location from 2D grid point
	 * 
	 * @param p
	 * @return Corresponding world location
	 */
	public Location getLocationFromPoint(GridPoint2D p) {
		return locationPointMap.get(p);
	}

	/**
	 * Get the grid point that corresponds to a given world location
	 * 
	 * @param l
	 * @return Corresponding grid point
	 */
	public GridPoint2D getPointFromLocation(Location l) {
		return new GridPoint2D(l.getBlockX() - anchorPoint.getBlockX(), l.getBlockZ() - anchorPoint.getBlockZ());
	}

	// Abstract Methods
	private void initMaterials() {
		gameMaterials.put(SweeperMaterial.COVERED, Material.SMOOTH_STONE);
		gameMaterials.put(SweeperMaterial.N1, Material.WHITE_WOOL);
		gameMaterials.put(SweeperMaterial.N2, Material.ORANGE_WOOL);
		gameMaterials.put(SweeperMaterial.N3, Material.MAGENTA_WOOL);
		gameMaterials.put(SweeperMaterial.N4, Material.LIGHT_BLUE_WOOL);
		gameMaterials.put(SweeperMaterial.N5, Material.YELLOW_WOOL);
		gameMaterials.put(SweeperMaterial.N6, Material.LIME_WOOL);
		gameMaterials.put(SweeperMaterial.N7, Material.PINK_WOOL);
		gameMaterials.put(SweeperMaterial.N8, Material.GRAY_WOOL);
		gameMaterials.put(SweeperMaterial.FLAG, Material.LIGHT_GRAY_WOOL);
		gameMaterials.put(SweeperMaterial.UNCOVERED, Material.CYAN_WOOL);
		gameMaterials.put(SweeperMaterial.MINE, Material.PURPLE_WOOL);
	}

	/**
	 * Tells if a given location falls within this games board boundaries. AKA a
	 * valid block click
	 * 
	 * @param l
	 * @return true if block is within board
	 */
	public boolean isLocationInBoard(Location l) {
		int x = l.getBlockX();
		int y = l.getBlockY();
		int z = l.getBlockZ();
		if (y == b1.getBlockY()) {
			if (x >= b1.getBlockX() && x <= b2.getBlockX()) {
				if (z >= b1.getBlockZ() && z <= b2.getBlockZ()) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Handles block click on board
	 * 
	 * @param l
	 * @param p
	 * @return
	 */
	public boolean blockLeftClicked(Location l, Player p) {
		// Return if block is outside board
		if (!isLocationInBoard(l))
			return false;

		GridPoint2D point = getPointFromLocation(l);
		MSBoardSquare square = board[point.X()][point.Z()];
		// Check that board square is empty
		if (!square.isChecked() && !square.isFlagged()) {
			square.checkSquare();

			if (square.isMine()) {
				revealAll();
				game.lose();
			}
		}

		return true;
	}

	public boolean blockRightClicked(Location l, Player p) {
		// Return if block is outside board
		if (!isLocationInBoard(l)) {
			return false;
		}

		GridPoint2D point = getPointFromLocation(l);
		MSBoardSquare square = board[point.X()][point.Z()];
		// Check that board square is empty
		if (!square.isFlagged()) {
			square.setFlagged(true);
			theoreticalNumMinesRemaining--;
			if (square.isMine())
				mines.remove(square);
		} else {
			square.setFlagged(false);
			theoreticalNumMinesRemaining++;
			if (square.isMine())
				mines.add(square);
		}

		updateBossBar();

		if (mines.isEmpty())
			game.doWin(PlayerType.PLAYER_ONE);

		return true;
	}

	public void revealAll() {
		for (MSBoardSquare[] r : board) {
			for (MSBoardSquare square : r) {
				if (square.isMine() && !square.isChecked()) {
					square.checkSquare();
				}
			}
		}
	}

}
