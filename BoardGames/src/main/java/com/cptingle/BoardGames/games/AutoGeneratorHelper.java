package com.cptingle.BoardGames.games;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import com.cptingle.BoardGames.BoardGames;
import com.cptingle.BoardGames.util.EntityPosition;

public class AutoGeneratorHelper {
	public static final String sep = File.separator;

	/*
	 * protected BoardGames plugin; protected GameMaster gm;
	 * 
	 * public AutoGenerator(BoardGames plugin) { this.plugin = plugin; this.gm =
	 * plugin.getGameMaster(); }
	 */

	/**
	 * Wrapper for store Patch
	 * 
	 * @param loc    - Player location
	 * @param radius - Radius around player
	 * @param height - how far up and down
	 * @param name   - name of game
	 * @param plugin
	 */
	public static boolean storePatch(Location loc, int radius, int height, String name, BoardGames plugin) {
		return storePatch(loc, radius, height, height, name, plugin);
	}

	/**
	 * Store region data into file
	 * 
	 * @param loc    - Player location
	 * @param radius - Radius around player
	 * @param up     - how high up
	 * @param down   - how low
	 * @param name   - name of game
	 * @param plugin
	 * @return
	 */
	public static boolean storePatch(Location loc, int radius, int up, int down, String name, BoardGames plugin) {
		// GameMaster gm = plugin.getGameMaster();
		World world = loc.getWorld();
		// Get the bounds.
		int x1 = (int) loc.getX() - radius;
		int x2 = (int) loc.getX() + radius;
		int y1 = (int) loc.getY() - down;
		int y2 = (int) loc.getY() + up;
		int z1 = (int) loc.getZ() - radius;
		int z2 = (int) loc.getZ() + radius;

		// Save the precious patch
		HashMap<EntityPosition, Material> preciousPatch = new HashMap<>();
		Location lo;
		Material mat;
		for (int i = x1; i <= x2; i++) {
			for (int j = y1; j <= y2; j++) {
				for (int k = z1; k <= z2; k++) {
					lo = world.getBlockAt(i, j, k).getLocation();
					mat = world.getBlockAt(i, j, k).getType();
					preciousPatch.put(new EntityPosition(lo), mat);
				}
			}
		}
		try {
			new File("plugins" + sep + "BoardGames" + sep + "agbackup").mkdir();
			FileOutputStream fos = new FileOutputStream(
					"plugins" + sep + "BoardGames" + sep + "agbackup" + sep + name + ".tmp");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(preciousPatch);
			oos.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			plugin.getLogger().warning("Couldn't create backup file. Aborting auto-generate...");
			return false;
		}
	}

	/**
	 * Auto Degenerate named game
	 * 
	 * @param name
	 * @param error
	 * @return
	 */
	public static boolean autoDegenerate(String name, BoardGames plugin, boolean error) {
		File file = new File("plugins" + sep + "BoardGames" + sep + "agbackup" + sep + name + ".tmp");
		HashMap<EntityPosition, Material> preciousPatch;
		try {
			FileInputStream fis = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(fis);
			preciousPatch = (HashMap<EntityPosition, Material>) ois.readObject();
			ois.close();
		} catch (Exception e) {
			if (error)
				plugin.getLogger().warning("Couldn't find backup file for game '" + name + "'");
			return false;
		}

		World world = plugin.getServer().getWorld(preciousPatch.keySet().iterator().next().getWorld());

		for (Map.Entry<EntityPosition, Material> entry : preciousPatch.entrySet()) {
			world.getBlockAt(entry.getKey().getLocation(world)).setType(entry.getValue());
		}

		plugin.getConfig().set("games." + name, null);
		plugin.saveConfig();

		file.delete();

		plugin.getGameMaster().reloadConfig();
		return true;
	}
}
