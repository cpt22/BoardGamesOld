package com.cptingle.BoardGames.commands.setup;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.cptingle.BoardGames.GameMaster;
import com.cptingle.BoardGames.commands.Command;
import com.cptingle.BoardGames.commands.CommandInfo;
import com.cptingle.BoardGames.framework.Game;

@CommandInfo(
	    name    = "editgame",
	    pattern = "edit(game)?",
	    usage   = "/bg editgame <game> (true|false)",
	    desc    = "set edit mode of a game",
	    permission = "boardgames.setup.editgame"
	)

public class EditGameCommand implements Command {

	@Override
	public boolean execute(GameMaster gm, CommandSender sender, String... args) {
		boolean value = false;
		Game game;
		if (args.length == 0) {
			if (gm.getGames().size() > 1) {
				gm.getGlobalMessenger().tell(sender, "There are multiple games.");
				return true;
			}
			game = gm.getGames().get(0);
			value = !game.inEditMode();
		} else if (args.length == 1) {
			if (args[0].matches("on|off|true|false")) {
				if (gm.getGames().size() > 1) {
					gm.getGlobalMessenger().tell(sender, "There are multiple games.");
					return true;
				}
				game = gm.getGames().get(0);
				value = args[0].matches("on|true");
			} else {
				game = gm.getGameWithName(args[0]);
				if (game == null) {
					gm.getGlobalMessenger().tell(sender, "There is no game named " + args[0]);
					return true;
				}
				value = !game.inEditMode();
			}
		} else {
			game = gm.getGameWithName(args[0]);
			value = args[1].matches("on|true");
		}
		game.setEditMode(value);
		gm.getGlobalMessenger().tell(sender, "Edit mode for game '" + game.configName()  + "': " + ((game.inEditMode()) ? ChatColor.GREEN + "true" : ChatColor.RED + "false"));
		if (game.inEditMode()) 
			gm.getGlobalMessenger().tell(sender, "Remember to turn it back off after editing!");
		return true;
	}
	
}
