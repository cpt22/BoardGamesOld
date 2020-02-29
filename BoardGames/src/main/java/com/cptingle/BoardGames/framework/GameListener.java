package com.cptingle.BoardGames.framework;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
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
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
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

import com.cptingle.BoardGames.BoardGames;
import com.cptingle.BoardGames.listeners.BGGlobalListener.TeleportResponse;
import com.cptingle.BoardGames.messaging.Msg;
import com.cptingle.BoardGames.region.GameRegion;

public abstract class GameListener {
	protected BoardGames plugin;
	protected Game game;
	protected GameRegion region;

	protected boolean protect;

	/**
	 * Constructor
	 * 
	 * @param plugin
	 * @param game
	 */
	public GameListener(BoardGames plugin, Game game) {
		this.plugin = plugin;
		this.game = game;
		this.region = game.getRegion();

		// Load some values from settings
		ConfigurationSection s = game.getSettings();
		this.protect = s.getBoolean("protect", true);
	}

	///////////////////////////////////////////////////////////////////////////
	// //
	// BLOCK EVENTS //
	// //
	///////////////////////////////////////////////////////////////////////////
	/**
	 * Handle Block Break Event
	 * 
	 * @param event
	 */
	public void onBlockBreak(BlockBreakEvent event) {
		if (!protect)
			return;

		if (game.inEditMode())
			return;

		if (!game.getRegion().contains(event.getBlock().getLocation()))
			return;

		event.setCancelled(true);
	}

	/**
	 * Handle Block Place Event
	 * 
	 * @param event
	 */
	public void onBlockPlace(BlockPlaceEvent event) {
		if (!protect)
			return;

		if (game.inEditMode())
			return;

		if (!game.getRegion().contains(event.getBlock().getLocation()))
			return;

		event.setCancelled(true);
	}

	/**
	 * Handle hanging break event
	 * 
	 * @param event
	 */
	public void onHangingBreak(HangingBreakEvent event) {
		// TODO: IMPLEMENT
		return;
	}

	/**
	 * Handle block burn event
	 * 
	 * @param event
	 */
	public void onBlockBurn(BlockBurnEvent event) {
		if (game.inEditMode())
			return;

		if (game.getRegion().contains(event.getBlock().getLocation()))
			event.setCancelled(true);
	}

	/**
	 * Handle block form event
	 * 
	 * @param event
	 */
	public void onBlockForm(BlockFormEvent event) {
		// TODO: IMPLEMENT
		return;
	}

	/**
	 * Handle block from to event
	 * 
	 * @param event
	 */
	public void onBlockFromTo(BlockFromToEvent event) {
		// TODO: IMPLEMENT
		return;
	}

	/**
	 * Handle block ignite event
	 * 
	 * @param event
	 */
	public void onBlockIgnite(BlockIgniteEvent event) {
		if (game.inEditMode())
			return;

		if (game.getRegion().contains(event.getBlock().getLocation()))
			event.setCancelled(true);
	}

	/**
	 * Handle sign change event
	 * 
	 * @param event
	 */
	public void onSignChange(SignChangeEvent event) {
		// TODO: IMPLEMENT
		return;
	}

	///////////////////////////////////////////////////////////////////////////
	// //
	// ENTITY EVENTS //
	// //
	///////////////////////////////////////////////////////////////////////////

	/**
	 * Handle creature spawn event
	 * 
	 * @param event
	 */
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		// TODO: IMPLEMENT
		return;
	}

	/**
	 * Handle entity change block event
	 * 
	 * @param event
	 */
	public void onEntityChangeBlock(EntityChangeBlockEvent event) {
		if (game.inEditMode())
			return;

		if (game.getRegion().contains(event.getBlock().getLocation()))
			event.setCancelled(true);
	}

	/**
	 * Handle entity combust event
	 * 
	 * @param event
	 */
	public void onEntityCombust(EntityCombustEvent event) {
		// TODO: IMPLEMENT
		return;
	}

	/**
	 * Handle entity damage event
	 * 
	 * @param event
	 */
	public void onEntityDamage(EntityDamageEvent event) {
		// TODO: IMPLEMENT
		return;
	}

	/**
	 * Handle entity death event
	 * 
	 * @param event
	 */
	public void onEntityDeath(EntityDeathEvent event) {
		// TODO: IMPLEMENT
		return;
	}

	/**
	 * Handle entity explode event
	 * 
	 * @param event
	 */
	public void onEntityExplode(EntityExplodeEvent event) {
		if (game.inEditMode())
			return;

		if (game.getRegion().contains(event.getLocation()))
			event.setCancelled(true);
	}

	/**
	 * Handle block explode event
	 * 
	 * @param event
	 */
	public void onBlockExplode(BlockExplodeEvent event) {
		if (game.inEditMode())
			return;

		if (game.getRegion().contains(event.getBlock().getLocation()))
			event.setCancelled(true);
	}

	/**
	 * Handle entity regain health event
	 * 
	 * @param event
	 */
	public void onEntityRegainHealth(EntityRegainHealthEvent event) {
		// TODO: IMPLEMENT
		return;
	}

	/**
	 * Handle food level change event
	 * 
	 * @param event
	 */
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		// TODO: IMPLEMENT
		return;
	}

	/**
	 * Handle entity target event
	 * 
	 * @param event
	 */
	public void onEntityTarget(EntityTargetEvent event) {
		// TODO: IMPLEMENT
		return;
	}

	/**
	 * Handle entity teleport event
	 * 
	 * @param event
	 */
	public void onEntityTeleport(EntityTeleportEvent event) {
		// TODO: IMPLEMENT
		return;
	}

	/**
	 * Handle potion splash event
	 * 
	 * @param event
	 */
	public void onPotionSplash(PotionSplashEvent event) {
		// TODO: IMPLEMENT
		return;
	}

	/**
	 * Handle projectile hit event
	 * 
	 * @param event
	 */
	public void onProjectileHit(ProjectileHitEvent event) {
		// TODO: IMPLEMENT
		return;
	}

	///////////////////////////////////////////////////////////////////////////
	// //
	// PLAYER EVENTS //
	// //
	///////////////////////////////////////////////////////////////////////////

	/**
	 * Handle player animation event
	 * 
	 * @param event
	 */
	public void onPlayerAnimation(PlayerAnimationEvent event) {
		// TODO: IMPLEMENT
		return;
	}

	/**
	 * Handle player bucket empty event
	 * 
	 * @param event
	 */
	public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
		// TODO: IMPLEMENT
		return;
	}

	/**
	 * Handle player chat event
	 * 
	 * @param event
	 */
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		// TODO: IMPLEMENT
		return;
	}

	/**
	 * Handle player command preprocess event
	 * 
	 * @param event
	 */
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		Player p = event.getPlayer();

		if (event.isCancelled() || !game.inGame(p)) {
			return;
		}

		// This is safe, because commands will always have at least one element.
		String base = event.getMessage().split(" ")[0];

		// Check if the entire base command is allowed.
		if (plugin.getGameMaster().isAllowed(game, base)) {
			return;
		}

		// If not, check if the specific command is allowed.
		String noslash = event.getMessage().substring(1);
		if (plugin.getGameMaster().isAllowed(game, noslash)) {
			return;
		}

		// This is dirty, but it ensures that commands are indeed blocked.
		// event.setMessage("/");

		// Cancel the event regardless.
		// event.setCancelled(true);
		game.getMessenger().tell(p, Msg.MISC_COMMAND_NOT_ALLOWED);
	}

	/**
	 * Handle player drop item event
	 * 
	 * @param event
	 */
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		// TODO: IMPLEMENT
		return;
	}

	/**
	 * Handle player interact event
	 * 
	 * @param event
	 */
	public void onPlayerInteract(PlayerInteractEvent event) {
		// TODO: IMPLEMENT
		return;
	}

	/**
	 * Handle player join event
	 * 
	 * @param event
	 */
	public void onPlayerJoin(PlayerJoinEvent event) {
		// TODO: IMPLEMENT
		return;
	}

	/**
	 * Handle player kick event
	 * 
	 * @param event
	 */
	public void onPlayerKick(PlayerKickEvent event) {
		if (game.getPlayers().contains(event.getPlayer()))
			game.playerLeave(event.getPlayer());
		return;
	}

	/**
	 * Handle player quit event
	 * 
	 * @param event
	 */
	public void onPlayerQuit(PlayerQuitEvent event) {
		if (game.getPlayers().contains(event.getPlayer()))
			game.playerLeave(event.getPlayer());
		return;
	}

	/**
	 * Handle player respawn event
	 * 
	 * @param event
	 */
	public boolean onPlayerRespawn(PlayerRespawnEvent event) {
		// TODO: IMPLEMENT
		return false;
	}

	/**
	 * Handle player teleport event
	 * 
	 * @param event
	 */
	public TeleportResponse onPlayerTeleport(PlayerTeleportEvent event) {
		// TODO: IMPLEMENT
		return TeleportResponse.IDGAF;
	}

	/**
	 * Handle player login event
	 * 
	 * @param event
	 */
	public void onPlayerPreLogin(PlayerLoginEvent event) {
		// TODO: IMPLEMENT
		return;
	}

	/**
	 * Handle vehicle exit event
	 * 
	 * @param event
	 */
	public void onVehicleExit(VehicleExitEvent event) {
		// TODO: IMPLEMENT
		return;
	}

	/**
	 * Handle opening of inventory
	 * 
	 * @param event
	 */
	public void onInventoryOpen(InventoryOpenEvent event) {
		return;
	}

	/**
	 * Handle interaction with inventory
	 * 
	 * @param event
	 */
	public void onInventoryInteract(InventoryInteractEvent event) {
		return;
	}

}
