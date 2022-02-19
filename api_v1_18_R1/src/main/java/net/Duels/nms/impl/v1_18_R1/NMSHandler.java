package net.Duels.nms.impl.v1_18_R1;

import net.Duels.nms.Hologram;
import net.Duels.nms.NMS;
import net.Duels.utility.ReflectionUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.entity.Entity;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_18_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.atomic.AtomicInteger;

public class NMSHandler implements NMS {

    @Override
    public Hologram createHologram() {
        return new NMSHologram();
    }

    @Override
    public ItemStack addCustomData(ItemStack itemStack, String key, String value) {
        net.minecraft.world.item.ItemStack nmsCopy = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound tag = nmsCopy.s();
        if (tag == null) {
            tag = new NBTTagCompound();
            nmsCopy.c(tag);
        }
        tag.a(key, value);
        nmsCopy.c(tag);
        return CraftItemStack.asBukkitCopy(nmsCopy);
    }

    @Override
    public ItemStack removeCustomData(ItemStack itemStack, String key) {
        net.minecraft.world.item.ItemStack nmsCopy = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound tag = nmsCopy.s();
        if (tag == null) {
            tag = new NBTTagCompound();
            nmsCopy.c(tag);
        }
        tag.r(key);
        nmsCopy.c(tag);
        return CraftItemStack.asBukkitCopy(nmsCopy);
    }

    @Override
    public String getCustomData(ItemStack itemStack, String key) {
        NBTTagCompound tag = CraftItemStack.asNMSCopy(itemStack).s();
        if (tag == null) {
            return null;
        }
        return tag.l(key);
    }

    @Override
    public boolean isCustomData(ItemStack itemStack, String key) {
        net.minecraft.world.item.ItemStack nmsCopy = CraftItemStack.asNMSCopy(itemStack);
        if (nmsCopy == null) {
            return false;
        }
        NBTTagCompound tag = nmsCopy.s();
        return tag != null && tag.e(key);
    }

    @Override
    public double getAbsorptionHearts(Player player) {
        return ((CraftPlayer) player).getHandle().el();
    }

    @Override
    public int getCitizenID() {
        AtomicInteger id = ReflectionUtils.getValue(Entity.class, "b");
        if (id == null) {
            return -1;
        }
        int targetID = id.incrementAndGet();
        ReflectionUtils.setValueWithNewType(Entity.class, "b", id);
        return targetID;
    }
}