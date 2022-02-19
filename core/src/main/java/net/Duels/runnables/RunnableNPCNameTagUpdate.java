package net.Duels.runnables;

import org.bukkit.scheduler.BukkitRunnable;

import net.Duels.Duel;
import net.Duels.npc.DuelNPC;
import net.Duels.npc.NPCType;
import net.Duels.utility.NameTagUtils;

public class RunnableNPCNameTagUpdate extends BukkitRunnable {
	
	public void run() {
		for (NPCType type : NPCType.values()) {
			for (DuelNPC duelNPC : Duel.getNpcController().getNpcMap().get(type)) {
				NameTagUtils.removeNameTags(duelNPC.getNpc());
			}
		}
	}
	
}
