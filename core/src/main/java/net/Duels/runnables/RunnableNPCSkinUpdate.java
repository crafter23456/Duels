package net.Duels.runnables;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;

import net.Duels.Duel;
import net.Duels.npc.DuelNPC;
import net.Duels.npc.NPCType;

public class RunnableNPCSkinUpdate extends BukkitRunnable {
	
	public void run() {
		for (NPCType type : NPCType.values()) {
			for (DuelNPC duelNPC : Duel.getNpcController().getNpcMap().get(type)) {
				if (duelNPC.getNpc().isSpawned()) {
					Entity entity = duelNPC.getNpc().getEntity();
					if (entity.getType() != EntityType.PLAYER || duelNPC.getTextureData() == null
							|| duelNPC.getTextureSignature() == null) {
						continue;
					}
					duelNPC.setSkin(duelNPC.getTextureData(), duelNPC.getTextureSignature());
				}
			}
		}
	}
	
}
