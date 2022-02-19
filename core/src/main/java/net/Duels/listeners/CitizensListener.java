package net.Duels.listeners;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import net.Duels.Duel;
import net.Duels.arenas.Arena;
import net.Duels.menus.AchievementGUI;
import net.Duels.menus.StatsGUI;
import net.Duels.npc.DuelNPC;
import net.Duels.npc.NPCType;
import net.Duels.player.PlayerObject;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;

public class CitizensListener implements Listener {
	
	@EventHandler
	public void onNPCRightClickEvent(NPCRightClickEvent event) {
		Player player = event.getClicker();
		UUID uuid = player.getUniqueId();
		PlayerObject playerObject = Duel.getPlayerController().getPlayer(uuid);
		if (playerObject == null) {
			return;
		}
		this.onNPCClickEvent(playerObject, event.getNPC());
	}

	@EventHandler
	public void onNPCLeftClickEvent(NPCLeftClickEvent event) {
		Player player = event.getClicker();
		UUID uuid = player.getUniqueId();
		PlayerObject playerObject = Duel.getPlayerController().getPlayer(uuid);
		if (playerObject == null) {
			return;
		}
		this.onNPCClickEvent(playerObject, event.getNPC());
	}

	public void onNPCClickEvent(PlayerObject playerObject, NPC npc) {
		if (!playerObject.inArena() && playerObject.getSetupData() == null) {
			NPCType[] values;
			for (int length = (values = NPCType.values()).length, i = 0; i < length; ++i) {
				NPCType type = values[i];
				for (DuelNPC duelNPC : Duel.getNpcController().getNpcMap().get(type)) {
					if (npc.getUniqueId().equals(duelNPC.getNpc().getUniqueId())) {
						if (type == NPCType.PLAY_NPC) {
							List<Arena> arenas = new LinkedList<>(Duel.getArenaManager().getArenas());
							if (arenas.isEmpty()) {
								playerObject.getPlayer()
										.sendMessage(Duel.getMessageConfig().getString("errors.no-games"));
								playerObject.playSound("sounds.errors.no-game");
								return;
							}
							List<Arena> availableGames = new LinkedList<>();
							for (Arena arena3 : arenas) {
								if (arena3.getArenaState() == Arena.ArenaState.WAIT && !arena3.isFull()) {
									availableGames.add(arena3);
								}
							}
							if (availableGames.isEmpty()) {
								playerObject.getPlayer()
										.sendMessage(Duel.getMessageConfig().getString("errors.no-games"));
								playerObject.playSound("sounds.errors.no-available-games");
								return;
							}
							Collections.shuffle(availableGames);
							Arena arena3 = availableGames.stream()
									.min((arena1, arena2) -> arena2.getPlayers().size() - arena1.getPlayers().size())
									.get();
							arena3.addPlayer(playerObject);
							playerObject.playSound("sounds.action.random-join");
							return;
						} else {
							if (type == NPCType.STATS_NPC) {
								new StatsGUI(playerObject);
								return;
							}
							if (type == NPCType.TRAIL_SHOP_NPC) {
								playerObject.getPlayer().sendMessage(
										Duel.getMessageConfig().getString("commands.still-in-development"));
								playerObject.playSound("sounds.errors.open-shop");
								return;
							}
							if (type == NPCType.ACHIEVEMENT_NPC) {
								new AchievementGUI(playerObject).createAchievementMenu(1);
								return;
							}
						}
					}
				}
			}
		}
	}
	
}
