package com.cptingle.BoardGames.commands.admin;

import org.bukkit.command.CommandSender;

import com.cptingle.BoardGames.GameMaster;
import com.cptingle.BoardGames.commands.Command;
import com.cptingle.BoardGames.commands.CommandInfo;
import com.cptingle.BoardGames.messaging.Msg;

@CommandInfo(
	    name    = "reload",
	    pattern = "reload",
	    usage   = "/bg reload",
	    desc    = "Reload BoardGames config from file",
	    permission = "boardgames.admin.reload"
	)
public class ReloadCommand implements Command {

	@Override
	public boolean execute(GameMaster gm, CommandSender sender, String... args) {
	
		gm.getPlugin().onDisable();
		gm.getPlugin().onEnable();
		
		gm.getGlobalMessenger().tell(sender, Msg.CONFIG_RELOADED);
		
		return true;
	}

}
