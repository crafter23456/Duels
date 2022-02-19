package net.Duels.runnables;

import org.bukkit.entity.ArmorStand;
import org.bukkit.scheduler.BukkitRunnable;

import net.Duels.Duel;
import net.Duels.npc.DuelNPC;
import net.Duels.npc.NPCType;

public class RunnableNPCUpdateHologram extends BukkitRunnable {
	
	public void run() {
		for (NPCType type : NPCType.values()) {
			for (DuelNPC duelNPC : Duel.getNpcController().getNpcMap().get(type)) {
				for (int i = 0; i < duelNPC.getHologramText().size(); ++i) {
					String text = duelNPC.getHologramText().get(i);
					text = text
							.replace("%%arena_size%%",
									String.valueOf(Duel.getArenaManager().getArenas().size()))
							.replace("%%player_size%%",
									String.valueOf(Duel.getArenaManager().getPlayerSize()))
							.replace("%%online_size%%", String.valueOf(Duel.getInstance().getServer().getOnlinePlayers().size()));
					ArmorStand armorStand = duelNPC.getArmorStands().get(i);
					armorStand.setCustomName("§c§r" + text);
				}
			}
		}
	}
	
}
