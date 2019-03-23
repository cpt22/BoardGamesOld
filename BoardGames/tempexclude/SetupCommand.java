package com.cptingle.BoardGames.commands.setup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ConversationAbandonedListener;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.cptingle.BoardGames.GameMaster;
import com.cptingle.BoardGames.commands.Command;
import com.cptingle.BoardGames.commands.CommandInfo;
import com.cptingle.BoardGames.commands.Commands;
import com.cptingle.BoardGames.framework.Game;
import com.cptingle.BoardGames.messaging.Msg;
import com.cptingle.BoardGames.region.GameRegion;

@CommandInfo(name = "setup", 
pattern = "setup",
usage = "/bg setup <game>",
desc = "enter setup mode for a game",
permission = "boardgames.setup.setup")
public class SetupCommand implements Command {

	@Override
	public boolean execute(GameMaster gm, CommandSender sender, String... args) {
		if (!Commands.isPlayer(sender)) {
			gm.getGlobalMessenger().tell(sender, Msg.MISC_NOT_FROM_CONSOLE);
			return true;
		}

		// Get the game
		Game game;
		if (args.length == 0) {
			List<Game> games = gm.getGames();
			if (games.size() > 1) {
				return false;
			}
			game = games.get(0);
		} else {
			game = gm.getGameWithName(args[0]);
			if (game == null) {
				gm.getGlobalMessenger().tell(sender,
						"There is no game with the name " + ChatColor.RED + args[0] + ChatColor.RESET + ".");
				gm.getGlobalMessenger().tell(sender,
						"Type " + ChatColor.YELLOW + "/bg addgame " + args[0] + ChatColor.RESET + " to create it!");
				return true;
			}
		}
		Player player = Commands.unwrap(sender);

		// Create the setup object
		Setup setup = new Setup(player, game);

		// Register it as an event listener
		gm.getPlugin().getServer().getPluginManager().registerEvents(setup, gm.getPlugin());

		// Set up the conversation
		Conversation convo = new Conversation(gm.getPlugin(), player, setup);
		setup.convo = convo;
		convo.addConversationAbandonedListener(setup);
		convo.setLocalEchoEnabled(false);
		convo.begin();
		return true;
	}

	/**
	 * The internal Setup class has three roles; it is the prompt and the abandon
	 * listener for the Conversation initiated by the setup command, but it is also
	 * an event listener for the interact event, to handle the Toolbox events.
	 */
	private class Setup implements Prompt, ConversationAbandonedListener, Listener {
		private Player player;
		private Game game;
		private Conversation convo;

		private boolean enabled;
		private boolean allowFlight;
		private boolean flying;
		private ItemStack[] armor;
		private ItemStack[] items;

		private List<String> missing;
		private String next;

		public Setup(Player player, Game game) {
			this.player = player;
			this.game = game;

			// Store player and game state
			this.enabled = game.isEnabled();
			this.allowFlight = player.getAllowFlight();
			this.flying = player.isFlying();
			this.armor = player.getInventory().getArmorContents();
			this.items = player.getInventory().getContents();

			// Change state
			game.setEnabled(false);
			player.setAllowFlight(true);
			player.setFlying(true);
			player.getInventory().clear();
			player.getInventory().setContents(getToolbox());
			player.getInventory().setHeldItemSlot(0);

			this.missing = new ArrayList<>();
			this.next = color(
					String.format("Setup Mode for game &a%s&r. Type &e?&r for help.", "&a" + game.configName() + "&r"));

			GameRegion region = game.getRegion();
			if (!region.isSetup()) {
				// Region points
				if (!region.isDefined()) {
					missing.add("p1");
					missing.add("p2");
				}

				// Board points
				if (!region.isBoardDefined()) {
					missing.add("b1");
					missing.add("b2");
				}

				// Game, lobby, and spectator warps
				if (region.getP1Spawn() == null)
					missing.add("p1spawn");
				if (region.getP2Spawn() == null)
					missing.add("p2spawn");
			}
		}

		// ====================================================================
		// Toolbox handler
		// ====================================================================

		private ItemStack[] getToolbox() {
			// Game region tool
			ItemStack areg = makeTool(Material.GOLDEN_AXE, AREG_NAME, color("Set &ep1"), color("Set &ep2"));
			// Board region tool
			ItemStack breg = makeTool(Material.GOLDEN_HOE, BREG_NAME, color("Set &el1"), color("Set &el2"));
			// Spawns tool
			ItemStack spawns = makeTool(Material.GOLDEN_SWORD, SPAWNS_NAME, color("&eSet &rplayer 1 spawn"),
					color("&eSet &rplayer 2 spawn"));
			// Round 'em up.
			return new ItemStack[] { null, areg, breg, spawns, null, null, null };
		}

		private ItemStack makeTool(Material mat, String name, String left, String right) {
			ItemStack tool = new ItemStack(mat);
			ItemMeta meta = tool.getItemMeta();
			meta.setDisplayName(name);
			meta.setLore(Arrays.asList(color("&9Left&r: &r" + left), color("&cRight&r: &r" + right)));
			tool.setItemMeta(meta);
			return tool;
		}

		private boolean isTool(ItemStack item) {
			if (item == null || item.getType() == Material.AIR)
				return false;

			String name = item.getItemMeta().getDisplayName();
			if (name == null)
				return false;

			// Just check the names of each tool
			return name.equals(AREG_NAME) || name.equals(BREG_NAME) || name.equals(SPAWNS_NAME)
					|| name.equals(MANUAL_NAME);
		}

		@EventHandler
		public void onDisable(PluginDisableEvent event) {
			if (event.getPlugin().getName().equals(game.getPlugin().getName()) && player.isConversing()) {
				player.abandonConversation(convo);
			}
		}

		@EventHandler
		public void onQuit(PlayerQuitEvent event) {
			if (event.getPlayer().equals(player) && player.isConversing()) {
				player.abandonConversation(convo);
			}
		}

		@EventHandler
		public void onBreak(BlockBreakEvent event) {
			Player p = event.getPlayer();
			if (!p.equals(player))
				return;

			ItemStack tool = p.getInventory().getItemInMainHand();
			if (!isTool(tool))
				return;

			event.setCancelled(true);
			tool.getItemMeta().setUnbreakable(true);
		}

		@EventHandler
		public void onDrop(PlayerDropItemEvent event) {
			Player p = event.getPlayer();
			if (!p.equals(player))
				return;

			event.setCancelled(true);
			tell(p, "You can't drop the toolbox items.");
		}

		@EventHandler(priority = EventPriority.LOWEST)
		public void onInteract(PlayerInteractEvent event) {
			Player p = event.getPlayer();
			if (!p.equals(player))
				return;

			if (event.getHand() == EquipmentSlot.OFF_HAND)
				return;

			ItemStack tool = p.getInventory().getItemInMainHand();
			if (!isTool(tool))
				return;

			String name = tool.getItemMeta().getDisplayName();
			if (name.equals(AREG_NAME)) {
				if (!game(event))
					return;
			} else if (name.equals(BREG_NAME)) {
				if (!board(event))
					return;
			} else if (name.equals(SPAWNS_NAME)) {
				if (!spawns(event))
					return;
			}

			event.setUseItemInHand(Event.Result.DENY);
			event.setCancelled(true);

			player.sendRawMessage(getPromptText(null));
		}

		private boolean game(PlayerInteractEvent event) {
			if (!event.hasBlock()) {
				return false;
			}

			Location loc = event.getClickedBlock().getLocation();
			region(event.getAction(), "p1", "p2", loc);
			return true;
		}

		private boolean board(PlayerInteractEvent event) {
			if (!event.hasBlock()) {
				return false;
			}

			Location loc = event.getClickedBlock().getLocation();
			region(event.getAction(), "b1", "b2", loc);
			return true;
		}

		private boolean region(Action action, String lower, String upper, Location loc) {
			switch (action) {
			case LEFT_CLICK_BLOCK:
				regions(lower, loc);
				return true;
			case RIGHT_CLICK_BLOCK:
				regions(upper, loc);
				return true;
			}
			return false;
		}

		private boolean spawns(PlayerInteractEvent event) {
			if (!event.hasBlock()) {
				return false;
			}

			Location l = event.getClickedBlock().getLocation();
			fix(l);
			switch (event.getAction()) {
			case LEFT_CLICK_BLOCK:
				spawns("p1", l);
				return true;
			case RIGHT_CLICK_BLOCK:
				spawns("p2", l);
				return true;
			}
			return false;
		}

		private void fix(Location loc) {
			loc.setX(loc.getBlockX() + 0.5D);
			loc.setY(loc.getBlockY() + 1);
			loc.setZ(loc.getBlockZ() + 0.5D);
		}

		private String[] spawnArray = new String[] { "p1spawn", "p2spawn" };

		private static final String AREG_NAME = "Game Region";
		private static final String BREG_NAME = "Board Region";
		private static final String SPAWNS_NAME = "Spawnpoints";
		private static final String MANUAL_NAME = "Manual";

		// ====================================================================
		// Conversation end handler (items, state, etc.)
		// ====================================================================

		@Override
		public void conversationAbandoned(ConversationAbandonedEvent event) {
			// Unregister listener
			HandlerList.unregisterAll(this);

			// Restore player and game state
			game.setEnabled(enabled);
			game.getRegion().save();
			game.getRegion().reloadAll();
			player.getInventory().setContents(items);
			player.getInventory().setArmorContents(armor);

			// setAllowFlight(false) also handles setFlying(false)
			player.setAllowFlight(allowFlight);
			if (allowFlight) {
				player.setFlying(flying);
			}
		}

		// ====================================================================
		// Prompt methods
		// ====================================================================

		@Override
		public String getPromptText(ConversationContext context) {
			return ChatColor.GREEN + "[EasyCheckers] " + ChatColor.RESET + next;
		}

		@Override
		public boolean blocksForInput(ConversationContext context) {
			return true;
		}

		@Override
		public Prompt acceptInput(ConversationContext context, String s) {
			// Check regexes at the bottom of the file
			return s.matches(HELP) ? help()
					: s.matches(MISSING) ? missing()
							: s.matches(EXPAND) ? expand(s)
									: s.matches(EXPHELP) ? expandOptions()
											: s.matches(SHOW) ? show(context, s)
													: s.matches(SHOWHELP) ? showOptions()
															: s.matches(DONE) ? done() : invalidInput();
		}

		// ====================================================================
		// Input handlers
		// ====================================================================

		/**
		 * Help
		 */
		private Prompt help() {
			StringBuilder buffy = new StringBuilder();
			buffy.append("\nAvailable input:");
			buffy.append("\n&r&e exp   &7expand a region");
			buffy.append("\n&r&e show   &7show a region or spawn");
			buffy.append("\n&r&e miss   &7show missing warps and points");
			buffy.append("\n&r&e done   &7exit out of Setup Mode");
			buffy.append("\n&r&7Read &bitem tooltips&r&7 for info about each tool.");
			next = color(buffy.toString());
			return this;
		}

		/**
		 * Regions
		 */
		private Prompt regions(String s, Location loc) {
			// Change worlds if needed
			if (!inGameWorld()) {
				String msg = String.format("Changed world of game %s from %s to %s.",
						ChatColor.GREEN + game.configName() + ChatColor.RESET,
						ChatColor.YELLOW + game.getWorld().getName() + ChatColor.RESET,
						ChatColor.YELLOW + loc.getWorld().getName() + ChatColor.RESET);
				game.setWorld(loc.getWorld());
				tell(player, msg);
			}
			game.getRegion().set(s, loc);
			next = formatYellow("Region point %s was set.", s);
			missing.remove(s);
			return this;
		}

		/**
		 * Expand
		 */
		private Prompt expand(String s) {
			String[] parts = s.split(" ");

			int amount = Integer.parseInt(parts[1]);

			if (parts[2].equalsIgnoreCase("up")) {
				game.getRegion().expandUp(amount);

			} else if (parts[2].equalsIgnoreCase("down")) {
				game.getRegion().expandDown(amount);

			} else {
				game.getRegion().expandOut(amount);
			}
			next = color(String.format("Expanded region &e%s&r by &e%s&r blocks.", parts[2], parts[1]));
			return this;
		}

		/**
		 * Spawns
		 */
		private Prompt spawns(String s, Location loc) {
			if (s.equalsIgnoreCase("p1") || s.equalsIgnoreCase("player1"))
				s = "p1spawn";
			if (s.equalsIgnoreCase("p2") || s.equalsIgnoreCase("player2"))
				s = "p2spawn";

			// World change stuff for the game warp

			missing.remove(s);
			game.getRegion().set(s, loc);
			next = formatYellow("Warp point %s was set.", s);
			return this;
		}

		/**
		 * Show things.
		 */
		private Prompt show(ConversationContext context, String s) {
			GameRegion region = game.getRegion();
			String toShow = s.split(" ")[1].trim();

			// Regions
			if (toShow.equalsIgnoreCase("r") || toShow.equalsIgnoreCase("regions")) {
				if (region.isDefined()) {
					region.showRegion(player);
					if (region.isBoardDefined()) {
						region.showBoardRegion(player);
						next = formatYellow("Showing both %s.", "regions");
					} else {
						next = formatYellow("Showing %s (board region not defined).", "game region");
					}
				} else if (region.isBoardDefined()) {
					region.showBoardRegion(player);
					next = formatYellow("Showing %s (game region not defined).", "board region");
				} else {
					next = "No regions have been defined yet.";
				}
				return this;
			} else if (toShow.equalsIgnoreCase("gr")) {
				if (region.isDefined()) {
					next = formatYellow("Showing %s.", "game region");
					region.showRegion(player);
				} else {
					next = "The region has not been defined yet.";
				}
				return this;
			} else if (toShow.equalsIgnoreCase("br")) {
				if (region.isBoardDefined()) {
					next = formatYellow("Showing %s.", "board region");
					region.showBoardRegion(player);
				} else {
					next = "The board region has not been defined yet.";
				}
				return this;
			}
			game.getGlobalMessenger().tell(player, "GOT HERE");
			// Spawns
			if (toShow.matches("spawns")) {
				next = formatYellow("Showing %s spawn.", toShow);
				Location loc;
				loc = toShow.equals("spawns") ? region.getP1Spawn()
						: toShow.equals("spawns") ? region.getP2Spawn() : null;
				region.showSpawns(player);
				return this;
			}

			// Show the "show help", if invalid thing
			return acceptInput(context, "show ?");
		}

		/**
		 * Missing points and warps
		 */
		private Prompt missing() {
			if (missing.isEmpty()) {
				next = "All required points and spawns have been set!";
			} else {
				next = "Missing points and spawns: " + getMissing();
			}
			return this;
		}

		/**
		 * Expand options
		 */
		private Prompt expandOptions() {
			StringBuilder buffy = new StringBuilder();
			buffy.append("\nUsage: &eexp <region> <amount> <direction>");

			buffy.append("\n\n&r&7Variable details:");
			buffy.append("\n&r&7 region: &rar&7 (game region)");
			buffy.append("\n&r&7 amount: number of blocks to expand by");
			buffy.append("\n&r&7 direction: &rup&7, &rdown&7, or &routs&7");

			buffy.append("\n\n&r&7Examples:");
			buffy.append("\n&r exp ar 5 up   &7expand game region up by 5");
			next = color(buffy.toString());
			return this;
		}

		/**
		 * Show options
		 */
		private Prompt showOptions() {
			StringBuilder buffy = new StringBuilder();
			buffy.append("\nUsage: &eshow <thing>");

			buffy.append("\n\n&r&7Possible things to show:");
			buffy.append("\n&r&7 regions: &rgr&7 (game region) or &rbr&7 (board region) or &rr&7 (both)");
			buffy.append("\n&r&7 spawns: &rp1&7, &rp2&7");

			buffy.append("\n\n&r&7Examples:");
			buffy.append("\n&r show gr   &7show game region");
			next = color(buffy.toString());
			return this;
		}

		/**
		 * Done!
		 */
		private Prompt done() {
			if (missing.isEmpty()) {
				tell(player, "Setup complete! Game is ready to be used!");
			} else {
				tell(player, "Setup incomplete. Missing points and warps: " + getMissing());
			}
			return Prompt.END_OF_CONVERSATION;
		}

		/**
		 * Invalid input
		 */
		private Prompt invalidInput() {
			next = formatYellow("Invalid input. Type %s for help", "?");
			return this;
		}

		// ====================================================================
		// Auxiliary methods
		// ====================================================================

		private String getMissing() {
			StringBuilder buffy = new StringBuilder();
			for (String m : missing) {
				buffy.append("\n").append(m);
			}
			return buffy.toString();
		}

		private String color(String s) {
			return ChatColor.translateAlternateColorCodes('&', s);
		}

		private boolean inGameWorld() {
			return player.getWorld().getName().equals(game.getWorld().getName());
		}

		private void tell(Conversable whom, String msg) {
			whom.sendRawMessage(ChatColor.GREEN + "[EasyCheckers] " + ChatColor.RESET + msg);
		}

		private String formatYellow(String msg, String arg) {
			return String.format(msg, ChatColor.YELLOW + arg + ChatColor.RESET);
		}

		private String getName(Location l) {
			return l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ();
		}

		// ====================================================================
		// Regular expressions for the input
		// ====================================================================

		private static final String HELP = "[?]|h(elp)?";
		private static final String MISSING = "miss(ing)?";
		private static final String EXPAND = "exp(and)? [1-9][0-9]* (up|down|out)";
		private static final String EXPHELP = "exp(and)?";
		private static final String SHOW = "show (r|gr|br|p1spawn|p2spawn)";
		private static final String SHOWHELP = "show";
		private static final String DONE = "done|quit|stop|end";
	}

}
