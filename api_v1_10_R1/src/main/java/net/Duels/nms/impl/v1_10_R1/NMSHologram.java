package net.Duels.nms.impl.v1_10_R1;

import net.Duels.nms.Hologram;
import net.Duels.utility.ChatUtils;
import net.minecraft.server.v1_10_R1.EntityArmorStand;
import net.minecraft.server.v1_10_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_10_R1.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_10_R1.PacketPlayOutSpawnEntityLiving;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_10_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class NMSHologram implements Hologram {
    private EntityArmorStand armorStand;
    private String text;
    private Location location;

    @Override
    public void spawn(Location location, String text) {
        this.location = location;
        this.text = text;
        (this.armorStand = new EntityArmorStand(((CraftWorld) location.getWorld()).getHandle())).setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        this.armorStand.setCustomNameVisible(true);
        this.armorStand.setNoGravity(false);
        this.armorStand.setCustomName(ChatUtils.colorTranslate(text));
        this.armorStand.setInvisible(true);
    }

    @Override
    public void sendTo(Player... array) {
        for (Player player : array) {
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutSpawnEntityLiving(this.armorStand));
            this.update(array);
        }
    }

    @Override
    public void update(Player... players) {
        for (Player player : players) {
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityMetadata(this.armorStand.getId(), this.armorStand.getDataWatcher(), false));
        }
    }

    @Override
    public void remove(Player... array) {
        for (int length = array.length, i = 0; i < length; ++i) {
            ((CraftPlayer) array[i]).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(this.armorStand.getId()));
        }
    }

    @Override
    public void setArmorStandText(String s) {
        this.armorStand.setCustomName(ChatUtils.colorTranslate(s));
    }

    public Location getLocation() {
        return this.location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
