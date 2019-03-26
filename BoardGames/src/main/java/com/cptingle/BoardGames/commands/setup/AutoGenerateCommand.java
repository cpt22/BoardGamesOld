package com.cptingle.BoardGames.commands.setup;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.cptingle.BoardGames.GameMaster;
import com.cptingle.BoardGames.commands.Command;
import com.cptingle.BoardGames.commands.CommandInfo;
import com.cptingle.BoardGames.commands.Commands;
import com.cptingle.BoardGames.framework.Game;
import com.cptingle.BoardGames.games.GameType;
import com.cptingle.BoardGames.games.checkers.CheckersAutoGenerator;
import com.cptingle.BoardGames.games.minesweeper.MinesweeperAutoGenerator;
import com.cptingle.BoardGames.games.sudoku.SudokuAutoGenerator;
import com.cptingle.BoardGames.games.tictactoe.TicTacToeAutoGenerator;
import com.cptingle.BoardGames.messaging.Msg;

@CommandInfo(
	    name    = "autogenerate",
	    pattern = "auto(\\-)?generate",
	    usage   = "/bg autogenerate <game> <type>",
	    desc    = "autogenerate a new game",
	    permission = "easycheckers.setup.autogenerate"
	)
public class AutoGenerateCommand implements Command {

	@Override
	public boolean execute(GameMaster gm, CommandSender sender, String... args) {
		if (!Commands.isPlayer(sender)) {
			gm.getGlobalMessenger().tell(sender, Msg.MISC_NOT_FROM_CONSOLE);
		}
		
		// Requires game name
		if (args.length != 2) return false;
		
		// Unwrap sender
		Player p = Commands.unwrap(sender);
		
		Game game = gm.getGameWithName(args[0]);
		if (game != null) {
			gm.getGlobalMessenger().tell(sender, "A game with that name already exists!");
			return true;
		}
		boolean successful = false;
		GameType type = GameType.fromString(args[1]);
		
		if (type == null) {
			gm.getGlobalMessenger().tell(sender, "Invalid game type!");
			return true;
		}
		
		switch(type) {
		case BATTLESHIP:
			break;
		case CHECKERS:
			successful = CheckersAutoGenerator.autogenerate(p.getLocation(), 12, args[0], gm.getPlugin());
			break;
		case TICTACTOE:
			successful = TicTacToeAutoGenerator.autogenerate(p.getLocation(), 8, args[0], gm.getPlugin());
			break;
		case MINESWEEPER:
			successful = MinesweeperAutoGenerator.autogenerate(p.getLocation(), 12, args[0], gm.getPlugin());
			break;
		case SUDOKU:
			successful = SudokuAutoGenerator.autogenerate(p.getLocation(), 9, args[0], gm.getPlugin());
			break;
		}
		
		if (!successful) {
			gm.getGlobalMessenger().tell(sender, "Could not auto-generate game.");
			return true;
		}
		
		gm.getGlobalMessenger().tell(sender, "Game with name '" + args[0] + "' generated.");
		return true;
	}

}
