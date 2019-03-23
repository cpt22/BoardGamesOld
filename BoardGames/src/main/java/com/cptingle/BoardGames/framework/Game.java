package com.cptingle.BoardGames.framework;

import static com.cptingle.BoardGames.util.config.ConfigUtils.makeSection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.cptingle.BoardGames.BoardGames;
import com.cptingle.BoardGames.GameMaster;
import com.cptingle.BoardGames.games.GameType;
import com.cptingle.BoardGames.games.PlayerType;
import com.cptingle.BoardGames.games.PointCategory;
import com.cptingle.BoardGames.messaging.Messenger;
import com.cptingle.BoardGames.region.GameRegion;
import com.cptingle.BoardGames.region.RegionPoint;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public abstract class Game {
	// Basic variables
	protected BoardGames plugin;
	protected GameMaster gm;
	protected String name;
	protected World world;
	protected GameType type;
	protected Messenger messenger;

	// Config stuff
	protected ConfigurationSection settings;
	protected ConfigurationSection specificSettings;

	// Region
	protected GameRegion region;

	// Players
	protected Set<Player> players;
	protected BiMap<PlayerType, Player> playerMap;
	protected Map<Player, GamePlayer> savedPlayers;
	protected Map<PlayerType, Location> playerSpawns;
	
	protected PlayerType whoseTurn;
	protected PlayerType winner;

	// Critical settings
	protected boolean enabled, protect, running, edit;


	// Other Settings
	protected boolean isolatedChat;

	// Listener
	protected GameListener listener;

	// Game Stuff
	// protected Gameboard gameboard;

	/**
	 * Constructor
	 * 
	 * @param plugin
	 * @param config
	 * @param name
	 * @param world
	 */
	public Game(BoardGames plugin, ConfigurationSection section, GameType type, String name, World world) {
		if (world == null)
			throw new NullPointerException("[BoardGames] ERROR ! World for game '" + name + "' does not exist!");

		this.plugin = plugin;
		this.gm = plugin.getGameMaster();
		this.name = name;
		this.world = world;
		this.settings = makeSection(section, "settings");
		this.specificSettings = makeSection(section, "specific-settings");
		this.region = new GameRegion(section, this);
		this.type = type;

		this.running = false;
		this.enabled = false;
		this.edit = false;
		this.protect = false;

		this.players = new HashSet<>();
		this.playerMap = HashBiMap.create();
		this.savedPlayers = new HashMap<>();
		this.playerSpawns = new HashMap<>();
		this.whoseTurn = PlayerType.NONE;
		
		String configPrefix = settings.getString("prefix", type.defaultPrefix());

		createMessenger(configPrefix.equals("") ? type.defaultPrefix() : configPrefix);

		loadSettings();
	}

	protected void loadSettings() {
		this.enabled = settings.getBoolean("enabled", false);
		this.isolatedChat = settings.getBoolean("isolatedChat", false);		
	}

	protected void createMessenger(String prefix) {
		if (prefix.equals("")) {
			messenger = gm.getGlobalMessenger();
			return;
		}

		messenger = new Messenger(prefix);
	}

	public ConfigurationSection getSettings() {
		return settings;
	}
	
	public ConfigurationSection getSpecificSettings() {
		return specificSettings;
	}

	public World getWorld() {
		return world;
	}

	public void setWorld(World world) {
		this.world = world;
		settings.set("world", world.getName());
		plugin.saveConfig();
		if (region != null) {
			region.refreshWorld();
		}
	}

	public String getName() {
		return name;
	}

	public String gameName() {
		return name;
	}

	public String configName() {
		return name;
	}

	public GameType getType() {
		return type;
	}

	public BoardGames getPlugin() {
		return plugin;
	}

	public Set<Player> getPlayers() {
		return players;
	}

	public Set<Player> getAllPlayers() {
		return players;
	}

	public BiMap<PlayerType, Player> getPlayerMap() {
		return playerMap;
	}
	
	public PlayerType getTypeFromPlayer(Player p) {
		return playerMap.inverse().get(p);
	}
	
	public Player getPlayerFromType(PlayerType pt) {
		return playerMap.get(pt);
	}

	public void resetGame() {
		players.clear();
		playerMap.clear();
	}

	public boolean isRunning() {
		return running;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean value) {
		enabled = value;
		settings.set("enabled", value);
		if (enabled) {
			setEditMode(false);
		}
	}

	public boolean isProtected() {
		return protect;
	}

	public void setProtected(boolean value) {
		protect = value;
		settings.set("protect", protect);
	}

	public boolean inEditMode() {
		return edit;
	}

	public void setEditMode(boolean value) {
		edit = value;
	}

	public boolean hasIsolatedChat() {
		return isolatedChat;
	}

	public GameRegion getRegion() {
		return region;
	}

	public Messenger getGlobalMessenger() {
		return plugin.getGlobalMessenger();
	}

	public Messenger getMessenger() {
		return messenger;
	}

	public GameListener getEventListener() {
		return listener;
	}

	public void setEventListener(GameListener gl) {
		listener = gl;
	}
	
	public Location getSpawnForPlayer(PlayerType p) {
		return playerSpawns.get(p);
	}

	public PlayerType whoseTurn() {
		return whoseTurn;
	}
	
	public PlayerType turn() {
		return whoseTurn;
	}
	
	public void setTurn(PlayerType pt) {
		whoseTurn = pt;
	}
	
	public void doWin(PlayerType pt) {
		winner = pt;
		Player winplr = playerMap.get(winner);
		for (Player player : playerMap.inverse().keySet()) {
			messenger.tell(player, (winplr.equals(player)) ? "You win!" : "You Lose!");
		}
		
		this.end();
	}

	/*
	 * Game Handling
	 */
	/**
	 * Checks if player is in this game
	 * @param p
	 * @return
	 */
	public boolean inGame(Player p) {
		return players.contains(p);
	}

	/**
	 * Join a player to this game
	 * @param p
	 * @return
	 */
	public boolean playerJoin(Player p) {
		storePlayer(p);
		players.add(p);
		gm.addPlayer(p, this);

		return true;
	}

	/**
	 * Make a player leave this game
	 * @param p
	 * @return
	 */
	public boolean playerLeave(Player p) {
		restorePlayer(p);
		gm.removePlayer(p);
		playerMap.remove(playerMap.inverse().get(p));
		players.remove(p);
		if (isRunning()) {
			end("The game is ending!");
		}
		return true;
	}

	/**
	 * Send a message to all players currently in this game
	 * @param message
	 */
	public void tellAllPlayers(String message) {
		for (Player p : players) {
			messenger.tell(p, message);
		}
	}

	/**
	 * Stores a players data for restoration after leaving game
	 * @param p
	 */
	public void storePlayer(Player p) {
		savedPlayers.put(p, new GamePlayer(p));

		if (settings.getBoolean("take-inv-while-in-game", false))
			p.getInventory().clear();
	}

	/**
	 * Restore a players data after leaving the game
	 * @param p
	 */
	public void restorePlayer(Player p) {
		if (savedPlayers.containsKey(p)) {
			GamePlayer plr = savedPlayers.remove(p);
			p.getInventory().setContents(plr.getInventory());
			p.teleport(plr.getReturnLocation());
		}
	}
	
	/**
	 * End the game
	 */
	public void end() {
		end(null);
	}
	
	/**
	 * End the game with specified message sent to all players
	 * @param message
	 */
	public void end(String message) {
		if (message != null)
			tellAllPlayers(message);
		
		running = false;
		setTurn(PlayerType.NONE);
		
		Player[] plyrs = players.toArray(new Player[players.size()]);
		for (Player p : plyrs) {
			playerLeave(p);
		}
		resetGame();
	}
	
	/**
	 * Force end the game
	 */
	public void forceEnd() {
		tellAllPlayers("Game is ending!");
		running = false;
		Player[] plyrs = players.toArray(new Player[players.size()]);
		for (Player p : plyrs) {
			playerLeave(p);
		}
	}

	/**
	 * Schedule a Runnable to be executed after the given delay in server ticks.
	 */
	public void scheduleTask(Runnable r, int delay) {
		Bukkit.getScheduler().runTaskLater(plugin, r, delay);
	}

	/**
	 * Compare two games
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof Game) {
			return ((Game) o).getName() == this.getName();
		} else if (o instanceof String) {
			return ((String) o).equals(this.getName());
		}
		return false;
	}
	
	public RegionPoint[] getPointTypesWithCategory(PointCategory cat) {
		List<RegionPoint> result = new ArrayList<>();
		
		for (RegionPoint p : getAllRegionPoints()) {
			if (p.getCategory() == cat)
				result.add(p);
		}
		
		return result.toArray(new RegionPoint[result.size()]);
	}

	///////////////////////////////////
	// //
	// Abstract Methods //
	// //
	///////////////////////////////////

	// Game methods
	/**
	 * Check if a player is permitted to join the game
	 * @param p
	 * @return true if player permitted to join
	 */
	public abstract boolean canJoin(Player p);

	/**
	 * Begin the game
	 */
	protected abstract void begin();
	
	/**
	 * Advance game to next turn
	 */
	public abstract void nextTurn();

	// Other methods
	/**
	 * Initialize a game
	 */
	public abstract void init();
	
	/**
	 *  Load a specific game's point types from string
	 * @param s
	 * @return
	 */
	public abstract RegionPoint getRegionPointFromString(String s);
	
	/**
	 * Gets all point types
	 * @return
	 */
	public abstract RegionPoint[] getAllRegionPoints();

	
}
