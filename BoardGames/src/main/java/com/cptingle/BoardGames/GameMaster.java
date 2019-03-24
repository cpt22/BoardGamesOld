package com.cptingle.BoardGames;

import static com.cptingle.BoardGames.util.config.ConfigUtils.makeSection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;

import com.cptingle.BoardGames.framework.Game;
import com.cptingle.BoardGames.games.GameType;
import com.cptingle.BoardGames.games.battleship.BattleshipGame;
import com.cptingle.BoardGames.games.checkers.CheckersGame;
import com.cptingle.BoardGames.games.tictactoe.TicTacToeGame;
import com.cptingle.BoardGames.messaging.Messenger;
import com.cptingle.BoardGames.util.config.ConfigUtils;

public class GameMaster {
	// basic things
	private BoardGames plugin;
	private FileConfiguration config;

	// Game storage
	private List<Game> games;
	private Map<Player, Game> gameMap;
	// private Game selectedGame;

	private Map<Game, Set<String>> allowedCommands;

	// State of plugin enableness
	private boolean enabled;

	/**
	 * Constructor
	 * 
	 * @param plugin
	 */
	public GameMaster(BoardGames plugin) {
		this.plugin = plugin;
		this.config = plugin.getConfig();

		// intialize game storage
		this.games = new ArrayList<>();
		this.gameMap = new HashMap<>();

		this.allowedCommands = new HashMap<>();

		// Determine if plugin is set to be enabled
		this.enabled = config.getBoolean("global-settings.enabled", true);
	}

	/**
	 * Gets the plugin associated with this GameMaster
	 * 
	 * @return {@link BoardGames} plugin instance
	 */
	public BoardGames getPlugin() {
		return plugin;
	}

	/**
	 * Gets the global messenger of the {@link BoardGames} plugin
	 * 
	 * @return {@link Messenger}
	 */
	public Messenger getGlobalMessenger() {
		return plugin.getGlobalMessenger();
	}

	/**
	 * Gets if the plugin as a whole is enabled or disabled
	 * 
	 * @return {@link Boolean}
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Updates enabled state of plugin and stores it to the configuration file
	 * 
	 * @param true
	 *            if plugin should be enabled, false if it should be disabled
	 */
	public void setEnabled(boolean value) {
		enabled = value;
		config.set("global-settings.enabled", enabled);
	}

	/**
	 * Gets all loaded games
	 * 
	 * @return {@link List}<{@link Game}>
	 */
	public List<Game> getGames() {
		return games;
	}

	/**
	 * Adds a Game to the specified Player in the gameMap
	 * 
	 * @param p
	 *            Player
	 * @param g
	 *            Game
	 */
	public void addPlayer(Player p, Game g) {
		gameMap.put(p, g);
	}

	/**
	 * Removes Player-Game
	 * 
	 * @param p
	 *            Player to search for
	 * @return {@link Game} if {@link Player} found or null if Player not found
	 */
	public Game removePlayer(Player p) {
		return gameMap.remove(p);
	}

	/**
	 * Resets the game map
	 */
	public void resetGameMap() {
		gameMap.clear();
	}

	/**
	 * Checks if the provided command's use is permitted in the given game
	 * 
	 * @param game
	 * @param command
	 * @return true if allowed, false if not allowed
	 */
	public boolean isAllowed(Game game, String command) {
		if (allowedCommands.containsKey(game))
			return allowedCommands.get(game).contains(command);

		return false;
	}

	/*
	 * ///////////////////////////////////////////////////////////////////////// //
	 * // Game getters //
	 * /////////////////////////////////////////////////////////////////////////
	 */
	/**
	 * Finds enabled games in this game master
	 * 
	 * @return {@link List} of all enabled games
	 */
	public List<Game> getEnabledGames() {
		return getEnabledGames(games);
	}

	/**
	 * Finds the enabled games in the provided list
	 * 
	 * @param games
	 *            - List of games to check for enabled games
	 * @return {@link List} of all enabled games in the input list
	 */
	public List<Game> getEnabledGames(List<Game> games) {
		List<Game> result = new ArrayList<>(games.size());
		for (Game game : games)
			if (game.isEnabled())
				result.add(game);

		return result;
	}

	/**
	 * Get games permitted for the given player
	 * 
	 * @param p
	 *            - Player
	 * @return List of permitted Games
	 */
	public List<Game> getPermittedGames(Player p) {
		List<Game> result = new ArrayList<>(games.size());
		for (Game game : games) {
			if (plugin.has(p, "boardgames.games." + game.getType().configName().toLowerCase() + "."
					+ game.configName().toLowerCase()))
				result.add(game);
			else if (plugin.has(p, "boardgames.games." + game.getType().configName().toLowerCase()))
				result.add(game);
		}

		return result;
	}

	/**
	 * Get games enabled AND permitted for the given player
	 * 
	 * @param p
	 *            - Player
	 * @return List of permitted Games
	 */
	public List<Game> getEnabledAndPermittedGames(Player p) {
		List<Game> result = new ArrayList<>(games.size());
		for (Game game : games) {
			if (game.isEnabled()) {
				if (plugin.has(p, "boardgames.games." + game.getType().configName().toLowerCase() + "."
						+ game.configName().toLowerCase()))
					result.add(game);
				else if (plugin.has(p, "boardgames.games." + game.getType().configName().toLowerCase()))
					result.add(game);
			}
		}

		return result;
	}

	public List<Game> getGamesInWorld(World world) {
		List<Game> result = new ArrayList<>(games.size());
		for (Game game : games)
			if (game.getWorld().equals(world))
				result.add(game);
		return result;
	}

	public List<Player> getAllPlayers() {
		List<Player> result = new ArrayList<>(games.size());
		for (Game game : games)
			result.addAll(game.getPlayers());
		return result;
	}

	public List<Player> getAllPlayersInGame(String gameName) {
		Game game = getGameWithName(gameName);
		return (game != null) ? new ArrayList<>(game.getPlayers()) : new ArrayList<Player>();

	}

	public Game getGameWithPlayer(Player p) {
		return gameMap.get(p);
	}

	public Game getGameWithPlayer(String playerName) {
		return gameMap.get(plugin.getServer().getPlayer(playerName));
	}

	public Game getGameWithName(String configName) {
		return getGameWithName(games, configName);
	}

	public Game getGameWithName(Collection<Game> games, String configName) {
		for (Game game : games) {
			if (game.configName().equals(configName)) {
				return game;
			}
		}
		return null;
	}

	/*
	 * ///////////////////////////////////////////////////////////////////////// //
	 * // Initialization //
	 * /////////////////////////////////////////////////////////////////////////
	 */

	public void initialize() {
		loadSettings();
		loadGames();
		loadAllowedCommands();
	}

	/**
	 * Load global settings
	 */
	public void loadSettings() {
		ConfigurationSection section = plugin.getConfig().getConfigurationSection("global-settings");
		ConfigUtils.addMissingRemoveObsolete(plugin, "global-settings.yml", section);
	}

	public void loadAllowedCommands() {
		for (Game g : games) {
			Set<String> temp = new HashSet<>();
			// Get commands string
			String cmds = g.getSettings().getString("allowed-commands", "");

			// Split by commas
			String[] parts = cmds.split(",");

			// Add in ec command
			temp.add("/gb");

			// Add in each command
			for (String part : parts) {
				temp.add(part.trim().toLowerCase());
			}

			allowedCommands.put(g, temp);
		}
	}

	/**
	 * Load all games related stuff
	 */
	public void loadGames() {
		ConfigurationSection section = makeSection(config, "games");
		Set<String> gamenames = section.getKeys(false);

		if (gamenames == null || gamenames.isEmpty()) {
			createGameNode(section, "default", "checkers", plugin.getServer().getWorlds().get(0), false);
		}

		games = new ArrayList<>();
		for (World w : Bukkit.getServer().getWorlds()) {
			loadGamesInWorld(w.getName());
		}
		plugin.getLogger().info(games.size() + " games loaded");
	}

	public void loadGamesInWorld(String worldName) {
		Set<String> gameNames = config.getConfigurationSection("games").getKeys(false);
		if (gameNames == null || gameNames.isEmpty()) {
			return;
		}
		for (String gameName : gameNames) {
			Game game = getGameWithName(gameName);
			if (game != null)
				continue;

			String gameWorld = config.getString("games." + gameName + ".settings.world", "");
			if (!gameWorld.equals(worldName))
				continue;

			loadGame(gameName);
		}
	}

	public void unloadGamesInWorld(String worldName) {
		Set<String> gameNames = config.getConfigurationSection("games").getKeys(false);
		if (gameNames == null || gameNames.isEmpty()) {
			return;
		}
		for (String gameName : gameNames) {
			Game game = getGameWithName(gameName);
			if (game == null)
				continue;

			String gameWorld = game.getWorld().getName();
			if (!gameWorld.equals(worldName))
				continue;

			game.forceEnd();
			games.remove(game);
		}
	}

	public Game loadGame(String gamename) {
		ConfigurationSection section = makeSection(config, "games." + gamename);
		ConfigurationSection settings = makeSection(section, "settings");
		ConfigurationSection specificSettings = makeSection(section, "specific-settings");
		String worldName = settings.getString("world", "");
		String type = settings.getString("type", "");
		World world;

		if (!worldName.equals("")) {
			world = plugin.getServer().getWorld(worldName);
			if (world == null) {
				plugin.getLogger().warning("World '" + worldName + "' for game '" + gamename + "' was not found...");
				return null;
			}
		} else {
			world = plugin.getServer().getWorlds().get(0);
			plugin.getLogger().warning("Could not find the world for game '" + gamename + "'. Using default world ('"
					+ world.getName() + "')! Check the config-file!");
		}

		ConfigUtils.addMissingRemoveObsolete(plugin, "generic-game-settings.yml", settings);
		ConfigUtils.addIfEmpty(plugin, type.toLowerCase() + "-settings.yml", specificSettings);

		Game game = createGame(type, plugin, section, gamename, world);

		registerPermission(
				"easycheckers.games." + game.getType().configName().toLowerCase() + "." + gamename.toLowerCase(),
				PermissionDefault.TRUE);
		
		games.add(game);
		//game.init();
		return game;
	}

	public boolean reloadGame(String name) {
		Game game = getGameWithName(name);
		if (game == null)
			return false;

		game.forceEnd();
		games.remove(game);

		plugin.reloadConfig();
		config = plugin.getConfig();

		loadGame(name);
		return true;
	}

	public void reload() {
		for (Game g : games) {
			g.forceEnd();
		}
		
		games.clear();
	
		plugin.reloadConfig();
		config = plugin.getConfig();

		initialize();
	}

	// New games
	public Game createGameNode(String gameName, String type, World world) {
		ConfigurationSection section = makeSection(config, "games");
		return createGameNode(section, gameName, type, world, true);
	}

	public Game createGameNode(ConfigurationSection games, String gameName, String type, World world, boolean load) {
		if (games.contains(gameName)) {
			throw new IllegalArgumentException("Game already exists!");
		}
		ConfigurationSection section = makeSection(games, gameName);

		// Add missing settings and remove obsolete ones
		ConfigUtils.addMissingRemoveObsolete(plugin, "generic-game-settings.yml", makeSection(section, "settings"));
		ConfigUtils.addIfEmpty(plugin, type.toLowerCase() + "-settings.yml", makeSection(section, "specific-settings"));
		section.set("settings.world", world.getName());
		section.set("settings.type", type);
		plugin.saveConfig();

		return (load ? loadGame(gameName) : null);
	}

	public void removeGameNode(Game game) {
		games.remove(game);
		unregisterPermission("boardgames.games." + game.getType().configName().toLowerCase() + "."
				+ game.configName().toLowerCase());

		config.set("games." + game.configName(), null);
		plugin.saveConfig();
	}

	public void reloadConfig() {
		boolean wasEnabled = isEnabled();
		if (wasEnabled)
			setEnabled(false);
		for (Game g : games) {
			g.forceEnd();
		}
		plugin.reloadConfig();
		config = plugin.getConfig();
		initialize();
		if (wasEnabled)
			setEnabled(true);
	}

	public void saveConfig() {
		plugin.saveConfig();
	}

	private Permission registerPermission(String permString, PermissionDefault value) {
		PluginManager pm = plugin.getServer().getPluginManager();

		Permission perm = pm.getPermission(permString);
		if (perm == null) {
			perm = new Permission(permString);
			perm.setDefault(value);
			pm.addPermission(perm);
		}
		return perm;
	}

	private void unregisterPermission(String s) {
		plugin.getServer().getPluginManager().removePermission(s);
	}

	// Weird Special Stuff
	private Game createGame(String type, BoardGames plugin, ConfigurationSection section, String name, World world) {
		// plugin.reloadConfig();
		Game game = null;
		GameType t = GameType.fromString(type);

		switch (t) {
		case CHECKERS:
			game = new CheckersGame(plugin, section, name, world);
			break;
		case BATTLESHIP:
			game = new BattleshipGame(plugin, section, name, world);
			break;
		case TICTACTOE:
			game = new TicTacToeGame(plugin, section, name, world);
			break;
		}
		return game;
	}

}
