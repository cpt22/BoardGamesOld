package com.cptingle.BoardGames.commands.admin;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.cptingle.BoardGames.GameMaster;
import com.cptingle.BoardGames.commands.Command;
import com.cptingle.BoardGames.commands.CommandInfo;
import com.cptingle.BoardGames.framework.Game;
import com.cptingle.BoardGames.messaging.Msg;

@CommandInfo(
	    name    = "enable",
	    pattern = "enable|on",
	    usage   = "/bg enable",
	    desc    = "enable BoardGames or individual arenas",
	    permission = "boardgames.admin.enable"
	)
public class EnableCommand implements Command {

	@Override
	public boolean execute(GameMaster gm, CommandSender sender, String... args) {
		// Grab the argument, if any.
        String arg1 = (args.length > 0 ? args[0] : "");
        
        if (arg1.equals("all")) {
            for (Game game : gm.getGames()) {
                enable(gm, game, sender);
            }
            return true;
        }
        
        if (!arg1.equals("")) {
            Game game = gm.getGameWithName(arg1);
            if (game == null) {
                gm.getGlobalMessenger().tell(sender, Msg.GAME_DOES_NOT_EXIST);
                return true;
            }
            enable(gm, game, sender);
            return true;
        }
        
        gm.setEnabled(true);
        gm.saveConfig();
        gm.getGlobalMessenger().tell(sender, "EasyCheckers " + ChatColor.GREEN + "enabled");
        return true;
    }
    
    private void enable(GameMaster gm, Game game, CommandSender sender) {
        game.setEnabled(true);
        game.getPlugin().saveConfig();
        gm.reloadGame(game.getName());
        game.getPlugin().getLogger().severe("" + game.getGlobalMessenger());
        game.getGlobalMessenger().tell(sender, "Game '" + game.configName() + "' " + ChatColor.GREEN + "enabled");
    }

}
