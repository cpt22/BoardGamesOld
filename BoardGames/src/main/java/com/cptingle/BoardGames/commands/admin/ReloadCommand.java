package com.cptingle.BoardGames.commands.admin;

import org.bukkit.command.CommandSender;

import com.cptingle.BoardGames.GameMaster;
import com.cptingle.BoardGames.commands.Command;
import com.cptingle.BoardGames.commands.CommandInfo;
import com.cptingle.BoardGames.framework.Game;
import com.cptingle.BoardGames.messaging.Msg;

import net.md_5.bungee.api.ChatColor;

@CommandInfo(name = "reload", pattern = "reload", usage = "/bg reload", desc = "Reload BoardGames config from file", permission = "boardgames.admin.reload")
public class ReloadCommand implements Command {

	@Override
	public boolean execute(GameMaster gm, CommandSender sender, String... args) {

		if (args.length == 0) {
			gm.reload();
			gm.getGlobalMessenger().tell(sender, Msg.CONFIG_RELOADED);
		} else if (args.length == 1) {
			Game game = gm.getGameWithName(args[0]);

			if (game == null) {
				gm.getGlobalMessenger().tell(sender, Msg.GAME_DOES_NOT_EXIST);
				return true;
			}

			gm.getGlobalMessenger().tell(sender,
					"Game " + ChatColor.YELLOW + game.getName() + "&r was reloaded from config!");
			gm.reloadGame(game.getName());
		}

		return true;
	}

}
