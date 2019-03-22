package com.cptingle.BoardGames.region;

import static com.cptingle.BoardGames.util.config.ConfigUtils.makeSection;
import static com.cptingle.BoardGames.util.config.ConfigUtils.parseLocation;
import static com.cptingle.BoardGames.util.config.ConfigUtils.setLocation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.cptingle.BoardGames.BoardGames;
import com.cptingle.BoardGames.framework.Game;
import com.cptingle.BoardGames.util.BGUtils;
import com.cptingle.BoardGames.util.Enums;


public class GameRegion {

	private Game game;
	private World world;

	private Location lastP1, lastP2, lastB1, lastB2;
	private Location p1, p2, b1, b2, p1Spawn, p2Spawn;
	private Map<String, Location> otherCoords;

	private boolean setup;

	private ConfigurationSection coords;
	private ConfigurationSection oCoords;

	public GameRegion(ConfigurationSection section, Game game) {
		this.game = game;
		refreshWorld();

		this.coords = makeSection(section, "coords");
		this.oCoords = makeSection(coords, "other");
		
		otherCoords = new HashMap<>();

		reloadAll();
	}

	public void reloadAll() {
		reloadRegion();
		reloadSpawns();
		//reloadOtherCoords();
		verifyData();
	}

	public void refreshWorld() {
		this.world = game.getWorld();
	}

	public void reloadRegion() {
		p1 = parseLocation(coords, "p1", world);
		p2 = parseLocation(coords, "p2", world);

		b1 = parseLocation(coords, "b1", world);
		b2 = parseLocation(coords, "b2", world);
	}

	public void reloadSpawns() {
		p1Spawn = parseLocation(coords, "p1Spawn", world);
		p2Spawn = parseLocation(coords, "p2Spawn", world);
	}
	
	public void reloadOtherCoords() {
		otherCoords.clear();
		for (String key: oCoords.getKeys(true)) {
			Location loc = parseLocation(oCoords, key, world);
			otherCoords.put(key, loc);
		}
	}

	public void verifyData() {
		setup = (p1 != null && p2 != null && b1 != null && b2 != null && p1Spawn != null && p2Spawn != null);
	}

	public void checkData(BoardGames plugin, CommandSender s, boolean ready, boolean region, boolean spawns) {
		verifyData();

		List<String> list = new ArrayList<>();

		// Region pts
		if (region) {
			if (p1 == null)
				list.add("p1");
			if (p2 == null)
				list.add("p2");
			if (b1 == null)
				list.add("b1");
			if (b2 == null)
				list.add("b2");
			if (!list.isEmpty()) {
				game.getGlobalMessenger().tell(s, "Missing region points: " + BGUtils.listToString(list, plugin));
				list.clear();
			}
		}

		// Spawns
		if (spawns) {
			if (p1Spawn == null)
				list.add("p2spawn");
			if (p2Spawn == null)
				list.add("p2spawn");
			if (!list.isEmpty()) {
				game.getGlobalMessenger().tell(s, "Missing spawn points: " + BGUtils.listToString(list, plugin));
				list.clear();
			}
		}

		// Ready?
		if (ready && setup) {
			game.getGlobalMessenger().tell(s, "Game is ready to be used!");
		}
	}

	public boolean isDefined() {
		return (p1 != null && p2 != null);
	}

	public boolean isBoardDefined() {
		return (b1 != null && b2 != null);
	}

	public boolean isSetup() {
		return setup;
	}

	public boolean isSpawn(Location l) {
		return (l.equals(p1Spawn) || l.equals(p2Spawn));
	}

	public boolean contains(Location l) {
		if (!l.getWorld().getName().equals(world.getName()) || !isDefined()) {
			return false;
		}

		int x = l.getBlockX();
		int y = l.getBlockY();
		int z = l.getBlockZ();

		// Returns false if the location is outside of the region.
		return ((x >= p1.getBlockX() && x <= p2.getBlockX()) && (z >= p1.getBlockZ() && z <= p2.getBlockZ())
				&& (y >= p1.getBlockY() && y <= p2.getBlockY()));
	}

	public boolean boardContains(Location l) {
		if (!l.getWorld().getName().equals(world.getName()) || !isDefined()) {
			return false;
		}

		int x = l.getBlockX();
		int y = l.getBlockY();
		int z = l.getBlockZ();

		return ((x >= b1.getBlockX() && x <= b2.getBlockX()) && (z >= b1.getBlockZ() && z <= b2.getBlockZ())
				&& (y >= b1.getBlockY() && y <= b2.getBlockY() + 2));
	}

	public boolean contains(Location l, int radius) {
		if (!l.getWorld().getName().equals(world.getName()) || !isDefined()) {
			return false;
		}

		int x = l.getBlockX();
		int y = l.getBlockY();
		int z = l.getBlockZ();

		return ((x + radius >= p1.getBlockX() && x - radius <= p2.getBlockX())
				&& (z + radius >= p1.getBlockZ() && z - radius <= p2.getBlockZ())
				&& (y + radius >= p1.getBlockY() && y - radius <= p2.getBlockY()));
	}

	// Region expand
	public void expandUp(int amount) {
		int x = p2.getBlockX();
		int y = Math.min(p2.getWorld().getMaxHeight(), p2.getBlockY() + amount);
		int z = p2.getBlockZ();
		setSaveReload(coords, "p2", p2.getWorld(), x, y, z);
	}

	public void expandDown(int amount) {
		int x = p1.getBlockX();
		int y = Math.max(0, p1.getBlockY() - amount);
		int z = p1.getBlockZ();
		setSaveReload(coords, "p1", p1.getWorld(), x, y, z);
	}

	public void expandP1(int dx, int dz) {
		int x = p1.getBlockX() - dx;
		int y = p1.getBlockY();
		int z = p1.getBlockZ() - dz;
		setSaveReload(coords, "p1", p1.getWorld(), x, y, z);
	}

	public void expandP2(int dx, int dz) {
		int x = p2.getBlockX() + dx;
		int y = p2.getBlockY();
		int z = p2.getBlockZ() + dz;
		setSaveReload(coords, "p2", p2.getWorld(), x, y, z);
	}

	public void expandOut(int amount) {
		expandP1(amount, amount);
		expandP2(amount, amount);
	}

	private void setSaveReload(ConfigurationSection section, String key, World w, double x, double y, double z) {
		Location loc = new Location(w, x, y, z);
		setLocation(section, key, loc);
		save();
		reloadRegion();
	}

	public void fixRegion() {
		fix("p1", "p2");
	}

	private void fix(String location1, String location2) {
		Location loc1 = parseLocation(coords, location1, world);
		Location loc2 = parseLocation(coords, location2, world);

		if (loc1 == null || loc2 == null) {
			return;
		}

		boolean modified = false;

		if (loc1.getX() > loc2.getX()) {
			double tmp = loc1.getX();
			loc1.setX(loc2.getX());
			loc2.setX(tmp);
			modified = true;
		}

		if (loc1.getZ() > loc2.getZ()) {
			double tmp = loc1.getZ();
			loc1.setZ(loc2.getZ());
			loc2.setZ(tmp);
			modified = true;
		}

		if (loc1.getY() > loc2.getY()) {
			double tmp = loc1.getY();
			loc1.setY(loc2.getY());
			loc2.setY(tmp);
			modified = true;
		}

		if (!game.getWorld().getName().equals(world.getName())) {
			game.setWorld(world);
			modified = true;
		}

		if (!modified) {
			return;
		}

		setLocation(coords, location1, loc1);
		setLocation(coords, location2, loc2);
		save();
	}

	public List<Chunk> getChunks() {
		List<Chunk> result = new ArrayList<>();

		if (p1 == null || p2 == null) {
			return result;
		}

		Chunk c1 = world.getChunkAt(p1);
		Chunk c2 = world.getChunkAt(p2);

		for (int i = c1.getX(); i <= c2.getX(); i++) {
			for (int j = c1.getZ(); j <= c2.getZ(); j++) {
				result.add(world.getChunkAt(i, j));
			}
		}

		return result;
	}

	public Location getP1Spawn() {
		return p1Spawn;
	}

	public Location getP2Spawn() {
		return p2Spawn;
	}

	public Location getB1() {
		return b1;
	}

	public Location getB2() {
		return b2;
	}

	public void set(RegionPoint point, Location loc) {
		// Act based on the point
		switch (point) {
		case P1:
		case P2:
		case B1:
		case B2:
			setPoint(point, loc);
			return;
		case P1SPAWN:
		case P2SPAWN:
		case SPAWN:
			setSpawn(point, loc);
			return;
		}

		throw new IllegalArgumentException("Invalid region point!");
	}

	private void setPoint(RegionPoint point, Location l) {
		// Lower and upper locations
		RegionPoint r1, r2;
		Location lower, upper;

		/*
		 * Initialize the bounds.
		 *
		 * To allow users to set a region point without paying attention to the 'fixed'
		 * points, we continuously store the previously stored location for the given
		 * point. These location references are only ever overwritten when using the set
		 * commands, and remain fully decoupled from the 'fixed' points.
		 * 
		 * Effectively, the config-file and region store 'fixed' locations that allow
		 * fast membership tests, but the region also stores the 'unfixed' locations for
		 * a more intuitive setup process.
		 */
		switch (point) {
		case P1:
			lastP1 = l.clone();
			lower = lastP1.clone();
			upper = (lastP2 != null ? lastP2.clone() : p2);
			r1 = RegionPoint.P1;
			r2 = RegionPoint.P2;
			break;
		case P2:
			lastP2 = l.clone();
			lower = (lastP1 != null ? lastP1.clone() : p1);
			upper = lastP2.clone();
			r1 = RegionPoint.P1;
			r2 = RegionPoint.P2;
			break;
		case B1:
			lastB1 = l.clone();
			lower = lastB1.clone();
			upper = (lastB2 != null ? lastB2.clone() : b2);
			r1 = RegionPoint.B1;
			r2 = RegionPoint.B2;
			break;
		case B2:
			lastB2 = l.clone();
			lower = (lastB1 != null ? lastB1.clone() : b1);
			upper = lastB2.clone();
			r1 = RegionPoint.B1;
			r2 = RegionPoint.B2;
			break;
		default:
			lower = upper = null;
			r1 = r2 = null;
		}

		// Min-max if both locations are non-null
		if (lower != null && upper != null) {
			double tmp;
			if (lower.getX() > upper.getX()) {
				tmp = lower.getX();
				lower.setX(upper.getX());
				upper.setX(tmp);
			}
			if (lower.getY() > upper.getY()) {
				tmp = lower.getY();
				lower.setY(upper.getY());
				upper.setY(tmp);
			}
			if (lower.getZ() > upper.getZ()) {
				tmp = lower.getZ();
				lower.setZ(upper.getZ());
				upper.setZ(tmp);
			}
		}

		// Set the coords and save
		if (lower != null)
			setLocation(coords, r1.name().toLowerCase(), lower);
		if (upper != null)
			setLocation(coords, r2.name().toLowerCase(), upper);
		save();

		// Reload regions and verify data
		reloadRegion();
		verifyData();
	}

	public void set(String point, Location loc) {
		// Get the region point enum
		RegionPoint rp = Enums.getEnumFromString(RegionPoint.class, point);
		if (rp == null)
			throw new IllegalArgumentException("Invalid region point '" + point + "'");

		// Then delegate
		set(rp, loc);
	}
	
	public void setOtherPoint(String point, Location loc) {
		if (point == null || loc == null || point.equals(""))
				throw new IllegalArgumentException("Invalid points and/or location");
		
		setLocation(oCoords, point, loc);
		otherCoords.put(point, loc);
		save();
	}
	
	public Location getOtherPoint(String point) {
		if (point == null || point.equals(""))
			throw new IllegalArgumentException("Point can not be null or empty");
		reloadOtherCoords();
		return otherCoords.get(point);
	}
	
	public Map<String, Location> getOtherCoords() {
		reloadOtherCoords();
		return otherCoords;
	}

	public void setSpawn(RegionPoint point, Location l) {
		// Set the point and save
		setLocation(coords, point.toString(), l);
		save();

		// Then reload warps
		reloadSpawns();
	}

	public void save() {
		game.getPlugin().saveConfig();
	}

	public void showRegion(Player p) {
		if (!isDefined()) {
			return;
		}
		showBlocks(p, getFramePoints(p1, p2));
	}

	public void showBoardRegion(Player p) {
		if (!isBoardDefined()) {
			return;
		}
		showBlocks(p, getFramePoints(b1, b2));
	}

	public void showSpawns(Player p) {
		if (p1Spawn != null) {
			showBlock(p, p1Spawn, Material.BLUE_WOOL);
		}
		if (p2Spawn != null) {
			showBlock(p, p2Spawn, Material.RED_WOOL);
		}
		if (p1Spawn == null && p2Spawn == null)
			game.getGlobalMessenger().tell(p, "NO SPAWNS");
	}

	public void showBlock(final Player p, final Location loc, final Material m) {
		game.scheduleTask(new Runnable() {
			@Override
			public void run() {
				p.sendBlockChange(loc, m.createBlockData());
				game.scheduleTask(new Runnable() {
					@Override
					public void run() {
						if (!p.isOnline())
							return;

						Block b = loc.getBlock();
						p.sendBlockChange(loc, b.getBlockData());
					}
				}, 100);
			}
		}, 0);
	}

	private void showBlocks(final Player p, final Collection<Location> points) {
		game.scheduleTask(new Runnable() {
			@Override
			public void run() {
				// Grab all the blocks, and send block change events.
				final Map<Location, BlockState> blocks = new HashMap<>();
				for (Location l : points) {
					Block b = l.getBlock();
					blocks.put(l, b.getState());
					p.sendBlockChange(l, Material.RED_WOOL.createBlockData());
				}
				game.scheduleTask(new Runnable() {
					public void run() {
						// If the player isn't online, just forget it.
						if (!p.isOnline()) {
							return;
						}

						// Send block "restore" events.
						for (Map.Entry<Location, BlockState> entry : blocks.entrySet()) {
							Location l = entry.getKey();
							BlockState b = entry.getValue();

							p.sendBlockChange(l, b.getBlockData());
						}
					}
				}, 100);
			}
		}, 0);
	}

	private List<Location> getFramePoints(Location loc1, Location loc2) {
		List<Location> result = new ArrayList<>();
		int x1 = loc1.getBlockX();
		int y1 = loc1.getBlockY();
		int z1 = loc1.getBlockZ();
		int x2 = loc2.getBlockX();
		int y2 = loc2.getBlockY();
		int z2 = loc2.getBlockZ();

		for (int i = x1; i <= x2; i++) {
			result.add(world.getBlockAt(i, y1, z1).getLocation());
			result.add(world.getBlockAt(i, y1, z2).getLocation());
			result.add(world.getBlockAt(i, y2, z1).getLocation());
			result.add(world.getBlockAt(i, y2, z2).getLocation());
		}

		for (int j = y1; j <= y2; j++) {
			result.add(world.getBlockAt(x1, j, z1).getLocation());
			result.add(world.getBlockAt(x1, j, z2).getLocation());
			result.add(world.getBlockAt(x2, j, z1).getLocation());
			result.add(world.getBlockAt(x2, j, z2).getLocation());
		}

		for (int k = z1; k <= z2; k++) {
			result.add(world.getBlockAt(x1, y1, k).getLocation());
			result.add(world.getBlockAt(x1, y2, k).getLocation());
			result.add(world.getBlockAt(x2, y1, k).getLocation());
			result.add(world.getBlockAt(x2, y2, k).getLocation());
		}
		return result;
	}

}
