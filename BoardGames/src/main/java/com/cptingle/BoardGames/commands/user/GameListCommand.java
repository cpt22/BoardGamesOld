package com.cptingle.BoardGames.commands.user;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.cptingle.BoardGames.GameMaster;
import com.cptingle.BoardGames.commands.Command;
import com.cptingle.BoardGames.commands.CommandInfo;
import com.cptingle.BoardGames.commands.Commands;
import com.cptingle.BoardGames.framework.Game;
import com.cptingle.BoardGames.messaging.Msg;
import com.cptingle.BoardGames.util.BGUtils;

@CommandInfo(
	    name    = "gamelist",
	    pattern = "games|gamel.*|lista.*",
	    usage   = "/bg games",
	    desc    = "lists all available games",
	    permission = "boardgames.use.gamelist"
	)
public class GameListCommand implements Command {

	@Override
	public boolean execute(GameMaster gm, CommandSender sender, String... args) {
		List<Game> games;
		List<String> gameString = new ArrayList<>();
		
		if (Commands.isPlayer(sender)) {
			Player p = Commands.unwrap(sender);
			games = gm.getPermittedGames(p);
		} else {
			games = gm.getGames();
		}
		for (Game g: games) {
			gameString.add(g.configName());
		}
		
		String list = BGUtils.listToString(gameString, gm.getPlugin());
		gm.getGlobalMessenger().tell(sender, Msg.MISC_LIST_GAMES.format(list));
		return false;
	}

}
