package com.cptingle.BoardGames.framework;

import static com.cptingle.BoardGames.util.config.ConfigUtils.makeSection;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.cptingle.BoardGames.BoardGames;
import com.cptingle.BoardGames.GameMaster;
import com.cptingle.BoardGames.games.GameType;
import com.cptingle.BoardGames.games.checkers.components.PlayerType;
import com.cptingle.BoardGames.messaging.Messenger;
import com.cptingle.BoardGames.region.GameRegion;
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
		
		String configPrefix = settings.getString("prefix", type.defaultPrefix());

		createMessenger(configPrefix.equals("") ? type.defaultPrefix() : configPrefix);

		loadSettings();
	}

	protected void loadSettings() {
		this.enabled = settings.getBoolean("enabled", false);
		this.isolatedChat = settings.getBoolean("isolatedChat", false);

		GameType t = GameType.fromString(settings.getString("type", ""));
		if (t == null)
			throw new NullPointerException("[BoardGames] ERROR! Game Type (" + settings.getString("type", "")
					+ ") for game '" + name + "' is invalid!");

		this.type = t;
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

	// public Gameboard getGameboard() {
	// return gameboard;
	// }

	/*
	 * Game Handling
	 */
	public boolean inGame(Player p) {
		return players.contains(p);
	}

	public boolean playerJoin(Player p) {
		storePlayer(p);
		players.add(p);
		gm.addPlayer(p, this);

		return true;
	}

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

	public void tellAllPlayers(String message) {
		for (Player p : players) {
			messenger.tell(p, message);
		}
	}

	public void storePlayer(Player p) {
		savedPlayers.put(p, new GamePlayer(p));

		if (settings.getBoolean("take-inv-while-in-game", false))
			p.getInventory().clear();
	}

	public void restorePlayer(Player p) {
		if (savedPlayers.containsKey(p)) {
			GamePlayer plr = savedPlayers.remove(p);
			p.getInventory().setContents(plr.getInventory());
			p.teleport(plr.getReturnLocation());
		}
	}
	
	public void end() {
		end(null);
	}
	
	public void end(String message) {
		if (message != null)
			tellAllPlayers(message);
		
		running = false;
		Player[] plyrs = players.toArray(new Player[players.size()]);
		for (Player p : plyrs) {
			playerLeave(p);
		}
		resetGame();
	}
	
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

	///////////////////////////////////
	// //
	// Abstract Methods //
	// //
	///////////////////////////////////

	// Game methods
	public abstract boolean canJoin(Player p);

	protected abstract void begin();

	// Other methods
	public abstract void init();
	
}
