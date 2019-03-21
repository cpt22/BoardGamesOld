package com.cptingle.BoardGames.commands.setup;

import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.cptingle.BoardGames.GameMaster;
import com.cptingle.BoardGames.commands.Command;
import com.cptingle.BoardGames.commands.CommandInfo;
import com.cptingle.BoardGames.framework.Game;

@CommandInfo(
	    name    = "setting",
	    pattern = "sett(ing)?",
	    usage   = "/bg setting <game> (<setting> (<value>))",
	    desc    = "show or change arena settings",
	    permission = "boardgames.setup.setting"
	)

public class SettingCommand implements Command {

	@Override
	public boolean execute(GameMaster gm, CommandSender sender, String... args) {
		// Require one game at least
		if (args.length < 1) return false;
		
		// Find game
		Game game = gm.getGameWithName(args[0]);
		if (game == null) {
			gm.getGlobalMessenger().tell(sender, "Theres no game with the name '" + args[0] + "'.");
			return true;
		}
		
		// Show settings if no more args
		if (args.length == 1) {
			StringBuilder buffy = new StringBuilder();
            buffy.append("Settings for ").append(ChatColor.GREEN).append(args[0]).append(ChatColor.RESET).append(":");
            for (Map.Entry<String,Object> entry : game.getSettings().getValues(false).entrySet()) {
                buffy.append("\n").append(ChatColor.RESET);
                buffy.append(ChatColor.AQUA).append(entry.getKey()).append(ChatColor.RESET).append(": ");
                buffy.append(ChatColor.YELLOW).append(entry.getValue());
            }
            gm.getGlobalMessenger().tell(sender, buffy.toString());
            return true;
		}
		
		// Otherwise, find the setting
        Object val = game.getSettings().get(args[1], null);
        if (val == null) {
            StringBuilder buffy = new StringBuilder();
            buffy.append(ChatColor.RED).append(" is not a valid setting.");
            buffy.append("Type ").append(ChatColor.YELLOW).append("/ma setting ").append(args[0]);
            buffy.append(ChatColor.RESET).append(" to see all settings.");
            gm.getGlobalMessenger().tell(sender, buffy.toString());
            return true;
        }

        // If there are no more args, show the value
        if (args.length == 2) {
            StringBuilder buffy = new StringBuilder();
            buffy.append(ChatColor.AQUA).append(args[1]).append(ChatColor.RESET).append(": ");
            buffy.append(ChatColor.YELLOW).append(val);
            gm.getGlobalMessenger().tell(sender, buffy.toString());
            return true;
        }

        // Otherwise, determine the value of the setting
        if (val instanceof Boolean) {
            if (!args[2].matches("on|off|yes|no|true|false")) {
                gm.getGlobalMessenger().tell(sender, "Expected a boolean value for that setting");
                return true;
            }
            boolean value = args[2].matches("on|yes|true");
            args[2] = String.valueOf(value);
            game.getSettings().set(args[1], value);
        } else if (val instanceof Number) {
            try {
                game.getSettings().set(args[1], Integer.parseInt(args[2]));
            } catch (NumberFormatException e) {
                gm.getGlobalMessenger().tell(sender, "Expected a numeric value for that setting.");
                return true;
            }
        } else {
            game.getSettings().set(args[1], args[2]);
        }

        // Save config-file and reload arena
        gm.saveConfig();
        gm.reloadGame(args[0]);

        // Notify the sender
        StringBuilder buffy = new StringBuilder();
        buffy.append("Setting ").append(ChatColor.AQUA).append(args[1]).append(ChatColor.RESET);
        buffy.append(" for game ").append(ChatColor.GREEN).append(args[0]).append(ChatColor.RESET);
        buffy.append(" set to ").append(ChatColor.YELLOW).append(args[2]).append(ChatColor.RESET);
        buffy.append("!");
        gm.getGlobalMessenger().tell(sender, buffy.toString());
        return true;
	}

}
