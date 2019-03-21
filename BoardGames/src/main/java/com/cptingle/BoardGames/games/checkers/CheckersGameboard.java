package com.cptingle.BoardGames.games.checkers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.cptingle.BoardGames.framework.Gameboard;
import com.cptingle.BoardGames.games.checkers.components.CheckerPiece;
import com.cptingle.BoardGames.games.checkers.components.CheckerSquare;
import com.cptingle.BoardGames.games.checkers.components.MaterialType;
import com.cptingle.BoardGames.games.checkers.components.PlayerType;
import com.cptingle.BoardGames.games.checkers.components.SquareType;
import com.cptingle.BoardGames.games.checkers.components.exceptions.InvalidMoveException;
import com.cptingle.BoardGames.games.checkers.components.exceptions.PieceNotFoundException;
import com.cptingle.BoardGames.util.Direction;
import com.cptingle.BoardGames.util.GridPoint2D;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class CheckersGameboard extends Gameboard {

	// Game stuff
	private CheckerSquare[][] board;
	private BiMap<GridPoint2D, Location> locationPointMap;
	private Set<CheckerPiece> p1Pieces;
	private Set<CheckerPiece> p2Pieces;

	// Locations stuff
	private Location anchorPoint;
	private Location directionPoint;
	private Direction direction;

	// State stuff
	private CheckerSquare fromSquare;
	private CheckerSquare squareToGoTo;
	private boolean isDoubleJumpMove;

	// Materials stuff
	private Map<MaterialType, Material> gameMaterials;

	public CheckersGameboard(CheckersGame game) {
		super(game);

		this.board = new CheckerSquare[8][8];
		this.locationPointMap = HashBiMap.create();
		this.p1Pieces = new HashSet<>();
		this.p2Pieces = new HashSet<>();

		initMaterials();

		anchorPoint = game.getRegion().getB1();
		directionPoint = game.getRegion().getB2();

		isDoubleJumpMove = false;

		if (anchorPoint != null && directionPoint != null) {
			orient();
			create();
		} else {
			game.getPlugin().getLogger().severe("ERROR, GAME " + game.getName() + " COULD NOT BE ENABLED");
			if (anchorPoint == null)
				game.getPlugin().getLogger().severe("ERROR, GAME " + game.getName() + " ANCHOR POINT NULL");
			if (directionPoint == null)
				game.getPlugin().getLogger().severe("ERROR, GAME " + game.getName() + " DIR POINT NULL");
		}
	}

	/**
	 * Orient board
	 */
	private void orient() {
		if (anchorPoint.getBlockX() == directionPoint.getBlockX()) {
			if (anchorPoint.getBlockZ() < directionPoint.getBlockZ()) {
				direction = Direction.Z;
			}
		} else if (anchorPoint.getBlockZ() == directionPoint.getBlockZ()) {
			if (anchorPoint.getBlockX() < directionPoint.getBlockX()) {
				direction = Direction.X;
			}
		} else {
			direction = Direction.X;
		}
	}

	/**
	 * Create new board
	 */
	private void create() {
		clearMaps();
		for (int x = 0; x < 8; x++) {
			for (int z = 0; z < 8; z++) {
				GridPoint2D p = new GridPoint2D(x, z);
				Location l = new Location(anchorPoint.getWorld(), anchorPoint.getBlockX() + x, anchorPoint.getBlockY(),
						anchorPoint.getBlockZ() + z);
				board[x][z] = new CheckerSquare(game, l, p);

				locationPointMap.put(p, l);

				Location l1 = l.clone().add(0, 1, 0);
				Location l3 = l.clone().add(0, 2, 0);
				l1.getBlock().setType(Material.AIR);
				l3.getBlock().setType(Material.AIR);

				int t1;
				int t2;
				if (direction == Direction.Z) {
					t1 = z;
					t2 = x;
				} else {
					t1 = x;
					t2 = z;
				}
				if (((t1 % 2 == 0) && (t2 % 2 == 1)) || ((t1 % 2 == 1) && (t2 % 2 == 0))) {
					board[x][z].setBlock(getMaterial(MaterialType.BLACK_SQUARE));
					if (t1 < 3) {
						CheckerPiece gp = new CheckerPiece(game, this, PlayerType.PLAYER_ONE, p);
						board[x][z].setPiece(gp);
						p1Pieces.add(gp);
					} else if (t1 > 4) {
						CheckerPiece gp = new CheckerPiece(game, this, PlayerType.PLAYER_TWO, p);
						board[x][z].setPiece(gp);
						p2Pieces.add(gp);
					} else {
						board[x][z].setType(SquareType.EMPTY);
					}
				} else {
					board[x][z].setType(SquareType.RED);
					board[x][z].setBlock(getMaterial(MaterialType.RED_SQUARE));
				}
			}
		}
	}

	// Materials
	private void initMaterials() {
		gameMaterials = new HashMap<>();
		gameMaterials.put(MaterialType.RED_SQUARE,
				Material.matchMaterial(game.getSettings().getString("red-square-block", "RED_CONCRETE")));
		gameMaterials.put(MaterialType.BLACK_SQUARE,
				Material.matchMaterial(game.getSettings().getString("black-square-block", "BLACK_CONCRETE")));
		gameMaterials.put(MaterialType.P1_PIECE,
				Material.matchMaterial(game.getSettings().getString("p1-piece-block", "ACACIA_FENCE")));
		gameMaterials.put(MaterialType.P2_PIECE,
				Material.matchMaterial(game.getSettings().getString("p2-piece-block", "DARK_OAK_FENCE")));
		gameMaterials.put(MaterialType.KING,
				Material.matchMaterial(game.getSettings().getString("king-block", "CREEPER_HEAD")));
		gameMaterials.put(MaterialType.SELECTED,
				Material.matchMaterial(game.getSettings().getString("selected-block", "BIRCH_FENCE")));
	}

	public Material getMaterial(MaterialType m) {
		return gameMaterials.get(m);
	}

	public Direction getDirection() {
		Direction toReturn = (direction == null) ? direction : Direction.X;
		return toReturn;
	}

	public boolean isValidBoardClick(Location l) {
		if (l.getBlockY() > (anchorPoint.getBlockY() + 2) || l.getBlockY() < anchorPoint.getBlockY()) {
			return false;
		}
		GridPoint2D gp = new GridPoint2D(l.getBlockX() - anchorPoint.getBlockX(),
				l.getBlockZ() - anchorPoint.getBlockZ());
		if (!locationPointMap.containsKey(gp)) {
			return false;
		}

		return true;
	}

	private PlayerType turn() {
		return thisGame().whoseTurn();
	}

	public CheckersGame thisGame() {
		return (CheckersGame) game;
	}

	/**
	 * Method to handle block clicked on board
	 * 
	 * @param l
	 * @param p
	 */
	public void blockClicked(Location l, Player p) {
		if (game.getPlayerMap().inverse().get(p) == turn()) {
			GridPoint2D point = getPointFromLocation(l);
			CheckerSquare tempSquare = board[point.getX()][point.getZ()];

			if (!isDoubleJumpMove && tempSquare.getType() == SquareType.PIECE && tempSquare.getPlayer() == turn()) {
				if (fromSquare != null)
					fromSquare.unselect();
				fromSquare = tempSquare;
				fromSquare.select();
			}

			if (tempSquare != null && fromSquare != null && tempSquare.getType() == SquareType.EMPTY) {
				squareToGoTo = tempSquare;
				try {
					move(fromSquare, squareToGoTo);
				} catch (InvalidMoveException e) {
					// game.getMessenger().tell(p, "Invalid Move");
				}
			}
		} else {
			return;
		}
	}

	private void move(CheckerSquare fromSquare, CheckerSquare toSquare) {
		int deltaMove = 0;
		int deltaSide = 0;

		if (direction == Direction.X) {
			deltaMove = toSquare.getPoint().X() - fromSquare.getPoint().X();
			deltaSide = toSquare.getPoint().Z() - fromSquare.getPoint().Z();
		} else {
			deltaMove = toSquare.getPoint().Z() - fromSquare.getPoint().Z();
			deltaSide = toSquare.getPoint().X() - fromSquare.getPoint().X();
		}

		if (!doMove(fromSquare, toSquare, deltaMove, deltaSide)) {
			throw new InvalidMoveException();
		}
		
		fromSquare.transferPiece(toSquare);
		checkForKings();

		this.fromSquare = null;
		this.squareToGoTo = null;

		checkForWinner();
		
		if (Math.abs(deltaMove) == 2 && Math.abs(deltaSide) == 2) {
			if (calculateDoubleJumpPossible(toSquare)) {
				game.tellAllPlayers("ITS A DOUBLE JUMP TURN");
				isDoubleJumpMove = true;
				this.fromSquare = toSquare;
				this.fromSquare.select();
				thisGame().setTurn(turn());
				return;
			}
		}
		
		isDoubleJumpMove = false;
		thisGame().nextTurn();
	}

	private boolean doMove(CheckerSquare fromSquare, CheckerSquare toSquare, int deltaMove, int deltaSide) {
		
		if (!fromSquare.isKing() && deltaMove / turn().getDirection() < 0) {
			return false;
		}

		if (Math.abs(deltaMove) == 2 && Math.abs(deltaSide) == 2) {
			return doJumpMove(fromSquare, toSquare, deltaMove, deltaSide);
		} else if (Math.abs(deltaMove) == 1 && Math.abs(deltaSide) == 1 && !isDoubleJumpMove) {
			return doNormalMove(fromSquare, toSquare, deltaMove, deltaSide);
		}

		return false;
	}

	private boolean doNormalMove(CheckerSquare fromSquare, CheckerSquare toSquare, int deltaMove, int deltaSide) {
		return true;
	}

	private boolean doJumpMove(CheckerSquare fromSquare, CheckerSquare toSquare, int deltaMove, int deltaSide) {
		GridPoint2D middlePoint = null;
		if (direction == Direction.X) {
			middlePoint = fromSquare.getPoint().translate(deltaMove / 2, deltaSide / 2);
		} else {
			middlePoint = fromSquare.getPoint().translate(deltaSide / 2, deltaMove / 2);
		}
		CheckerSquare midSquare = board[middlePoint.X()][middlePoint.Z()];

		if (midSquare.getType() != SquareType.PIECE || midSquare.getPlayer() == turn())
			return false;

		try {
			removePieceFrom(midSquare);
			return true;
		} catch (PieceNotFoundException e) {
			game.getPlugin().getLogger().severe("An error has occured with removing a piece that should exist");
			return false;
		}
	}

	private boolean calculateDoubleJumpPossible(CheckerSquare sq) {
		List<GridPoint2D> pts = new ArrayList<>();
		pts.add(sq.getPoint().translate(2, 2));
		pts.add(sq.getPoint().translate(-2, 2));
		pts.add(sq.getPoint().translate(2, -2));
		pts.add(sq.getPoint().translate(-2, -2));
		boolean toReturn = false;
		
		for (int i = pts.size(); i > 0; i--) {
			GridPoint2D pt = pts.get(i);
			if (pt.X() > 7 || pt.X() < 0 || pt.Z() > 7 || pt.Z() < 0) {
				pts.remove(i);
			} else if (!validJumpMovePoint(sq, pt)) {
				pts.remove(i);
			}
		}
		
		if (pts.size() > 0) {
			return true;
		}
		
		return false;
		
	}
	
	private boolean validJumpMovePoint(CheckerSquare fromSquare, GridPoint2D toPoint) {
		int deltaMove;
		int deltaSide;
		if (direction == Direction.X) {
			deltaMove = toPoint.X() - fromSquare.getPoint().X();
			deltaSide = toPoint.Z() - fromSquare.getPoint().Z();
		} else {
			deltaMove = toPoint.Z() - fromSquare.getPoint().Z();
			deltaSide = toPoint.X() - fromSquare.getPoint().X();
		}
		
		if (!fromSquare.isKing() && deltaMove / turn().getDirection() < 0) {
			return false;
		}
		
		GridPoint2D middlePoint;
		CheckerSquare finalSquare;
		if (direction == Direction.X) {
			middlePoint = fromSquare.getPoint().translate(deltaMove / 2, deltaSide / 2);
			finalSquare = board[fromSquare.getPoint().translate(deltaMove, deltaSide).X()][fromSquare.getPoint().translate(deltaMove, deltaSide).Z()];
		} else {
			middlePoint = fromSquare.getPoint().translate(deltaSide / 2, deltaMove / 2);
			finalSquare = board[fromSquare.getPoint().translate(deltaSide, deltaMove).X()][fromSquare.getPoint().translate(deltaSide, deltaMove).Z()];

		}
		CheckerSquare midSquare = board[middlePoint.X()][middlePoint.Z()];

		if (midSquare.getType() != SquareType.PIECE || midSquare.getPlayer() == turn())
			return false;
		
		if (finalSquare.getType() != SquareType.EMPTY)
			return false;
		
		return true;
	}

	private void removePieceFrom(CheckerSquare square) {
		CheckerPiece piece = square.removePiece();
		Boolean removedPiece = false;

		if (piece == null)
			throw new PieceNotFoundException();

		if (turn() == PlayerType.PLAYER_ONE) {
			removedPiece = p2Pieces.remove(piece);
		} else {
			removedPiece = p1Pieces.remove(piece);
		}

		if (!removedPiece)
			throw new PieceNotFoundException();

	}

	public void checkForWinner() {
		if (p1Pieces.size() == 0) {
			game.tellAllPlayers("Player 2 wins!");
			game.end();
		} else if (p2Pieces.size() == 0) {
			game.tellAllPlayers("Player 1 wins!");
			game.end();
		}

	}

	public void checkForKings() {
		for (CheckerPiece p : p1Pieces) {
			p.setKingIfKing(direction);
		}
		for (CheckerPiece p : p2Pieces) {
			p.setKingIfKing(direction);
		}
	}

	public CheckerSquare getSquareAt(Location l) {
		GridPoint2D p = getPointFromLocation(l);
		if (p != null) {
			return getSquareAt(p);
		}
		return null;
	}

	public CheckerSquare getSquareAt(GridPoint2D p) {
		return board[p.X()][p.Z()];
	}

	public Location getLocationFromPoint(GridPoint2D p) {
		return locationPointMap.get(p);
	}

	public GridPoint2D getPointFromLocation(Location l) {
		// l.setY(anchorPoint.getBlockY());
		// return locationPointMap.inverse().get(l);
		return new GridPoint2D(l.getBlockX() - anchorPoint.getBlockX(), l.getBlockZ() - anchorPoint.getBlockZ());
	}

	public void clearMaps() {
		locationPointMap.clear();
	}
	
	public boolean isDoubleJumpTurn() {
		return this.isDoubleJumpMove;
	}

}
