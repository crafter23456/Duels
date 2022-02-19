package net.Duels.nms.impl.v1_18_R1;

import net.Duels.nms.Hologram;
import net.Duels.utility.ChatUtils;
import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntityLiving;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class NMSHologram implements Hologram {
    private EntityArmorStand armorStand;
    private String text;
    private Location location;

    @Override
    public void spawn(Location location, String text) {
        this.location = location;
        this.text = text;
        (this.armorStand = new EntityArmorStand(((CraftWorld) location.getWorld()).getHandle(), 0.0, 0.0, 0.0)).b(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        this.armorStand.n(true);
        this.armorStand.e(false);
        this.armorStand.a(new ChatComponentText(ChatUtils.colorTranslate(text)));
        this.armorStand.j(true);
    }

    @Override
    public void sendTo(Player... array) {
        for (Player player : array) {
            ((CraftPlayer) player).getHandle().b.a(new PacketPlayOutSpawnEntityLiving(this.armorStand));
            this.update(array);
        }
    }

    @Override
    public void update(Player... players) {
        for (Player player : players) {
            ((CraftPlayer) player).getHandle().b.a(new PacketPlayOutEntityMetadata(this.armorStand.ae(), this.armorStand.ai(), false));
        }
    }

    @Override
    public void remove(Player... array) {
        for (int length = array.length, i = 0; i < length; ++i) {
            ((CraftPlayer) array[i]).getHandle().b.a(new PacketPlayOutEntityDestroy(this.armorStand.ae()));
        }
    }

    @Override
    public void setArmorStandText(String s) {
        this.armorStand.a(new ChatComponentText(ChatUtils.colorTranslate(s)));
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
