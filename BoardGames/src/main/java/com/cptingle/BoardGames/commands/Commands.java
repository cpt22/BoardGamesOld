package com.cptingle.BoardGames.commands;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.cptingle.BoardGames.GameMaster;
import com.cptingle.BoardGames.framework.Game;
import com.cptingle.BoardGames.messaging.Msg;

public class Commands {
	/**
	 * Unwrap the given CommandSender reference, in case it is a proxy.
	 * <p>
	 * Because plugins like CommandSigns use horrible proxy hacks to do what they
	 * need to do, a Player reference is not necessarily a real Player, and using
	 * that reference brings MobGame into an inconsistent state.
	 * <p>
	 * The method returns the "real" Player reference by making a UUID lookup.
	 *
	 * @param sender a CommandSender reference, possibly a proxy, non-null
	 * @return the real Player reference, possibly the same as the argument
	 */
	public static Player unwrap(CommandSender sender) {
		Player proxy = (Player) sender;
		UUID id = proxy.getUniqueId();
		return Bukkit.getPlayer(id);
	}

	public static boolean isPlayer(CommandSender sender) {
		return (sender instanceof Player);
	}

	public static Game getGameToJoin(GameMaster gm, Player p, String arg1) {
		// Check if BoardGames is enabled first.
		if (!gm.isEnabled()) {
			gm.getGlobalMessenger().tell(p, Msg.JOIN_NOT_ENABLED);
			return null;
		}

		// Then check if we have permission at all.
		List<Game> games = gm.getPermittedGames(p);
		if (games.isEmpty()) {
			gm.getGlobalMessenger().tell(p, Msg.JOIN_NO_PERMISSION);
			return null;
		}

		// Then check if we have any enabled games.
		games = gm.getEnabledGames(games);
		if (games.isEmpty()) {
			gm.getGlobalMessenger().tell(p, Msg.JOIN_NOT_ENABLED);
			return null;
		}

		// The game to join.
		Game game = null;

		// Branch on whether there's an argument or not.
		if (arg1 != null) {
			game = gm.getGameWithName(arg1);
			if (game == null) {
				gm.getGlobalMessenger().tell(p, Msg.GAME_DOES_NOT_EXIST);
				return null;
			}

			if (!games.contains(game)) {
				gm.getGlobalMessenger().tell(p, Msg.JOIN_ARENA_NOT_ENABLED);
				return null;
			}
		} else {
			if (games.size() > 1) {
				gm.getGlobalMessenger().tell(p, Msg.JOIN_ARG_NEEDED);
				// gm.getGlobalMessenger().tell(p,
				// Msg.MISC_LIST_GAMES.format(ECUtils.listToString(games)));
				return null;
			}
			game = games.get(0);
		}

		// If player is in a boat/minecart, eject!
		if (p.isInsideVehicle()) {
			p.leaveVehicle();
		}

		// If player is in a bed, unbed!
		if (p.isSleeping()) {
			p.kickPlayer("Banned for life... Nah, just don't join from a bed ;)");
			return null;
		}

		return game;
	}

}
