package com.cptingle.BoardGames;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.cptingle.BoardGames.commands.CommandHandler;
import com.cptingle.BoardGames.framework.Game;
import com.cptingle.BoardGames.listeners.BGGlobalListener;
import com.cptingle.BoardGames.messaging.Messenger;

/**
 * Board Games Plugin
 *
 */
public class BoardGames extends JavaPlugin {
	// Basic stuff
	private GameMaster gameMaster;
	private CommandHandler commandHandler;

	// Configuration stuff
	private File configFile;
	private FileConfiguration config;

	// Messaging
	private Messenger messenger;
	
	@Override
	public void onLoad() {

	}

	@Override
	public void onEnable() {
		// Intialize config file
		configFile = new File(getDataFolder(), "config.yml");
		config = new YamlConfiguration();
		reloadConfig();

		// Initialize global messenger
		String prefix = config.getString("global-settings.prefix", "");
		if (prefix.isEmpty()) {
			prefix = ChatColor.RED + "[BoardGames]";
		}
		this.messenger = new Messenger(prefix);

		// Set the header and save
		getConfig().options().header(getHeader());
		saveConfig();

		// Create gameMaster
		this.gameMaster = new GameMaster(this);
		this.gameMaster.initialize();

		// Register any inventories to restore.
		//registerInventories();

		// Register event listeners
		registerListeners();

		// Announce enable!
		getLogger().info("v" + this.getDescription().getVersion() + " enabled.");
	}

	@Override
	public void onDisable() {
		// Force end all games
		if (gameMaster == null)
			return;
		for (Game game : gameMaster.getGames()) {
			game.forceEnd();
		}

		gameMaster.resetGameMap();

		getLogger().info("disabled");
	}
	
	public File getPluginFile() {
		return getFile();
	}

	@Override
	public FileConfiguration getConfig() {
		return config;
	}

	@Override
	public void reloadConfig() {
		// Check if the config-file exists
		if (!configFile.exists()) {
			getLogger().info("No config-file found, creating default...");
			saveDefaultConfig();
		}

		// Check for tab characters in config-file
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(new File(getDataFolder(), "config.yml")));
			int row = 0;
			String line;
			while ((line = in.readLine()) != null) {
				row++;
				if (line.indexOf('\t') != -1) {
					StringBuilder buffy = new StringBuilder();
					buffy.append("Found tab in config-file on line ").append(row).append(".");
					buffy.append('\n').append("NEVER use tabs! ALWAYS use spaces!");
					buffy.append('\n').append(line);
					buffy.append('\n');
					for (int i = 0; i < line.indexOf('\t'); i++) {
						buffy.append(' ');
					}
					buffy.append('^');
					throw new IllegalArgumentException(buffy.toString());
				}
			}

			// Actually reload the config-file
			config.load(configFile);
		} catch (InvalidConfigurationException e) {
			throw new RuntimeException(
					"\n\n>>>\n>>> There is an error in your config-file! Handle it!\n>>> Here is what snakeyaml says:\n>>>\n\n"
							+ e.getMessage());
		} catch (FileNotFoundException e) {
			throw new IllegalStateException("Config-file could not be created for some reason! <o>");
		} catch (IOException e) {
			// Error reading the file, just re-throw
			getLogger().severe("There was an error reading the config-file:\n" + e.getMessage());
		} finally {
			// Java 6 <3
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					// Swallow
				}
			}
		}
	}

	@Override
	public void saveConfig() {
		try {
			config.save(configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public GameMaster getGameMaster() {
		return gameMaster;
	}

	public CommandHandler getCommandHandler() {
		return commandHandler;
	}
	
	public Messenger getGlobalMessenger() {
		return messenger;
	}

	private String getHeader() {
		String sep = System.getProperty("line.separator");
		return "BoardGames v" + this.getDescription().getVersion() + " - Config-file" + sep
				+ "Read the Wiki for details on how to set up this file: " + sep
				+ "Note: You -must- use spaces instead of tabs!";
	}

	// Register all listeners
	private void registerListeners() {
		commandHandler = new CommandHandler(this);
		addCommand("bg");
		addCommand("boardgames");

		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvents(new BGGlobalListener(this, gameMaster), this);
	}
	
	public void addCommand(String cmd) {
		getCommand(cmd).setExecutor(commandHandler);
	}

	// Permissions stuff
	public boolean has(Player p, String s) {
		return p.hasPermission(s);
	}

	public boolean has(CommandSender sender, String s) {
		if (sender instanceof ConsoleCommandSender) {
			return true;
		}
		return has((Player) sender, s);
	}

}
