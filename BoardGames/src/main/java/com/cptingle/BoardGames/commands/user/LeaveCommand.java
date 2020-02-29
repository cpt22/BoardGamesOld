package com.cptingle.BoardGames.commands.user;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.cptingle.BoardGames.GameMaster;
import com.cptingle.BoardGames.commands.Command;
import com.cptingle.BoardGames.commands.CommandInfo;
import com.cptingle.BoardGames.commands.Commands;
import com.cptingle.BoardGames.framework.Game;
import com.cptingle.BoardGames.messaging.Msg;

@CommandInfo(name = "leave", pattern = "l|le((.*))?", usage = "/bg leave", desc = "leave the game", permission = "boardgames.use.leave")
public class LeaveCommand implements Command {

	@Override
	public boolean execute(GameMaster gm, CommandSender sender, String... args) {
		if (!Commands.isPlayer(sender)) {
			gm.getGlobalMessenger().tell(sender, Msg.MISC_NOT_FROM_CONSOLE);
			return true;
		}

		// Unwrap the sender.
		Player p = Commands.unwrap(sender);

		Game game = gm.getGameWithPlayer(p);

		if (game != null) {
			game.playerLeave(p);
			game.getMessenger().tell(p, Msg.PLAYER_LEAVE);
		} else {
			gm.getGlobalMessenger().tell(p, Msg.PLAYER_NOT_IN_GAME);
		}

		return true;
	}

}
