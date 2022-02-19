package net.Duels.npc;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.metadata.FixedMetadataValue;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import lombok.Getter;
import net.Duels.Duel;
import net.Duels.utility.ChatUtils;
import net.Duels.utility.NameTagUtils;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.DespawnReason;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.npc.skin.SkinnableEntity;
import net.citizensnpcs.trait.Gravity;

public class DuelNPC {
	
	@Getter
	private final String id;
	
	@Getter
	private final NPCType type;
	
	@Getter
	private final Location location;
	
	@Getter
	private NPC npc;
	
	@Getter
	private final List<ArmorStand> armorStands;
	
	@Getter
	private final List<String> hologramText;
	
	@Getter
	private String textureData;
	
	@Getter
	private String textureSignature;

	public DuelNPC(String id, NPCType type, Location location) {
		this.armorStands = new LinkedList<>();
		this.hologramText = new LinkedList<>();
		this.id = id;
		this.type = type;
		this.location = location;
		this.load();
	}

	public void spawn() {
		this.npc.spawn(this.location);
		if (this.npc.getEntity() != null) {
			Entity entity = this.npc.getEntity();
			entity.setCustomNameVisible(false);
			if (entity.getType() == EntityType.PLAYER) {
				NameTagUtils.removeNameTags(this.npc);
				this.textureData = (String) Duel.getNpcConfig().getMapping()
						.getOrDefault("option." + this.type.getIdentifier() + ".texture.data", null);
				this.textureSignature = (String) Duel.getNpcConfig().getMapping()
						.getOrDefault("option." + this.type.getIdentifier() + ".texture.signature", null);
				if (this.textureData != null && this.textureSignature != null) {
					this.setSkin(this.textureData, this.textureSignature);
				}
			}
			Location hologramLocation = this.location.clone().add(0.0, Duel.getNpcConfig().getConfig()
					.getDouble("option." + this.type.getIdentifier() + ".hologramStartY"), 0.0);
			this.hologramText.addAll(ChatUtils.colorTranslate(Duel.getNpcConfig().getConfig()
					.getStringList("option." + this.type.getIdentifier() + ".holograms")));
			Collections.reverse(this.hologramText);
			for (String text : this.hologramText) {
				text = text
						.replace("%%arena_size%%",
								String.valueOf(Duel.getArenaManager().getArenas().size()))
						.replace("%%player_size%%",
								String.valueOf(Duel.getArenaManager().getPlayerSize()))
						.replace("%%online_size%%", String.valueOf(Duel.getInstance().getServer().getOnlinePlayers().size()));
				this.armorStands
						.add(this.createHologram(
								hologramLocation.add(0.0,
										Duel.getNpcConfig().getConfig()
												.getDouble("option." + this.type.getIdentifier() + ".hologramY"),
										0.0),
								text));
			}
		}
	}

	public void remove() {
		if (this.npc.isSpawned()) {
			Entity entity = this.npc.getEntity();
			if (entity != null) {
				if (entity.getPassenger() != null) {
					entity.getPassenger().remove();
				}
				if (entity.getVehicle() != null) {
					entity.getVehicle().remove();
				}
			}
			this.npc.despawn(DespawnReason.PLUGIN);
		}
		for (ArmorStand armorStand : this.armorStands) {
			if (armorStand != null) {
				armorStand.remove();
			}
		}
	}

	private void load() {
		NPCRegistry registry = CitizensAPI.getNamedNPCRegistry("duel");
		String npcType = (String) Duel.getNpcConfig().getMapping()
				.getOrDefault("option." + this.type.getIdentifier() + ".type", "PLAYER");
		EntityType entityType = this.getStringToEntityType(npcType);
		boolean gravitate = (boolean) Duel.getNpcConfig().getMapping()
				.getOrDefault("option." + this.type.getIdentifier() + ".gravity", false);
		(this.npc = registry.createNPC(entityType, this.getRandomName())).setProtected(true);
		this.npc.getTrait(Gravity.class).gravitate(!gravitate);
		this.npc.setFlyable(true);
		this.npc.data().set("nameplate-visible", false);
	}

	public void setSkin(String data, String signature) {
		if (this.npc.isSpawned() && this.npc.getEntity() != null) {
			SkinnableEntity entity = (SkinnableEntity) this.npc.getEntity();
			GameProfile profile = entity.getProfile();
			profile.getProperties().put("textures", new Property("textures", data, signature));
		}
	}

	private EntityType getStringToEntityType(String type) {
		EntityType entityType;
		try {
			entityType = EntityType.valueOf(type);
		} catch (Exception e) {
			Duel.log(Duel.LOG_LEVEL.WARNING, "");
			entityType = EntityType.PLAYER;
		}
		return entityType;
	}

	public String getRandomName() {
		char[] alpha = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".toCharArray();
		Random r = new Random();
		StringBuilder random = new StringBuilder();
		for (int i = 0; i < 12; ++i) {
			random.append(alpha[r.nextInt(alpha.length)]);
		}
		return random.toString();
	}

	private ArmorStand createHologram(Location location, String text) {
		ArmorStand as = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
		as.setGravity(false);
		as.setCanPickupItems(false);
		as.setCustomName("§c§r" + text);
		as.setCustomNameVisible(true);
		as.setVisible(false);
		as.setMetadata("Duel", new FixedMetadataValue(Duel.getInstance(), ""));
		return as;
	}
}
