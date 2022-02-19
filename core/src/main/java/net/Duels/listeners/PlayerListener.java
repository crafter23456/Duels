package net.Duels.listeners;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.messages.ActionBar;
import net.Duels.Duel;
import net.Duels.arenas.Arena;
import net.Duels.config.impl.SignConfig;
import net.Duels.hologram.PlayerHologram;
import net.Duels.player.PlayerObject;
import net.Duels.utility.*;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class PlayerListener implements Listener {

	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		PlayerObject playerObject = Duel.getPlayerController().addPlayer(uuid);
		if (playerObject == null) {
			return;
		}

		player.setGameMode(GameMode.ADVENTURE);
		player.setMaxHealth(20.0);
		player.setHealth(player.getMaxHealth());
		player.setFoodLevel(20);
		player.setFireTicks(0);
		PlayerUtils.teleportToLobby(player);
		KitUtils.joinItem(player, playerObject);
		for (PotionEffect effect : player.getActivePotionEffects()) {
			player.removePotionEffect(effect.getType());
		}
		if ((boolean) Duel.getMainConfig().getMapping().getOrDefault("option.disable-join-message", true)) {
			event.setJoinMessage("");
		}
		Duel.getPlayerController().addPlayer(uuid);
		Duel.getBossbar().addPlayer(player, "");
		Duel.getHologramController().addPlayerHologram(player);
	}

	@EventHandler
	public void onPlayerQuitEvent(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		PlayerObject playerObject = Duel.getPlayerController().getPlayer(uuid);
		if (playerObject == null) {
			return;
		}

		if (playerObject.getQuitTask() != null) {
			Duel.getInstance().getServer().getScheduler().cancelTask(playerObject.getQuitTask().getTaskId());
		}
		if (playerObject.inArena()) {
			playerObject.getArena().removePlayer(playerObject);
		}
		Duel.getPlayerController().removePlayer(uuid);
		if ((boolean) Duel.getMainConfig().getMapping().getOrDefault("option.disable-quit-message", true)) {
			event.setQuitMessage("");
		}
		if (playerObject.isSpectator()) {
			playerObject.getArena().removeSpectator(playerObject);
		}
		Duel.getPlayerController().removePlayer(uuid);
		Duel.getBossbar().removePlayer(player);
		Duel.getHologramController().removePlayerHologram(player);
	}

	@EventHandler
	public void onPlayerMoveEvent(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		PlayerObject playerObject = Duel.getPlayerController().getPlayer(uuid);
		if (playerObject.inArena()) {
			Arena arena = playerObject.getArena();
			if (playerObject.isSpectator()) {
				if (event.getTo().getY() <= 0) {
					player.teleport(arena.getSpectatorLocation());
				}
			}
			if (arena.getArenaState() == Arena.ArenaState.WAIT) {
				if (event.getTo().getY() <= 0) {
					player.teleport(arena.getWaitingLocation());
				}
			}
			if (arena.getArenaState() == Arena.ArenaState.PLAY) {
				if (event.getTo().getY() <= 0) {
					player.teleport(arena.getSpectatorLocation());
					playerObject.setDeaths(playerObject.getDeaths() + 1);
					arena.addSpectator(playerObject);
					arena.endGame();
				}
			}
		}
	}

	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent event) {
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		PlayerObject playerObject = Duel.getPlayerController().getPlayer(uuid);

		if (playerObject == null) {
			if (Duel.getPlayerController().canRegister(uuid)) {
				playerObject = Duel.getPlayerController().addPlayer(uuid);
				if (playerObject == null) {
					return;
				} else {
					player.setGameMode(GameMode.ADVENTURE);
					player.setMaxHealth(20.0);
					player.setHealth(player.getMaxHealth());
					player.setFoodLevel(20);
					player.setFireTicks(0);
					PlayerUtils.teleportToLobby(player);
					KitUtils.joinItem(player, playerObject);
					for (PotionEffect effect : player.getActivePotionEffects()) {
						player.removePotionEffect(effect.getType());
					}
					Duel.getBossbar().addPlayer(player, "");
					Duel.getHologramController().addPlayerHologram(player);
				}
			} else {
				return;
			}
		} else {
			if (!Duel.getPlayerController().canRegister(uuid)) {
				playerObject.getPlayer()
						.setScoreboard(Duel.getInstance().getServer().getScoreboardManager().getMainScoreboard());
				if (playerObject.getQuitTask() != null) {
					Duel.getInstance().getServer().getScheduler().cancelTask(playerObject.getQuitTask().getTaskId());
				}
				if (playerObject.inArena()) {
					playerObject.getArena().removePlayer(playerObject);
				}
				Duel.getPlayerController().removePlayer(uuid);
				if (playerObject.isSpectator()) {
					playerObject.getArena().removeSpectator(playerObject);
				}
				Duel.getBossbar().removePlayer(player);
				Duel.getHologramController().removePlayerHologram(player);
				return;
			}
		}

		PlayerHologram hologram = Duel.getHologramController().getHologram(uuid);
		if (hologram == null) {
			return;
		}

		World toWorld = playerObject.getPlayer().getWorld();
		hologram.getHolograms().forEach((type, group) -> group.getValueAll().forEach(object -> {
			if (object.isInWorld(toWorld)) {
				object.spawnHologram();
			} else {
				object.destoryHologram();
			}
		}));
	}

	@EventHandler
	public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		PlayerObject playerObject = Duel.getPlayerController().getPlayer(uuid);

		if (playerObject == null) {
			return;
		}

		if (Duel.getMainConfig().isOptionChat()) {
			if (playerObject.inArena()) {
				Arena arena = playerObject.getArena();
				if (arena.getArenaState() != Arena.ArenaState.END && playerObject.isSpectator()) {
					for (PlayerObject gamePlayer : arena.getSpectators()) {
						gamePlayer.getPlayer()
								.sendMessage(Duel.getMessageConfig().getString("chats.spectator")
										.replace("<message>", event.getMessage())
										.replace("<rank>", RankUtils.getRank(playerObject.getScore()))
										.replace("<player>", player.getName()));
						event.setCancelled(true);
					}
					return;
				}
				if (arena.getArenaState() == Arena.ArenaState.END) {
					for (PlayerObject gamePlayer : arena.getPlayers()) {
						gamePlayer.getPlayer()
								.sendMessage(Duel.getMessageConfig().getString("chats.spectator")
										.replace("<message>", event.getMessage())
										.replace("<rank>", RankUtils.getRank(playerObject.getScore()))
										.replace("<player>", player.getName()));
					}
					event.setCancelled(true);
					for (PlayerObject gamePlayer : arena.getSpectators()) {
						gamePlayer.getPlayer()
								.sendMessage(Duel.getMessageConfig().getString("chats.spectator")
										.replace("<message>", event.getMessage())
										.replace("<rank>", RankUtils.getRank(playerObject.getScore()))
										.replace("<player>", player.getName()));
						event.setCancelled(true);
					}
				} else {
					for (PlayerObject gamePlayer : arena.getPlayers()) {
						gamePlayer.getPlayer()
								.sendMessage(Duel.getMessageConfig().getString("chats.ingame")
										.replace("<message>", event.getMessage())
										.replace("<rank>", RankUtils.getRank(playerObject.getScore()))
										.replace("<player>", player.getName()));
						event.setCancelled(true);
					}
					for (PlayerObject gamePlayer : arena.getSpectators()) {
						gamePlayer.getPlayer()
								.sendMessage(Duel.getMessageConfig().getString("chats.ingame")
										.replace("<message>", event.getMessage())
										.replace("<rank>", RankUtils.getRank(playerObject.getScore()))
										.replace("<player>", player.getName()));
						event.setCancelled(true);
					}
				}
			} else {
				for (PlayerObject all : Duel.getPlayerController().getAll()) {
					if (!all.inArena()) {
						all.getPlayer()
								.sendMessage(Duel.getMessageConfig().getString("chats.lobby")
										.replace("<message>", event.getMessage())
										.replace("<rank>", RankUtils.getRank(playerObject.getScore()))
										.replace("<player>", player.getName()));
						event.setCancelled(true);
					}
				}
			}
		}
	}

	@EventHandler
	public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		PlayerObject playerObject = Duel.getPlayerController().getPlayer(uuid);

		if (playerObject == null) {
			return;
		}

		String s = event.getMessage().split(" ")[0];
		if (playerObject.inArena()) {
			Arena arena = playerObject.getArena();
			if (arena.getArenaState() == Arena.ArenaState.PLAY && !player.hasPermission("duel.bypass.command")
					&& !DataUtils.containsIgnoreCase(Duel.getMainConfig().getConfig().getStringList("game.whitelist"),
					s)) {
				event.setCancelled(true);
				player.sendMessage(Duel.getMessageConfig().getString("arenas.arena-restricted-command"));
			}
		}
	}

	@EventHandler
	public void onPlayerDeathEvent(PlayerDeathEvent event) {
		Player player = event.getEntity();
		UUID uuid = player.getUniqueId();
		PlayerObject playerObject = Duel.getPlayerController().getPlayer(uuid);

		if (playerObject == null) {
			return;
		}

		if (playerObject.inArena()) {
			Arena arena = playerObject.getArena();
			if (arena.getArenaState() == Arena.ArenaState.PLAY) {
				event.setDeathMessage(null);
				player.setHealth(player.getMaxHealth());
				arena.onDeath(playerObject);
				event.getDrops().clear();
			}
		}
	}

	@EventHandler
	public void onSignChangeEvent(SignChangeEvent event) {
		if (!event.getLine(0).equalsIgnoreCase("1vs1")) {
			return;
		}

		Player player = event.getPlayer();
		if (!player.hasPermission("duel.sign.create")) {
			return;
		}

		Block block = event.getBlock();
		String arenaName = event.getLine(1);
		if (!Duel.getArenaManager().contains(arenaName)) {
			event.setCancelled(true);
			block.setType(Material.AIR);
			player.sendMessage(Duel.getMessageConfig().getString("errors.arena-found"));
			return;
		}

		Arena arena = Duel.getArenaManager().getArena(arenaName);
		Duel.getSignConfig().addSign(block, arena);
		player.sendMessage(Duel.getMessageConfig().getString("sign.add"));
	}

	@EventHandler
	public void onBlockPlaceEvent(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		PlayerObject playerObject = Duel.getPlayerController().getPlayer(uuid);
		if (playerObject == null) {
			return;
		}

		if (playerObject.isSpectator()) {
			event.setCancelled(true);
		}

		if (playerObject.getSetupData() != null) {
			ItemStack itemStack = event.getItemInHand();
			if (itemStack == null) {
				return;
			}
			if (Duel.getNms().isCustomData(itemStack, "type")) {
				event.setCancelled(true);
			}
		} else if (!playerObject.inArena()) {
			event.setCancelled(true);
		} else if (playerObject.inArena()) {
			Arena arena = playerObject.getArena();
			if (arena.getArenaState() != Arena.ArenaState.PLAY) {
				event.setCancelled(true);
			} else {
				Block block = event.getBlock();
				if (block.getType() != XMaterial.OAK_PLANKS.parseMaterial()) {
					event.setCancelled(true);
					event.setBuild(false);
				} else {
					arena.getPlaced().add(block);
				}
			}
		}
		Arena arena = playerObject.getArena();
		if (arena == null) {
			return;
		}
		if (arena.getArenaState() == Arena.ArenaState.PLAY) {
			double max_build_y = arena.getMaxBuildY();
			if (event.getBlockPlaced().getLocation().getBlockY() >= max_build_y) {
				player.sendMessage(Duel.getMessageConfig().getString("arenas.max-build-y"));
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onBlockBreakEvent(BlockBreakEvent event) {
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		PlayerObject playerObject = Duel.getPlayerController().getPlayer(uuid);

		if (playerObject == null) {
			return;
		}

		if (playerObject.isSpectator()) {
			event.setCancelled(true);
		}

		if (playerObject.inArena()) {
			Arena arena = playerObject.getArena();
			if (arena.getArenaState() != Arena.ArenaState.PLAY) {
				event.setCancelled(true);
			} else {
				Block block = event.getBlock();
				if (!arena.getPlaced().contains(block)) {
					event.setCancelled(true);
					player.sendMessage(Duel.getMessageConfig().getString("arenas.own-break"));
					return;
				}
				arena.getPlaced().remove(block);
			}
		} else {
			Block block2 = event.getBlock();
			if (block2.getState() instanceof Sign) {
				Location location = block2.getLocation();
				SignConfig.ConfigSign configSign = Duel.getSignConfig().getConfigSign(location);
				if (configSign != null) {
					Duel.getSignConfig().removeSign(location);
					player.sendMessage(Duel.getMessageConfig().getString("sign.remove"));
				}
			} else if (player.getGameMode() != GameMode.CREATIVE) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onBlockFromToEvent(BlockFromToEvent event) {
		for (String worldName : Duel.getMainConfig().getBlacklistWorlds()) {
			if (event.getBlock().getWorld().getName().equalsIgnoreCase(worldName)) {
				return;
			}
		}

		if (!event.getBlock().isLiquid()) {
			return;
		}
		Block to = event.getToBlock();
		if (event.getBlock().getType() == Material.COBBLESTONE) {
			event.setCancelled(true);
		}
		if (BucketUtils.generates(event.getBlock(), to)) {
			event.setCancelled(true);
		} else {
			BlockFace[] faces = {BlockFace.DOWN, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
			BlockFace[] array;
			for (int length = (array = faces).length, i = 0; i < length; ++i) {
				BlockFace face = array[i];
				if (BucketUtils.generates(event.getBlock(), to.getRelative(face))) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onPlayerBucketFillEvent(PlayerBucketFillEvent event) {
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		PlayerObject playerObject = Duel.getPlayerController().getPlayer(uuid);

		if (playerObject == null) {
			return;
		}

		if (playerObject.inArena()) {
			Arena arena = playerObject.getArena();
			if (arena.getArenaState() != Arena.ArenaState.PLAY) {
				event.setCancelled(true);
			} else {
				Block clickedBlock = event.getBlockClicked();
				if (arena.getBucketPlaced().contains(clickedBlock)) {
					if (clickedBlock.getType() == XMaterial.LAVA.parseMaterial()
							|| clickedBlock.getType() == XMaterial.LAVA.parseMaterial()) {
						List<Block> trackBlock = new LinkedList<>();
						BucketUtils.trackLava(clickedBlock, trackBlock);
						trackBlock.forEach(block -> block.setType(Material.AIR));
					}
					arena.getBucketPlaced().remove(clickedBlock);
				} else {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onPlayerBucketEmptyEvent(PlayerBucketEmptyEvent event) {
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		PlayerObject playerObject = Duel.getPlayerController().getPlayer(uuid);

		if (playerObject == null) {
			return;
		}

		if (playerObject.inArena()) {
			Arena arena = playerObject.getArena();
			if (arena.getArenaState() != Arena.ArenaState.PLAY) {
				event.setCancelled(true);
			} else {
				Block clickedBlock = event.getBlockClicked();
				Block block = null;
				if (event.getBlockFace() == BlockFace.UP) {
					block = clickedBlock.getLocation().add(0.0, 1.0, 0.0).getBlock();
				} else if (event.getBlockFace() == BlockFace.DOWN) {
					block = clickedBlock.getLocation().add(0.0, -1.0, 0.0).getBlock();
				} else if (event.getBlockFace() == BlockFace.EAST) {
					block = clickedBlock.getLocation().add(1.0, 0.0, 0.0).getBlock();
				} else if (event.getBlockFace() == BlockFace.SOUTH) {
					block = clickedBlock.getLocation().add(0.0, 0.0, 1.0).getBlock();
				} else if (event.getBlockFace() == BlockFace.WEST) {
					block = clickedBlock.getLocation().add(-1.0, 0.0, 0.0).getBlock();
				} else if (event.getBlockFace() == BlockFace.NORTH) {
					block = clickedBlock.getLocation().add(0.0, 0.0, -1.0).getBlock();
				}
				if (block != null) {
					arena.getBucketPlaced().add(block);
				} else if (player.getGameMode() != GameMode.CREATIVE) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onPlayerPickupItemEvent(PlayerPickupItemEvent event) {
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		PlayerObject playerObject = Duel.getPlayerController().getPlayer(uuid);

		if (playerObject == null) {
			return;
		}

		if (playerObject.getSetupData() != null) {
			ItemStack itemStack = event.getItem().getItemStack();
			if (itemStack == null) {
				return;
			}
			if (Duel.getNms().isCustomData(itemStack, "type")) {
				event.setCancelled(true);
			}
		} else if (playerObject.inArena()) {
			Arena arena = playerObject.getArena();
			ItemStack itemStack2 = event.getItem().getItemStack();
			if (itemStack2 == null) {
				return;
			}
			if (arena.getArenaState() == Arena.ArenaState.PLAY) {
				arena.getDroppedItem().remove(event.getItem());
			}
			if (arena.getArenaState() != Arena.ArenaState.PLAY) {
				event.setCancelled(true);
			} else if (itemStack2.getType() != XMaterial.OAK_PLANKS.parseMaterial()) {
				event.setCancelled(true);
			}
		} else if (player.getGameMode() != GameMode.CREATIVE) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		PlayerObject playerObject = Duel.getPlayerController().getPlayer(uuid);

		if (playerObject == null) {
			return;
		}

		if (playerObject.getSetupData() != null) {
			ItemStack itemStack = event.getItemDrop().getItemStack();
			if (itemStack == null) {
				return;
			}
			if (Duel.getNms().isCustomData(itemStack, "type")) {
				event.setCancelled(true);
			}
		} else if (playerObject.inArena()) {
			Arena arena = playerObject.getArena();
			ItemStack itemStack2 = event.getItemDrop().getItemStack();
			if (itemStack2 == null) {
				return;
			}
			if (arena.getArenaState() != Arena.ArenaState.PLAY) {
				event.setCancelled(true);
			} else if (itemStack2.getType() != XMaterial.OAK_PLANKS.parseMaterial()) {
				event.setCancelled(true);
			} else {
				arena.getDroppedItem().add(event.getItemDrop());
			}
		} else if (player.getGameMode() != GameMode.CREATIVE) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		PlayerObject playerObject = Duel.getPlayerController().getPlayer(uuid);

		if (playerObject == null) {
			return;
		}

		if (playerObject.isSpectator()) {
			event.setCancelled(true);
		}
		if (!playerObject.inArena()) {
			if (player.getGameMode() != GameMode.CREATIVE) {
				event.setCancelled(true);
			}
		}
		if (playerObject.inArena()) {
			Arena arena = playerObject.getArena();
			if (arena.getArenaState() == Arena.ArenaState.WAIT || arena.getArenaState() == Arena.ArenaState.PLAY || arena.getArenaState() == Arena.ArenaState.END || arena.getArenaState() == Arena.ArenaState.RESET) {
				if (event.getAction() == Action.PHYSICAL && event.getClickedBlock().getType() == XMaterial.FARMLAND.parseMaterial()) {
					event.setCancelled(true);
				}
				if (arena.getArenaState() == Arena.ArenaState.PLAY) {
					if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
						if (event.getItem() != null) {
							Material type;
							if (event.getItem().getType() == XMaterial.WATER_BUCKET.parseMaterial()) {
								type = XMaterial.WATER.parseMaterial();
							} else {
								if (event.getItem().getType() != XMaterial.LAVA_BUCKET.parseMaterial()) {
									return;
								}
								type = XMaterial.LAVA.parseMaterial();
							}
							Block block = event.getClickedBlock().getRelative(event.getBlockFace());
							if (BucketUtils.generates(type, block)) {
								arena.getPlaced().add(block);
							} else {
								BlockFace[] faces = {BlockFace.DOWN, BlockFace.UP, BlockFace.NORTH, BlockFace.EAST,
										BlockFace.SOUTH, BlockFace.WEST};
								BlockFace[] array;
								for (int length = (array = faces).length, i = 0; i < length; ++i) {
									BlockFace face = array[i];
									if (BucketUtils.generates(type, block.getRelative(face))) {
										event.setCancelled(true);
										break;
									}
								}
							}
						}
					}
				}
			}
		}
		if (event.getAction() != Action.PHYSICAL) {
			if (!playerObject.inArena() && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				Block block2 = event.getClickedBlock();
				if (block2.getState() instanceof Sign) {
					Location location = block2.getLocation();
					SignConfig.ConfigSign configSign = Duel.getSignConfig().getConfigSign(location);
					if (configSign != null) {
						ItemStack fakeItem = new ItemStack(Material.STONE);
						EventUtils.onItemType(playerObject,
								Duel.getNms().addCustomData(fakeItem, "arena", configSign.getName()), "join_to_arena",
								event.getAction());
						return;
					}
				}
			}
			ItemStack itemStack = event.getItem();
			if (itemStack == null) {
				return;
			}
			if (Duel.getNms().isCustomData(itemStack, "type")) {
				String type2 = Duel.getNms().getCustomData(itemStack, "type");
				EventUtils.onItemType(playerObject, itemStack, type2, event.getAction());
			}
		}
	}

	@EventHandler
	public void onSoilChangeEntity(EntityInteractEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			UUID uuid = player.getUniqueId();
			PlayerObject playerObject = Duel.getPlayerController().getPlayer(uuid);

			if (playerObject == null) {
				return;
			}

			if (playerObject.isSpectator()) {
				event.setCancelled(true);
			}
			if (playerObject.inArena()) {
				Arena arena = playerObject.getArena();
				if (arena.getArenaState() == Arena.ArenaState.WAIT || arena.getArenaState() == Arena.ArenaState.PLAY || arena.getArenaState() == Arena.ArenaState.END || arena.getArenaState() == Arena.ArenaState.RESET) {
					if (event.getEntityType() != EntityType.PLAYER && event.getBlock().getType() == XMaterial.FARMLAND.parseMaterial()) {
						event.setCancelled(true);
					}
				}
			}
		}
	}

	@EventHandler
	public void onInventoryClickEvent(InventoryClickEvent event) {
		Player player = (Player)event.getWhoClicked();
		UUID uuid = player.getUniqueId();
		PlayerObject playerObject = Duel.getPlayerController().getPlayer(uuid);
 		if (playerObject.isSpectator()) {
			if (player.getGameMode() != GameMode.CREATIVE) {
				event.setCancelled(true);
			}
			ItemStack itemStack = event.getCurrentItem();
			if (itemStack == null) {
				return;
			}
			if (Duel.getNms().isCustomData(itemStack, "type")) {
				String type = Duel.getNms().getCustomData(itemStack, "type");
				EventUtils.onItemType(playerObject, itemStack, type, Action.RIGHT_CLICK_BLOCK);
			}
		}
		else if (!playerObject.inArena()) {
			if (player.getGameMode() != GameMode.CREATIVE) {
				event.setCancelled(true);
			}
			ItemStack itemStack = event.getCurrentItem();
			if (itemStack == null) {
				return;
			}
			if (Duel.getNms().isCustomData(itemStack, "type")) {
				final String type = Duel.getNms().getCustomData(itemStack, "type");
				EventUtils.onItemType(playerObject, itemStack, type, Action.RIGHT_CLICK_BLOCK);
			}
		}
		else if (playerObject.inArena()) {
			Arena arena = playerObject.getArena();
			ItemStack itemStack2 = event.getCurrentItem();
			if (itemStack2 == null) {
				return;
			}
			if (arena.getArenaState() == Arena.ArenaState.PLAY) {
				if (event.getSlotType() == InventoryType.SlotType.ARMOR) {
					event.setCancelled(true);
				}
			}
			if (arena.getArenaState() != Arena.ArenaState.PLAY) {
				event.setCancelled(true);
				if (Duel.getNms().isCustomData(itemStack2, "type")) {
					String type2 = Duel.getNms().getCustomData(itemStack2, "type");
					EventUtils.onItemType(playerObject, itemStack2, type2, Action.RIGHT_CLICK_BLOCK);
				}
			}
		}
	}

	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event) {
		if (event.getEntity() instanceof Creeper) {
			event.setCancelled(true);
		}
		if (event.getEntity() instanceof TNTPrimed) {
			event.setCancelled(false);
		}
		if (event.getEntity() instanceof Fireball) {
			event.setCancelled(false);
		}
		if (event.getEntity() instanceof Wither) {
			event.setCancelled(true);
		}
		if (event.getEntity() instanceof Ghast) {
			event.setCancelled(true);
		}
		if (event.getEntity() instanceof EnderDragon) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onEntityDamageEvent(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			UUID uuid = player.getUniqueId();
			PlayerObject playerObject = Duel.getPlayerController().getPlayer(uuid);
			if (playerObject == null) {
				return;
			}
			if (!playerObject.inArena()) {
				event.setCancelled(true);
			} else if (playerObject.inArena()) {
				if (playerObject.isSpectator()) {
					event.setCancelled(true);
					return;
				}
				Arena arena = playerObject.getArena();
				if (arena.getArenaState() == Arena.ArenaState.WAIT || arena.getArenaState() == Arena.ArenaState.END) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player) {
			Player player = (Player) event.getDamager();
			UUID uuid = player.getUniqueId();
			PlayerObject playerObject = Duel.getPlayerController().getPlayer(uuid);
			if (playerObject == null) {
				return;
			}
			if (playerObject.inArena()) {
				if (playerObject.isSpectator()) {
					event.setCancelled(true);
					return;
				}
				Arena arena = playerObject.getArena();
				if (arena.getArenaState() != Arena.ArenaState.PLAY) {
					return;
				}
				if (event.getEntity() instanceof Player) {
					Player target = (Player) event.getEntity();
					UUID targetUUID = target.getUniqueId();
					PlayerObject targetObject = Duel.getPlayerController().getPlayer(targetUUID);
					if (targetObject == null) {
						return;
					}
					if (targetObject.inArena()) {
						Arena targetArena = targetObject.getArena();
						if (targetArena.getArenaState() != Arena.ArenaState.PLAY) {
							return;
						}
						if (arena.getCurrentUUID().equals(targetArena.getCurrentUUID())) {
							ActionBar.sendActionBar(player,
									Duel.getMessageConfig().getString("arenas.ingame.damage-actionbar").replace(
											"%%damage%%",
											String.valueOf(Math.round(event.getFinalDamage() * 100.0) / 100.0)));
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onCraftItem(CraftItemEvent event) {
		Player player = (Player) event.getWhoClicked();
		UUID uuid = player.getUniqueId();
		PlayerObject playerObject = Duel.getPlayerController().getPlayer(uuid);
		if (playerObject == null) {
			return;
		}
		if (playerObject.inArena()) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onVehicleEnter(VehicleEnterEvent event) {
		Player player = (Player) event.getEntered();
		UUID uuid = player.getUniqueId();
		PlayerObject playerObject = Duel.getPlayerController().getPlayer(uuid);
		if (playerObject == null) {
			return;
		}
		if (playerObject.inArena()) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onVehicleDamage(VehicleDamageEvent event) {
		Player player = (Player) event.getAttacker();
		UUID uuid = player.getUniqueId();
		PlayerObject playerObject = Duel.getPlayerController().getPlayer(uuid);
		if (playerObject == null) {
			return;
		}
		if (playerObject.inArena()) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onVehicleDestory(VehicleDestroyEvent event) {
		Player player = (Player) event.getAttacker();
		UUID uuid = player.getUniqueId();
		PlayerObject playerObject = Duel.getPlayerController().getPlayer(uuid);
		if (playerObject == null) {
			return;
		}
		if (playerObject.inArena()) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onVehicleMove(VehicleMoveEvent e) {
		Vehicle v = e.getVehicle();
		if (v.getPassenger() instanceof Player) {
			Player p = (Player) v.getPassenger();
			UUID uuid = p.getUniqueId();
			PlayerObject playerObject = Duel.getPlayerController().getPlayer(uuid);
			if (playerObject == null) {
				return;
			}
			if (playerObject.inArena()) {
				v.teleport(e.getFrom());
			}
		}
	}

	@EventHandler
	public void onTeleport(PlayerTeleportEvent event) {
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		PlayerObject playerObject = Duel.getPlayerController().getPlayer(uuid);
		if (playerObject == null) {
			return;
		}
		if (event.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL || event.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL) {
			if (playerObject.inArena()) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onItemFrame(PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		PlayerObject playerObject = Duel.getPlayerController().getPlayer(uuid);
		if (playerObject == null) {
			return;
		}
		if (event.getRightClicked().getType().equals(EntityType.ITEM_FRAME) && playerObject.inArena()) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onItemFrameDamage(EntityDamageByEntityEvent event) {
		UUID uuid = event.getDamager().getUniqueId();
		PlayerObject playerObject = Duel.getPlayerController().getPlayer(uuid);
		if (playerObject == null) {
			return;
		}
		if (event.getEntity() instanceof ItemFrame && event.getDamager() instanceof Player && playerObject.inArena()) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onFoodLevelChangeEvent(FoodLevelChangeEvent event) {
		for (String worldName : Duel.getMainConfig().getBlacklistWorlds()) {
			if (event.getEntity().getWorld().getName().equalsIgnoreCase(worldName)) {
				return;
			}
		}

		event.setCancelled(true);
		event.setFoodLevel(20);
	}

	@EventHandler
	public void onWeatherChangeEvent(WeatherChangeEvent event) {
		for (String worldName : Duel.getMainConfig().getBlacklistWorlds()) {
			if (event.getWorld().getName().equalsIgnoreCase(worldName)) {
				return;
			}
		}
		event.setCancelled(true);
	}
}
