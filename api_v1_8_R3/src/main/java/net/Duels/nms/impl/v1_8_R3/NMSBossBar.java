package net.Duels.nms.impl.v1_8_R3;

import com.google.common.collect.Maps;
import net.Duels.nms.BossBar;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class NMSBossBar implements BossBar
{
    private final Map<UUID, String> titles;
    private final Map<UUID, EntityWither> withers;
    
    public NMSBossBar() {
        this.titles = Maps.newLinkedHashMap();
        this.withers = Maps.newLinkedHashMap();
    }
    
    @Override
    public void addPlayer(Player player, String title) {
        UUID uuid = player.getUniqueId();
        if (this.titles.containsKey(uuid) || this.withers.containsKey(uuid)) {
            return;
        }
        EntityWither entityWither = new EntityWither(((CraftWorld)player.getWorld()).getHandle());
        Location witherLocation = this.getWitherLocation(player.getLocation());
        entityWither.setCustomName(title);
        entityWither.setInvisible(true);
        entityWither.setLocation(witherLocation.getX(), witherLocation.getY(), witherLocation.getZ(), 0.0f, 0.0f);
        this.withers.put(uuid, entityWither);
        this.titles.put(uuid, title);
    }
    
    @Override
    public void removePlayer(Player player) {
        UUID uuid = player.getUniqueId();
        if (!this.withers.containsKey(uuid) || !this.titles.containsKey(uuid)) {
            return;
        }
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(this.withers.get(uuid).getId()));
        this.withers.remove(uuid);
        this.titles.remove(uuid);
    }
    
    @Override
    public void setTitle(Player player, String title) {
        UUID uuid = player.getUniqueId();
        if (!this.withers.containsKey(uuid) || !this.titles.containsKey(uuid)) {
            return;
        }
        EntityWither wither = this.withers.get(uuid);
        wither.setCustomName(title);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityMetadata(wither.getId(), wither.getDataWatcher(), true));
        this.titles.put(uuid, title);
    }
    
    @Override
    public void setProgress(Player player, double progress) {
        UUID uuid = player.getUniqueId();
        if (!this.withers.containsKey(uuid) || !this.titles.containsKey(uuid)) {
            return;
        }
        EntityWither wither = this.withers.get(uuid);
        wither.setHealth((float)(progress * wither.getMaxHealth()));
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityMetadata(wither.getId(), wither.getDataWatcher(), true));
    }
    
    @Override
    public void update(Player player) {
    	if (player == null) {
    		return;
    	}
        UUID uuid = player.getUniqueId();
        if (!this.withers.containsKey(uuid) || !this.titles.containsKey(uuid)) {
            return;
        }
        EntityWither wither = this.withers.get(uuid);
        Location witherLocation = this.getWitherLocation(player.getLocation());
        wither.setLocation(witherLocation.getX(), witherLocation.getY(), witherLocation.getZ(), 0.0f, 0.0f);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityTeleport(wither));
    }
    
    @Override
    public void show(Player player) {
        UUID uuid = player.getUniqueId();
        if (!this.withers.containsKey(uuid) || !this.titles.containsKey(uuid)) {
            return;
        }
        EntityWither entityWither = this.withers.get(uuid);
        Location witherLocation = this.getWitherLocation(player.getLocation());
        entityWither.setCustomName(this.titles.get(uuid));
        entityWither.setInvisible(true);
        entityWither.setLocation(witherLocation.getX(), witherLocation.getY(), witherLocation.getZ(), 0.0f, 0.0f);
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(new PacketPlayOutSpawnEntityLiving(entityWither));
    }
    
    @Override
    public void hide(Player player) {
        UUID uuid = player.getUniqueId();
        if (!this.withers.containsKey(uuid) || !this.titles.containsKey(uuid)) {
            return;
        }
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(this.withers.get(uuid).getId()));
    }
    
    public Location getWitherLocation(Location location) {
        return location.add(location.getDirection().multiply(60));
    }
}
