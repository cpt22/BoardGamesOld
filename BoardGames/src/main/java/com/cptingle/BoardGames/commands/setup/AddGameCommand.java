package com.cptingle.BoardGames.commands.setup;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.cptingle.BoardGames.GameMaster;
import com.cptingle.BoardGames.commands.Command;
import com.cptingle.BoardGames.commands.CommandInfo;
import com.cptingle.BoardGames.commands.Commands;
import com.cptingle.BoardGames.framework.Game;
import com.cptingle.BoardGames.messaging.Msg;

@CommandInfo(
	    name    = "addgame",
	    pattern = "(add|new)game",
	    usage   = "/bg addgame <name> <type>",
	    desc    = "add a new game",
	    permission = "boardgames.setup.addgame"
)

public class AddGameCommand implements Command {
	
	@Override
	public boolean execute(GameMaster gm, CommandSender sender, String... args) {
		if (!Commands.isPlayer(sender)) {
			gm.getGlobalMessenger().tell(sender, Msg.MISC_NOT_FROM_CONSOLE);
		}
		
		// Require a game id
		if (args.length != 2) return false;
		
		Player p = Commands.unwrap(sender);
		
		Game game = gm.getGameWithName(args[0]);
		
		if (game != null) {
			gm.getGlobalMessenger().tell(sender, "A game with that name already exists");
			return true;
		}
		gm.createGameNode(args[0], args[1], p.getWorld());
		gm.getGlobalMessenger().tell(sender, "New game with name '" + args[0] + "' created of type '" + args[1] + "'!");
		return true;
		
		
	}
}
