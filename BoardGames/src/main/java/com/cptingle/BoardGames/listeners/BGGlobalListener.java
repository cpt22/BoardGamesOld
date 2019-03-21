package com.cptingle.BoardGames.listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import com.cptingle.BoardGames.BoardGames;
import com.cptingle.BoardGames.GameMaster;
import com.cptingle.BoardGames.framework.Game;

public class BGGlobalListener implements Listener {

	private BoardGames plugin;
	private GameMaster gm;

	public BGGlobalListener(BoardGames plugin, GameMaster gm) {
		this.plugin = plugin;
		this.gm = gm;
	}

	///////////////////////////////////////////////////////////////////////////
	// //
	// BLOCK EVENTS //
	// //
	///////////////////////////////////////////////////////////////////////////

	// TODO watch block physics, piston extend, and piston retract events
	@EventHandler(priority = EventPriority.HIGHEST)
	public void blockBreak(BlockBreakEvent event) {
		for (Game game : gm.getGames())
			game.getEventListener().onBlockBreak(event);
	}

	@EventHandler
	public void hangingBreak(HangingBreakEvent event) {
		for (Game game : gm.getGames())
			game.getEventListener().onHangingBreak(event);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void blockBurn(BlockBurnEvent event) {
		for (Game game : gm.getGames())
			game.getEventListener().onBlockBurn(event);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void blockForm(BlockFormEvent event) {
		for (Game game : gm.getGames())
			game.getEventListener().onBlockForm(event);
	}

	//// TODO: See GameListener.onBlockFromTo()
	// @EventHandler(priority = EventPriority.NORMAL)
	// public void blockFromTo(BlockFromToEvent event) {
	// for (Game game : gm.getGames())
	// game.getEventListener().onBlockFromTo(event);
	// }

	@EventHandler(priority = EventPriority.HIGH)
	public void blockIgnite(BlockIgniteEvent event) {
		for (Game game : gm.getGames())
			game.getEventListener().onBlockIgnite(event);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void blockPlace(BlockPlaceEvent event) {
		for (Game game : gm.getGames())
			game.getEventListener().onBlockPlace(event);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void signChange(SignChangeEvent event) {
		/*if (!event.getPlayer().hasPermission("mobgame.setup.leaderboards")) {
			return;
		}

		if (!event.getLine(0).startsWith("[MA]")) {
			return;
		}

		String text = event.getLine(0).substring((4));
		Game game;
		Stats stat;

		if ((game = gm.getGameWithName(text)) != null) {
			game.getEventListener().onSignChange(event);
			setSignLines(event, ChatColor.GREEN + "MobGame", ChatColor.YELLOW + game.gameName(),
					ChatColor.AQUA + "Players", "---------------");
		} else if ((stat = Stats.getByShortName(text)) != null) {
			setSignLines(event, ChatColor.GREEN + "", "", ChatColor.AQUA + stat.getFullName(), "---------------");
			gm.getGlobalMessenger().tell(event.getPlayer(), "Stat sign created.");
		}*/
	}

	private void setSignLines(SignChangeEvent event, String s1, String s2, String s3, String s4) {
		event.setLine(0, s1);
		event.setLine(1, s2);
		event.setLine(2, s3);
		event.setLine(3, s4);
	}

	///////////////////////////////////////////////////////////////////////////
	// //
	// ENTITY EVENTS //
	// //
	///////////////////////////////////////////////////////////////////////////

	@EventHandler(priority = EventPriority.LOW)
	public void creatureSpawn(CreatureSpawnEvent event) {
		for (Game game : gm.getGames())
			game.getEventListener().onCreatureSpawn(event);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityChangeBlock(EntityChangeBlockEvent event) {
		for (Game game : gm.getGames())
			game.getEventListener().onEntityChangeBlock(event);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void entityCombust(EntityCombustEvent event) {
		for (Game game : gm.getGames())
			game.getEventListener().onEntityCombust(event);
	}

	@EventHandler(priority = EventPriority.LOW)
	public void entityDamage(EntityDamageEvent event) {
		for (Game game : gm.getGames())
			game.getEventListener().onEntityDamage(event);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void entityDeath(EntityDeathEvent event) {
		for (Game game : gm.getGames())
			game.getEventListener().onEntityDeath(event);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void entityExplode(EntityExplodeEvent event) {
		for (Game game : gm.getGames())
			game.getEventListener().onEntityExplode(event);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void blockExplode(BlockExplodeEvent event) {
		// Create a copy of the block list so we can clear and re-add
		List<Block> blocks = new ArrayList<>(event.blockList());

		// Account for Spigot's messy extra event
		EntityExplodeEvent fake = new EntityExplodeEvent(null, event.getBlock().getLocation(), blocks,
				event.getYield());
		entityExplode(fake);

		// Copy the values over
		event.setCancelled(fake.isCancelled());
		event.blockList().clear();
		event.blockList().addAll(fake.blockList());
		event.setYield(fake.getYield());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void entityRegainHealth(EntityRegainHealthEvent event) {
		for (Game game : gm.getGames())
			game.getEventListener().onEntityRegainHealth(event);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void entityFoodLevelChange(FoodLevelChangeEvent event) {
		for (Game game : gm.getGames())
			game.getEventListener().onFoodLevelChange(event);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void entityTarget(EntityTargetEvent event) {
		for (Game game : gm.getGames())
			game.getEventListener().onEntityTarget(event);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void entityTeleport(EntityTeleportEvent event) {
		for (Game game : gm.getGames()) {
			game.getEventListener().onEntityTeleport(event);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void potionSplash(PotionSplashEvent event) {
		for (Game game : gm.getGames()) {
			game.getEventListener().onPotionSplash(event);
		}
	}

	///////////////////////////////////////////////////////////////////////////
	// //
	// PLAYER EVENTS //
	// //
	///////////////////////////////////////////////////////////////////////////

	@EventHandler(priority = EventPriority.NORMAL)
	public void playerAnimation(PlayerAnimationEvent event) {
		if (!gm.isEnabled())
			return;
		for (Game game : gm.getGames())
			game.getEventListener().onPlayerAnimation(event);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void playerBucketEmpty(PlayerBucketEmptyEvent event) {
		if (!gm.isEnabled())
			return;
		for (Game game : gm.getGames())
			game.getEventListener().onPlayerBucketEmpty(event);
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void playerChat(AsyncPlayerChatEvent event) {
		if (!gm.isEnabled())
			return;

		Game game = gm.getGameWithPlayer(event.getPlayer());
		if (game == null || !game.hasIsolatedChat())
			return;

		event.getRecipients().retainAll(game.getAllPlayers());
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void playerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		if (!gm.isEnabled())
			return;
		for (Game game : gm.getGames())
			game.getEventListener().onPlayerCommandPreprocess(event);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void playerDropItem(PlayerDropItemEvent event) {
		if (!gm.isEnabled())
			return;
		for (Game game : gm.getGames())
			game.getEventListener().onPlayerDropItem(event);
	}

	// HIGHEST => after SignShop
	@EventHandler(priority = EventPriority.HIGHEST)
	public void playerInteract(PlayerInteractEvent event) {
		if (!gm.isEnabled())
			return;
		for (Game game : gm.getGames())
			game.getEventListener().onPlayerInteract(event);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void playerJoin(PlayerJoinEvent event) {
		//InventoryManager.restoreFromFile(plugin, event.getPlayer());
		//if (!gm.notifyOnUpdates() || !event.getPlayer().isOp())
		//	return;

		//VersionChecker.checkForUpdates(plugin, event.getPlayer());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void playerKick(PlayerKickEvent event) {
		for (Game game : gm.getGames())
			game.getEventListener().onPlayerKick(event);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void playerQuit(PlayerQuitEvent event) {
		for (Game game : gm.getGames())
			game.getEventListener().onPlayerQuit(event);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void playerRespawn(PlayerRespawnEvent event) {
		for (Game game : gm.getGames()) {
			if (game.getEventListener().onPlayerRespawn(event)) {
				return;
			}
		}

		//plugin.restoreInventory(event.getPlayer());
	}

	public enum TeleportResponse {
		ALLOW, REJECT, IDGAF
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void playerTeleport(PlayerTeleportEvent event) {
		/*if (!gm.isEnabled())
			return;

		boolean allow = true;
		for (Game game : gm.getGames()) {
			TeleportResponse r = game.getEventListener().onPlayerTeleport(event);

			// If just one game allows, uncancel and stop.
			switch (r) {
			case ALLOW:
				event.setCancelled(false);
				return;
			case REJECT:
				allow = false;
				break;
			default:
				break;
			}
		}

		// Only cancel if at least one game has rejected the teleport.
		if (!allow) {
			event.setCancelled(true);
		}*/
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void playerPreLogin(PlayerLoginEvent event) {
		for (Game game : gm.getGames()) {
			game.getEventListener().onPlayerPreLogin(event);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void vehicleExit(VehicleExitEvent event) {
		for (Game game : gm.getGames()) {
			game.getEventListener().onVehicleExit(event);
		}
	}

	///////////////////////////////////////////////////////////////////////////
	// //
	// WORLD EVENTS //
	// //
	///////////////////////////////////////////////////////////////////////////

	@EventHandler(priority = EventPriority.NORMAL)
	public void worldLoadEvent(WorldLoadEvent event) {
		gm.loadGamesInWorld(event.getWorld().getName());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void worldUnloadEvent(WorldUnloadEvent event) {
		gm.unloadGamesInWorld(event.getWorld().getName());
	}

}
