package com.cptingle.BoardGames.commands.setup;

import org.bukkit.command.CommandSender;

import com.cptingle.BoardGames.GameMaster;
import com.cptingle.BoardGames.commands.Command;
import com.cptingle.BoardGames.commands.CommandInfo;
import com.cptingle.BoardGames.framework.Game;
import com.cptingle.BoardGames.games.AutoGeneratorHelper;
import com.cptingle.BoardGames.messaging.Msg;

@CommandInfo(name = "autodegenerate", pattern = "auto(\\-)?degenerate", usage = "/bg autodegenerate <game>", desc = "autodegenerate an existing game", permission = "boardgames.setup.autodegenerate")
public class AutoDegenerateCommand implements Command {

	@Override
	public boolean execute(GameMaster gm, CommandSender sender, String... args) {
		// Require an arena name
		if (args.length != 1)
			return false;

		// We have to make sure at least one arena exists before degenerating
		if (gm.getGames().size() < 2) {
			gm.getGlobalMessenger().tell(sender, "At least one game must exist!");
			return true;
		}

		// Check if arena exists.
		Game game = gm.getGameWithName(args[0]);
		if (game == null) {
			gm.getGlobalMessenger().tell(sender, Msg.GAME_DOES_NOT_EXIST);
			return true;
		}

		if (!AutoGeneratorHelper.autoDegenerate(args[0], gm.getPlugin(), true)) {
			gm.getGlobalMessenger().tell(sender, "Could not degenerate game.");
			return true;
		}

		gm.getGlobalMessenger().tell(sender, "Game with name '" + args[0] + "' degenerated.");
		return true;
	}

}
