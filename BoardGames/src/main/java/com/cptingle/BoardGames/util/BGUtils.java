package com.cptingle.BoardGames.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.cptingle.BoardGames.BoardGames;
import com.cptingle.BoardGames.GameMaster;
import com.cptingle.BoardGames.framework.Game;
import com.cptingle.BoardGames.messaging.Msg;
import com.cptingle.BoardGames.region.GameRegion;

public class BGUtils {
	public static final String sep = File.separator;

	/**
	 * Turn a list into a space-separated string-representation of the list.
	 */
	public static <E> String listToString(Collection<E> list, boolean none, BoardGames plugin) {
		if (list == null || list.isEmpty()) {
			return (none ? Msg.MISC_NONE.toString() : "");
		}

		StringBuffer buffy = new StringBuffer();
		int trimLength = 0;

		E type = list.iterator().next();
		if (type instanceof Player) {
			for (E e : list) {
				buffy.append(((Player) e).getName());
				buffy.append(" ");
			}
		} else if (type instanceof ItemStack) {
			trimLength = 2;
			ItemStack stack;
			for (E e : list) {
				stack = (ItemStack) e;
				buffy.append(stack.getType().toString().toLowerCase());
				buffy.append(":");
				buffy.append(stack.getAmount());
				buffy.append(", ");
			}
		} else {
			for (E e : list) {
				buffy.append(e.toString());
				buffy.append(" ");
			}
		}
		return buffy.toString().substring(0, buffy.length() - trimLength);
	}

	public static <E> String listToString(Collection<E> list, JavaPlugin plugin) {
		return listToString(list, true, (BoardGames) plugin);
	}

	public static boolean autogenerate(Location loc, int radius, String name, BoardGames plugin) {
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
		} catch (Exception e) {
			e.printStackTrace();
			plugin.getLogger().warning("Couldn't create backup file. Aborting auto-generate...");
			return false;
		}

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
        for (int i = x1; i <= x2; i++)
        {
            for (int k = z1; k <= z2; k++)
            {
                world.getBlockAt(i,y1,k).setType(Material.SANDSTONE);
            }
        }
        
        // Make a hippie roof.
        for (int i = x1; i <= x2; i++)
        {
            for (int k = z1; k <= z2; k++)
                world.getBlockAt(i,y2,k).setType(Material.GLASS);
        }
        
        // Monster bulldoze
        for (int i = x1+1; i < x2; i++) {
            for (int j = y1+1; j < y2; j++) {
                for (int k = z1+1; k < z2; k++) {
                    world.getBlockAt(i,j,k).setType(Material.AIR);
                }
            }
        }
        
        for (int r = bx1; r <= bx2; r++) {
        		for (int c = bz1; c <= bz2; c++) {
        			int bXPos = r-bx1;
        			int bZPos = c-bz1;
        			if (((bXPos % 2 == 0) && (bZPos %2 == 1)) || ((bXPos % 2 == 1) && (bZPos %2 == 0))) {
        				//world.getBlockAt(r,by, c).setType(Material.BLACK_CONCRETE);
    					if ((r-bx1) < 3) {
    					//	world.getBlockAt(r,by + 1, c).setType(Material.ACACIA_FENCE);
    					} else if ((r-bx1) > 4) {
    					//	world.getBlockAt(r,by + 1, c).setType(Material.DARK_OAK_FENCE);
    					}
    				} else {
    					//world.getBlockAt(r,by,c).setType(Material.RED_CONCRETE);
    				}
        		}
        }
        
     // Set up the Region points. 
        GameRegion region = game.getRegion();
        region.set("p1", new Location(world, x1, y1, z1));
        region.set("p2", new Location(world, x2, y2+1, z2));
        
        region.set("b1", new Location(world, bx1, by, bz1));
        region.set("b2", new Location(world, bx2, by, bz1));
        
        region.set("p1Spawn", new Location(world, loc.getX() - 4, y1+1, loc.getZ()));
        region.set("p2Spawn", new Location(world, loc.getX() + 4, y1+1, loc.getZ()));

        region.save();
        
        gm.reloadConfig();
        return true;
	}
	
	public static boolean autoDegenerate(String name, BoardGames plugin, boolean error) {
		File file = new File("plugins" + sep + "BoardGames" + sep + "agbackup" + sep + name + ".tmp");
        HashMap<EntityPosition,Material> preciousPatch;
        try
        {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            preciousPatch = (HashMap<EntityPosition,Material>) ois.readObject();
            ois.close();
        }
        catch (Exception e)
        {
            if (error) plugin.getLogger().warning("Couldn't find backup file for game '" + name + "'");
            return false;
        }
        
        World world = plugin.getServer().getWorld(preciousPatch.keySet().iterator().next().getWorld());
        
        for (Map.Entry<EntityPosition,Material> entry : preciousPatch.entrySet())
        {
            world.getBlockAt(entry.getKey().getLocation(world)).setType(entry.getValue());
        }

        plugin.getConfig().set("games." + name, null);
        plugin.saveConfig();
        
        file.delete();
        
        plugin.getGameMaster().reloadConfig();
        return true;
	}
}
