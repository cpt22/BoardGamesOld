package com.cptingle.BoardGames.commands.setup;

import org.bukkit.command.CommandSender;

import com.cptingle.BoardGames.GameMaster;
import com.cptingle.BoardGames.commands.Command;
import com.cptingle.BoardGames.commands.CommandInfo;
import com.cptingle.BoardGames.framework.Game;

@CommandInfo(
	    name    = "removegame",
	    pattern = "(del(.)*|r(e)?m(ove)?)game",
	    usage   = "/bg removegame <game>",
	    desc    = "remove a game",
	    permission = "boardgames.setup.removegame"
	)
public class RemoveGameCommand implements Command {

	@Override
	public boolean execute(GameMaster gm, CommandSender sender, String... args) {
		if (args.length != 1) return false;
		
		if (gm.getGames().size() == 1) {
			gm.getGlobalMessenger().tell(sender, "At least one game must exist.");
			return true;
		}
		
		Game game = gm.getGameWithName(args[0]);
		if (game == null) {
			gm.getGlobalMessenger().tell(sender, "There is no game with that name.");
			return true;
		}
		gm.removeGameNode(game);
		gm.getGlobalMessenger().tell(sender, "Game '" + game.configName() + "' deleted!");
		return true;
	}

}
