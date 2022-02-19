package net.Duels.nms.impl.v1_12_R1;

import net.Duels.nms.Hologram;
import net.Duels.nms.NMS;
import net.Duels.utility.ReflectionUtils;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class NMSHandler implements NMS {

    @Override
    public Hologram createHologram() {
        return new NMSHologram();
    }

    @Override
    public ItemStack addCustomData(ItemStack itemStack, String key, String value) {
        net.minecraft.server.v1_12_R1.ItemStack nmsCopy = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound tag = nmsCopy.getTag();
        if (tag == null) {
            tag = new NBTTagCompound();
            nmsCopy.setTag(tag);
        }
        tag.setString(key, value);
        nmsCopy.setTag(tag);
        return CraftItemStack.asBukkitCopy(nmsCopy);
    }

    @Override
    public ItemStack removeCustomData(ItemStack itemStack, String key) {
        net.minecraft.server.v1_12_R1.ItemStack nmsCopy = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound tag = nmsCopy.getTag();
        if (tag == null) {
            tag = new NBTTagCompound();
            nmsCopy.setTag(tag);
        }
        tag.remove(key);
        nmsCopy.setTag(tag);
        return CraftItemStack.asBukkitCopy(nmsCopy);
    }

    @Override
    public String getCustomData(ItemStack itemStack, String key) {
        NBTTagCompound tag = CraftItemStack.asNMSCopy(itemStack).getTag();
        if (tag == null) {
            return null;
        }
        return tag.getString(key);
    }

    @Override
    public boolean isCustomData(ItemStack itemStack, String key) {
        net.minecraft.server.v1_12_R1.ItemStack nmsCopy = CraftItemStack.asNMSCopy(itemStack);
        if (nmsCopy == null) {
            return false;
        }
        NBTTagCompound tag = nmsCopy.getTag();
        return tag != null && tag.hasKey(key);
    }

    @Override
    public double getAbsorptionHearts(Player player) {
        return ((CraftPlayer) player).getHandle().getAbsorptionHearts();
    }

    @Override
    public int getCitizenID() {
        int id = ReflectionUtils.getValue(Entity.class, "entityCount");
        ReflectionUtils.setValue(Entity.class, "entityCount", id + 1);
        return id;
    }
}