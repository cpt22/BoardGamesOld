package com.cptingle.BoardGames.region;

import static com.cptingle.BoardGames.util.config.ConfigUtils.makeSection;
import static com.cptingle.BoardGames.util.config.ConfigUtils.parseDirection;
import static com.cptingle.BoardGames.util.config.ConfigUtils.parseLocation;
import static com.cptingle.BoardGames.util.config.ConfigUtils.setDirection;
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
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.cptingle.BoardGames.framework.Game;

public class GameRegion {

	private Game game;
	private World world;

	private Location lastR1, lastR2;
	private Location r1, r2;
	private Map<RegionPoint, Location> coordsMap;
	private Map<RegionPoint, BlockFace> dirsMap;
	private Map<RegionPoint, Location> spawnsMap;

	private boolean setup;

	private ConfigurationSection coords;
	private ConfigurationSection locations;
	private ConfigurationSection directions;
	private ConfigurationSection spawns;

	public GameRegion(ConfigurationSection section, Game game) {
		this.game = game;
		refreshWorld();

		this.coords = makeSection(section, "coords");
		this.locations = makeSection(coords, "locations");
		this.directions = makeSection(coords, "directions");
		this.spawns = makeSection(coords, "spawns");

		this.setup = false;

		coordsMap = new HashMap<>();
		dirsMap = new HashMap<>();
		spawnsMap = new HashMap<>();

		reloadAll();
	}

	/**
	 * Refreshes world from configuration
	 */
	public void refreshWorld() {
		this.world = game.getWorld();
	}

	/**
	 * Reload all points from configuration
	 */
	public void reloadAll() {
		reloadRegion();
		reloadCoords();
		reloadDirections();
		reloadSpawns();

		verifyData();
	}

	/**
	 * Reloads region from configuration
	 */
	public void reloadRegion() {
		r1 = parseLocation(coords, "r1", world);
		r2 = parseLocation(coords, "r2", world);
	}

	/**
	 * Loads other coordinates from configuration
	 */
	public void reloadCoords() {
		coordsMap.clear();
		for (String key : locations.getKeys(false)) {
			RegionPoint k = game.getRegionPointFromString(key);
			if (k != null) {
				coordsMap.put(k, parseLocation(locations, key, world));
			}
		}
	}

	/**
	 * Reload directions and match to a point
	 */
	public void reloadDirections() {
		dirsMap.clear();
		for (String key : directions.getKeys(false)) {
			RegionPoint k = game.getRegionPointFromString(key);
			if (k != null) {
				dirsMap.put(k, parseDirection(directions, key));
			}
		}
	}

	/**
	 * Reload spawnpoints from configuration
	 */
	public void reloadSpawns() {
		spawnsMap.clear();
		for (String key : spawns.getKeys(false)) {
			RegionPoint k = game.getRegionPointFromString(key);
			if (k != null) {
				spawnsMap.put(k, parseLocation(spawns, key, world));
			}
		}
	}

	/**
	 * Verify data and check if the region is fully setup
	 */
	public void verifyData() {
		boolean coordsPresent = true;
		RegionPoint[] coordTypes = game.getPointTypesWithCategory(PointCategory.POINT);
		for (RegionPoint pt : coordTypes) {
			if (coordsMap.get(pt) == null)
				coordsPresent = false;
		}

		boolean coordsDirsPresent = true;
		RegionPoint[] coordDirTypes = game.getPointTypesWithCategory(PointCategory.POINT_DIR);
		for (RegionPoint pt : coordDirTypes) {
			if (dirsMap.get(pt) == null || coordsMap.get(pt) == null)
				coordsDirsPresent = false;
		}

		boolean coordsCuboidPresent = true;
		RegionPoint[] coordCuboidTypes = game.getPointTypesWithCategory(PointCategory.POINT_CUBOID);
		for (RegionPoint pt : coordCuboidTypes) {
			if (coordsMap.get(pt) == null)
				coordsCuboidPresent = false;
		}

		boolean spawnsPresent = spawnsValid();

		setup = (r1 != null) && (r2 != null) && coordsPresent && coordsDirsPresent && coordsCuboidPresent
				&& spawnsPresent;
	}

	/**
	 * Validate spawns
	 * 
	 * @return
	 */
	public boolean spawnsValid() {
		boolean spawnsPresent = true;
		RegionPoint[] spawnTypes = game.getPointTypesWithCategory(PointCategory.SPAWN);
		for (RegionPoint pt : spawnTypes) {
			if (spawnsMap.get(pt) == null)
				spawnsPresent = false;
		}

		return spawnsPresent;
	}

	/**
	 * Get all points that are yet to be set
	 * 
	 * @return
	 */
	public List<String> getAllMissing() {
		List<String> missing = new ArrayList<>();

		List<RegionPoint> coordTypes = new ArrayList<>();
		for (RegionPoint rp : game.getPointTypesWithCategory(PointCategory.POINT))
			coordTypes.add(rp);
		for (RegionPoint rp : game.getPointTypesWithCategory(PointCategory.POINT_DIR))
			coordTypes.add(rp);
		for (RegionPoint rp : game.getPointTypesWithCategory(PointCategory.POINT_CUBOID))
			coordTypes.add(rp);

		for (RegionPoint pt : coordTypes) {
			if (coordsMap.get(pt) == null)
				missing.add(pt.commonName());
		}

		RegionPoint[] spawnTypes = game.getPointTypesWithCategory(PointCategory.SPAWN);
		for (RegionPoint pt : spawnTypes) {
			if (spawnsMap.get(pt) == null)
				missing.add(pt.commonName());
		}

		// Return all missing points
		return missing;
	}

	/**
	 * Gets if protected region is defined
	 * 
	 * @return
	 */
	public boolean isDefined() {
		return (r1 != null && r2 != null);
	}

	/**
	 * Gets if the region is fully set up
	 */
	public boolean isSetup() {
		return setup;
	}

	/**
	 * Get a spawn for a given {@link PointType}
	 * 
	 * @param pt
	 * @return
	 */
	public Location getSpawn(RegionPoint pt) {
		return spawnsMap.get(pt);
	}

	/**
	 * Gets a point for a given {@link PointType}
	 * 
	 * @param pt
	 * @return
	 */
	public Location getPoint(RegionPoint pt) {
		return coordsMap.get(pt);
	}

	/**
	 * Gets a direction associated with a given point
	 * 
	 * @param pt
	 * @return
	 */
	public BlockFace getDirection(RegionPoint pt) {
		return dirsMap.get(pt);
	}

	/**
	 * Gets if a location is contained within the games protected region
	 * 
	 * @param l
	 * @return
	 */
	public boolean contains(Location l) {
		if (!l.getWorld().getName().equals(world.getName()) || !isDefined()) {
			return false;
		}

		int x = l.getBlockX();
		int y = l.getBlockY();
		int z = l.getBlockZ();

		// Returns false if the location is outside of the region.
		return ((x >= r1.getBlockX() && x <= r2.getBlockX()) && (z >= r1.getBlockZ() && z <= r2.getBlockZ())
				&& (y >= r1.getBlockY() && y <= r2.getBlockY()));
	}

	/**
	 * Gets if a location is contained within a radius of the games protected region
	 * 
	 * @param l
	 * @param radius
	 * @return
	 */
	public boolean contains(Location l, int radius) {
		if (!l.getWorld().getName().equals(world.getName()) || !isDefined()) {
			return false;
		}

		int x = l.getBlockX();
		int y = l.getBlockY();
		int z = l.getBlockZ();

		return ((x + radius >= r1.getBlockX() && x - radius <= r2.getBlockX())
				&& (z + radius >= r1.getBlockZ() && z - radius <= r2.getBlockZ())
				&& (y + radius >= r1.getBlockY() && y - radius <= r2.getBlockY()));
	}

	// Region expand
	public void expandUp(int amount) {
		int x = r2.getBlockX();
		int y = Math.min(r2.getWorld().getMaxHeight(), r2.getBlockY() + amount);
		int z = r2.getBlockZ();
		setSaveReload(coords, "r2", r2.getWorld(), x, y, z);
	}

	public void expandDown(int amount) {
		int x = r1.getBlockX();
		int y = Math.max(0, r1.getBlockY() - amount);
		int z = r1.getBlockZ();
		setSaveReload(coords, "r1", r1.getWorld(), x, y, z);
	}

	public void expandP1(int dx, int dz) {
		int x = r1.getBlockX() - dx;
		int y = r1.getBlockY();
		int z = r1.getBlockZ() - dz;
		setSaveReload(coords, "r1", r1.getWorld(), x, y, z);
	}

	public void expandP2(int dx, int dz) {
		int x = r2.getBlockX() + dx;
		int y = r2.getBlockY();
		int z = r2.getBlockZ() + dz;
		setSaveReload(coords, "r2", r2.getWorld(), x, y, z);
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
		fix("r1", "r2");
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

		if (r1 == null || r2 == null) {
			return result;
		}

		Chunk c1 = world.getChunkAt(r1);
		Chunk c2 = world.getChunkAt(r2);

		for (int i = c1.getX(); i <= c2.getX(); i++) {
			for (int j = c1.getZ(); j <= c2.getZ(); j++) {
				result.add(world.getChunkAt(i, j));
			}
		}

		return result;
	}

	/**
	 * Set the location for a {@link RegionPoint}
	 * 
	 * @param point
	 * @param loc
	 */
	public void set(RegionPoint point, Location loc) {
		set(point, loc, null);
	}

	/**
	 * Set the location for a {@link RegionPoint}
	 * 
	 * @param point
	 * @param loc
	 */
	public void set(String point, Location loc) {
		set(point, loc, null);
	}

	/**
	 * Set the location for a {@link RegionPoint}
	 * 
	 * @param point
	 * @param loc
	 * @param dir
	 */
	public void set(String point, Location loc, BlockFace dir) {
		// Get point
		RegionPoint rp = game.getRegionPointFromString(point);
		if (rp == null)
			rp = game.getRegionPointFromCommonName(point);
		RegionPointMaster rpm = (RegionPointMaster) RegionPointMaster.matchString(point);
		if (rp == null && rpm == null)
			throw new IllegalArgumentException("Invalid region point '" + point + "'");

		if (rpm != null)
			setRegionPoint(rpm, loc);
		else
			set(rp, loc, dir);
	}

	/**
	 * Set the location for a {@link RegionPoint}
	 * 
	 * @param point
	 * @param loc
	 * @param dir
	 */
	public void set(RegionPoint point, Location loc, BlockFace dir) {
		switch (point.getCategory()) {
		case POINT:
			setPoint(point, loc);
			return;
		case POINT_DIR:
			if (loc != null)
				setPoint(point, loc);
			if (dir != null)
				setDir(point, dir);
			return;
		case POINT_CUBOID:
			setPoint(point, loc);
			return;
		case SPAWN:
			setSpawn(point, loc);
			return;
		}

		throw new IllegalArgumentException("Invalid region point!");
	}

	private void setRegionPoint(RegionPointMaster point, Location l) {
		// Lower and upper locations
		RegionPointMaster rp1, rp2;
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
		case R1:
			lastR1 = l.clone();
			lower = lastR1.clone();
			upper = (lastR2 != null ? lastR2.clone() : r2);
			rp1 = RegionPointMaster.R1;
			rp2 = RegionPointMaster.R2;
			break;
		case R2:
			lastR2 = l.clone();
			lower = (lastR1 != null ? lastR1.clone() : r1);
			upper = lastR2.clone();
			rp1 = RegionPointMaster.R1;
			rp2 = RegionPointMaster.R2;
			break;
		default:
			lower = upper = null;
			rp1 = rp2 = null;
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
			setLocation(coords, rp1.configName(), lower);
		if (upper != null)
			setLocation(coords, rp2.configName(), upper);
		save();

		// Reload regions and verify data
		reloadRegion();
		verifyData();
	}

	public void fixIfNeedFixing(RegionPoint rp1, RegionPoint rp2) {
		Location p1 = getPoint(rp1);
		Location p2 = getPoint(rp2);

		if (rp1 == null || rp2 == null)
			return;

		if (p1.getX() > p2.getX())
			fixCuboid(rp1, rp2);
		if (p1.getY() > p2.getY())
			fixCuboid(rp1, rp2);
		if (p1.getZ() > p2.getZ())
			fixCuboid(rp1, rp2);
	}

	public void fixCuboid(RegionPoint rp1, RegionPoint rp2) {
		Location l1 = this.getPoint(rp1);
		Location l2 = this.getPoint(rp2);
		// Min-max if both locations are non-null
		if (l1 != null && l2 != null) {
			Location lower = l1.clone();
			Location upper = l2.clone();

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

			// Set the coords and save
			if (lower != null)
				coordsMap.put(rp1, lower);
			setLocation(locations, rp1.configName(), lower);
			if (upper != null)
				coordsMap.put(rp2, upper);
			setLocation(locations, rp2.configName(), upper);
			save();

			// Reload regions and verify data
			reloadRegion();
			verifyData();
		}
	}

	private void setPoint(RegionPoint point, Location loc) {
		if (point == null || loc == null)
			throw new IllegalArgumentException("Invalid point and/or location");

		setLocation(locations, point.configName(), loc);
		save();

		reloadCoords();
	}

	private void setDir(RegionPoint point, BlockFace dir) {
		if (point == null || dir == null)
			throw new IllegalArgumentException("Invalid point and/or location");

		setDirection(directions, point.configName(), dir);
		save();

		reloadDirections();
	}

	private void setSpawn(RegionPoint point, Location loc) {
		if (point == null || loc == null)
			throw new IllegalArgumentException("Invalid point and/or location");

		setLocation(spawns, point.configName(), loc);
		save();

		reloadSpawns();
	}

	public void save() {
		game.getPlugin().saveConfig();
	}

	public void showRegion(Player p) {
		if (!isDefined()) {
			return;
		}
		showBlocks(p, getFramePoints(r1, r2));
	}

	public void showSpawns(Player p) {
		if (spawnsMap.size() == 0)
			game.getGlobalMessenger().tell(p, "NO SPAWNS");

		for (RegionPoint rp : spawnsMap.keySet()) {
			Location l = spawnsMap.get(rp);
			if (l != null)
				showBlock(p, l, rp.getShowMaterial());
		}
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
