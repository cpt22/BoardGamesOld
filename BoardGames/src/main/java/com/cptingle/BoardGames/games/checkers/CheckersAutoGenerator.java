package com.cptingle.BoardGames.games.checkers;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;

import com.cptingle.BoardGames.BoardGames;
import com.cptingle.BoardGames.GameMaster;
import com.cptingle.BoardGames.framework.Game;
import com.cptingle.BoardGames.games.AutoGenerator;
import com.cptingle.BoardGames.region.GameRegion;

public class CheckersAutoGenerator {

	public static boolean autogenerate(Location loc, int radius, String name, BoardGames plugin) {
		AutoGenerator.storePatch(loc, radius, 9, 1, name, plugin);
		GameMaster gm = plugin.getGameMaster();
		World world = loc.getWorld();
		Game game = gm.createGameNode(name, "checkers", world);

		// Get the bounds.
		int x1 = (int) loc.getX() - radius;
		int x2 = (int) loc.getX() + radius;
		int y1 = (int) loc.getY() - 1;
		int y2 = (int) loc.getY() + 9;
		int z1 = (int) loc.getZ() - radius;
		int z2 = (int) loc.getZ() + radius;

		int bx1 = (int) loc.getX() - 3;
		int bx2 = (int) loc.getX() + 4;
		int by = (int) loc.getY() - 1;
		int bz1 = (int) loc.getZ() - 3;
		int bz2 = (int) loc.getZ() + 4;


		// Build some walls
		for (int i = x1; i < x2; i++) {
			for (int j = y1; j <= y2; j++) {
				world.getBlockAt(i, j, z1).setType(Material.STONE_BRICKS);
				world.getBlockAt(i, j, z2).setType(Material.STONE_BRICKS);
			}
		}
		for (int k = z1; k <= z2; k++) {
			for (int j = y1; j <= y2; j++) {
				world.getBlockAt(x1, j, k).setType(Material.STONE_BRICKS);
				world.getBlockAt(x2, j, k).setType(Material.STONE_BRICKS);
			}
		}

		// Build a monster floor, and some Obsidian foundation.
		for (int i = x1; i <= x2; i++) {
			for (int k = z1; k <= z2; k++) {
				world.getBlockAt(i, y1, k).setType(Material.SANDSTONE);
			}
		}

		// Make a hippie roof.
		for (int i = x1; i <= x2; i++) {
			for (int k = z1; k <= z2; k++)
				world.getBlockAt(i, y2, k).setType(Material.GLASS);
		}

		// Monster bulldoze
		for (int i = x1 + 1; i < x2; i++) {
			for (int j = y1 + 1; j < y2; j++) {
				for (int k = z1 + 1; k < z2; k++) {
					world.getBlockAt(i, j, k).setType(Material.AIR);
				}
			}
		}

		for (int r = bx1; r <= bx2; r++) {
			for (int c = bz1; c <= bz2; c++) {
				int bXPos = r - bx1;
				int bZPos = c - bz1;
				if (((bXPos % 2 == 0) && (bZPos % 2 == 1)) || ((bXPos % 2 == 1) && (bZPos % 2 == 0))) {
					// world.getBlockAt(r,by, c).setType(Material.BLACK_CONCRETE);
					if ((r - bx1) < 3) {
						// world.getBlockAt(r,by + 1, c).setType(Material.ACACIA_FENCE);
					} else if ((r - bx1) > 4) {
						// world.getBlockAt(r,by + 1, c).setType(Material.DARK_OAK_FENCE);
					}
				} else {
					// world.getBlockAt(r,by,c).setType(Material.RED_CONCRETE);
				}
			}
		}

		// Set up the Region points.
		GameRegion region = game.getRegion();
		region.set("r1", new Location(world, x1, y1, z1));
		region.set("r2", new Location(world, x2, y2 + 1, z2));

		region.set("board", new Location(world, bx1, by, bz1), BlockFace.EAST);
		region.set("p1-spawn", new Location(world, loc.getX() - 4, y1 + 1, loc.getZ(), -90.0f, 0.0f));
		region.set("p2-spawn", new Location(world, loc.getX() + 4, y1 + 1, loc.getZ(), 90.0f, 0.0f));

		region.save();

		gm.reloadConfig();
		return true;
	}

}
