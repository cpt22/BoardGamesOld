package com.cptingle.BoardGames.commands.user;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.cptingle.BoardGames.GameMaster;
import com.cptingle.BoardGames.commands.Command;
import com.cptingle.BoardGames.commands.CommandInfo;
import com.cptingle.BoardGames.commands.Commands;
import com.cptingle.BoardGames.framework.Game;
import com.cptingle.BoardGames.messaging.Msg;

@CommandInfo(name = "join", pattern = "j|jo.*|j.*n", usage = "/bg join (<game>)", desc = "join a game", permission = "boardgames.use.join")

public class JoinCommand implements Command {

	@Override
	public boolean execute(GameMaster gm, CommandSender sender, String... args) {
		if (!Commands.isPlayer(sender)) {
			gm.getGlobalMessenger().tell(sender, Msg.MISC_NOT_FROM_CONSOLE);
			return true;
		}

		// Unwrap the sender, grab the argument, if any.
		Player p = Commands.unwrap(sender);
		String arg1 = (args.length > 0 ? args[0] : null);

		Game toGame = Commands.getGameToJoin(gm, p, arg1);
		if (toGame == null) {
			// gm.getGlobalMessenger().tell(p, Msg.GAME_DOES_NOT_EXIST);
			return true;
		}
		// Deny joining from other arenas
		Game fromGame = gm.getGameWithPlayer(p);
		if (fromGame != null && (fromGame.inGame(p))) {
			fromGame.getMessenger().tell(p, Msg.JOIN_ALREADY_PLAYING);
			return true;
		}

		// Per-game sanity checks
		if (!toGame.canJoin(p)) {
			toGame.getMessenger().tell(p, Msg.GAME_FULL);
			return true;
		}

		// Force leave previous arena
		if (fromGame != null)
			fromGame.playerLeave(p);

		// Join the arena!
		return toGame.playerJoin(p);
	}

}
