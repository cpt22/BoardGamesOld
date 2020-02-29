package com.cptingle.BoardGames.commands.admin;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.cptingle.BoardGames.GameMaster;
import com.cptingle.BoardGames.commands.Command;
import com.cptingle.BoardGames.commands.CommandInfo;
import com.cptingle.BoardGames.framework.Game;
import com.cptingle.BoardGames.messaging.Msg;

@CommandInfo(name = "disable", pattern = "disable|off", usage = "/bg disable (<game>|all)", desc = "disable BoardGames or individual games", permission = "boardgames.admin.enable")
public class DisableCommand implements Command {

	@Override
	public boolean execute(GameMaster gm, CommandSender sender, String... args) {
		// Grab the argument, if any.
		String arg1 = (args.length > 0 ? args[0] : "");

		if (arg1.equals("all")) {
			for (Game game : gm.getGames()) {
				disable(game, sender);
			}
			return true;
		}

		if (!arg1.equals("")) {
			Game game = gm.getGameWithName(arg1);
			if (game == null) {
				gm.getGlobalMessenger().tell(sender, Msg.GAME_DOES_NOT_EXIST);
				return true;
			}
			disable(game, sender);
			return true;
		}

		gm.setEnabled(false);
		gm.saveConfig();
		gm.getGlobalMessenger().tell(sender, "BoardGames " + ChatColor.RED + "disabled");
		return true;
	}

	private void disable(Game game, CommandSender sender) {
		game.setEnabled(false);
		game.getPlugin().saveConfig();
		game.getGlobalMessenger().tell(sender, "Game '" + game.configName() + "' " + ChatColor.RED + "disabled");
		game.forceEnd();
	}

}
