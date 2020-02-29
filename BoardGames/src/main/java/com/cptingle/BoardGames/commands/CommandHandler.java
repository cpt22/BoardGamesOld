package com.cptingle.BoardGames.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversable;

import com.cptingle.BoardGames.BoardGames;
import com.cptingle.BoardGames.GameMaster;
import com.cptingle.BoardGames.commands.admin.DisableCommand;
import com.cptingle.BoardGames.commands.admin.EnableCommand;
import com.cptingle.BoardGames.commands.admin.ReloadCommand;
import com.cptingle.BoardGames.commands.setup.AddGameCommand;
import com.cptingle.BoardGames.commands.setup.AutoDegenerateCommand;
import com.cptingle.BoardGames.commands.setup.AutoGenerateCommand;
import com.cptingle.BoardGames.commands.setup.EditGameCommand;
import com.cptingle.BoardGames.commands.setup.RemoveGameCommand;
import com.cptingle.BoardGames.commands.setup.SettingCommand;
import com.cptingle.BoardGames.commands.setup.SetupCommand;
import com.cptingle.BoardGames.commands.user.GameListCommand;
import com.cptingle.BoardGames.commands.user.JoinCommand;
import com.cptingle.BoardGames.commands.user.LeaveCommand;
import com.cptingle.BoardGames.messaging.Msg;

public class CommandHandler implements CommandExecutor {

	private BoardGames plugin;
	private GameMaster gm;

	private Map<String, Command> commands;

	public CommandHandler(BoardGames plugin) {
		this.plugin = plugin;
		this.gm = plugin.getGameMaster();

		registerCommands();
	}

	@Override
	public boolean onCommand(CommandSender sender, org.bukkit.command.Command bcmd, String label, String[] args) {
		// Get base and then args
		String base = (args.length > 0 ? args[0] : "");
		String last = (args.length > 0 ? args[args.length - 1] : "");

		// If the player is in a convo (Setup Mode), bail
		if (sender instanceof Conversable && ((Conversable) sender).isConversing()) {
			return true;
		}

		// The help command is a little special
		if (base.equals("?") || base.equals("help") || base.equals("")) {
			showHelp(sender);
			return true;
		}

		// Get commands matching base
		List<Command> matches = getMatchingCommands(base);

		// Display matches if more than 1
		if (matches.size() > 1) {
			gm.getGlobalMessenger().tell(sender, Msg.MISC_MULTIPLE_MATCHES);
			for (Command cmd : matches) {
				showUsage(cmd, sender, false);
			}
			return true;
		}

		// If there are no matches at all, notify.
		if (matches.size() == 0) {
			gm.getGlobalMessenger().tell(sender, Msg.MISC_NO_MATCHES);
			return true;
		}

		// Grab the only match.
		Command command = matches.get(0);
		CommandInfo info = command.getClass().getAnnotation(CommandInfo.class);

		// Check for permission
		if (!plugin.has(sender, info.permission())) {
			gm.getGlobalMessenger().tell(sender, Msg.MISC_NO_ACCESS);
			return true;
		}

		// Check if the last argument is a ?, in which case, display usage and
		// description
		if (last.equals("?") || last.equals("help")) {
			showUsage(command, sender, true);
			return true;
		}

		// Otherwise, execute the command!
		String[] params = trimFirstArg(args);
		if (!command.execute(gm, sender, params)) {
			showUsage(command, sender, true);
		}
		return true;
	}

	/**
	 * Get all commands that match a given string.
	 * 
	 * @param arg the given string
	 * @return a list of commands whose patterns match the given string
	 */
	private List<Command> getMatchingCommands(String arg) {
		List<Command> result = new ArrayList<>();

		// Grab the commands that match the argument.
		for (Entry<String, Command> entry : commands.entrySet()) {
			if (arg.matches(entry.getKey())) {
				result.add(entry.getValue());
			}
		}

		return result;
	}

	/**
	 * Show the usage and description messages of a command to a player. The usage
	 * will only be shown, if the player has permission for the command.
	 * 
	 * @param cmd    a Command
	 * @param sender a CommandSender
	 */
	private void showUsage(Command cmd, CommandSender sender, boolean prefix) {
		CommandInfo info = cmd.getClass().getAnnotation(CommandInfo.class);
		if (!plugin.has(sender, info.permission()))
			return;

		gm.getGlobalMessenger().tell(sender,
				(prefix ? "Usage: " : "") + info.usage() + " " + ChatColor.YELLOW + info.desc());
	}

	/**
	 * Remove the first argument of a string. This is because the very first element
	 * of the arguments array will be the command itself.
	 * 
	 * @param args an array of length n
	 * @return the same array minus the first element, and thus of length n-1
	 */
	private String[] trimFirstArg(String[] args) {
		return Arrays.copyOfRange(args, 1, args.length);
	}

	/**
	 * List all the available MobArena commands for the CommandSender.
	 * 
	 * @param sender a player or the console
	 */
	private void showHelp(CommandSender sender) {
		StringBuilder user = new StringBuilder();
		StringBuilder admin = new StringBuilder();
		StringBuilder setup = new StringBuilder();

		for (Command cmd : commands.values()) {
			CommandInfo info = cmd.getClass().getAnnotation(CommandInfo.class);
			if (!plugin.has(sender, info.permission()))
				continue;

			StringBuilder buffy;
			if (info.permission().startsWith("boardgames.admin")) {
				buffy = admin;
			} else if (info.permission().startsWith("boardgames.setup")) {
				buffy = setup;
			} else {
				buffy = user;
			}
			buffy.append("\n").append(ChatColor.RESET).append(info.usage()).append(" ").append(ChatColor.YELLOW)
					.append(info.desc());
		}

		if (admin.length() == 0 && setup.length() == 0) {
			sender.sendMessage("Available commands: " + user.toString());
		} else {
			sender.sendMessage("User commands: " + user.toString());
			if (admin.length() > 0)
				sender.sendMessage("Admin commands: " + admin.toString());
			if (setup.length() > 0)
				sender.sendMessage("Setup commands: " + setup.toString());
		}
	}

	/**
	 * Register all commands directly
	 */
	private void registerCommands() {
		commands = new LinkedHashMap<>();

		// User Commands
		register(JoinCommand.class);
		register(GameListCommand.class);
		register(LeaveCommand.class);

		// Admin Commands
		register(EnableCommand.class);
		register(DisableCommand.class);
		register(ReloadCommand.class);

		// Setup Commands
		// register(SetupCommand.class);
		register(SettingCommand.class);
		register(SetupCommand.class);

		register(AddGameCommand.class);
		register(RemoveGameCommand.class);
		register(EditGameCommand.class);

		register(AutoGenerateCommand.class);
		register(AutoDegenerateCommand.class);
	}

	/**
	 * Register a command. The Command's CommandInfo annotation is queried to find
	 * its pattern string, which is used to map the commands.
	 * 
	 * @param c a Command
	 */
	public void register(Class<? extends Command> c) {
		CommandInfo info = c.getAnnotation(CommandInfo.class);
		if (info == null)
			return;

		try {
			commands.put(info.pattern(), c.newInstance());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
