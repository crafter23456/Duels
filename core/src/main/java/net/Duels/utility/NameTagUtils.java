package net.Duels.utility;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.metadata.FixedMetadataValue;

import net.Duels.Duel;
import net.citizensnpcs.api.npc.NPC;

public class NameTagUtils {

	public static void removeNameTags(NPC npc) {
		if (npc.isSpawned()) {
			if (npc.getEntity().getPassenger() != null) {
				return;
			}
			if (npc.getEntity().getVehicle() != null && npc.getEntity().getVehicle() instanceof ArmorStand) {
				npc.getEntity().getVehicle().remove();
			}
			if (npc.getEntity().getPassenger() != null && npc.getEntity().getPassenger() instanceof ArmorStand) {
				npc.getEntity().getPassenger().remove();
			}
			ArmorStand armorstand = (ArmorStand) npc.getEntity().getWorld()
					.spawnEntity(npc.getEntity().getLocation(), EntityType.ARMOR_STAND);
			armorstand.setVisible(false);
			armorstand.setCustomName("§c§r");
			armorstand.setCustomNameVisible(false);
			armorstand.setMetadata("HideNametag", new FixedMetadataValue(Duel.getInstance(), true));
			npc.getEntity().setPassenger(armorstand);
		}
	}
	
}
