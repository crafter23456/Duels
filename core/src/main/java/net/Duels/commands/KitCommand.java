package net.Duels.commands;

import com.cryptomorin.xseries.XMaterial;
import net.Duels.Duel;
import net.Duels.kit.Kit;
import net.Duels.player.PlayerObject;
import net.Duels.utility.ChatUtils;
import net.Duels.utility.ItemUtils;
import net.Duels.utility.KitUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class KitCommand implements CommandExecutor, TabCompleter {

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Duel.getMessageConfig().getString("no-console"));
            return true;
        }

        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();
        PlayerObject playerObject = Duel.getPlayerController().getPlayer(uuid);

        if (!player.hasPermission("duel.command.kit")) {
            player.sendMessage(Duel.getMessageConfig().getString("no-permission"));
            return true;
        }

        if (playerObject == null) {
            sender.sendMessage(Duel.getMessageConfig().getString("errors.blacklist-world-command"));
            return true;
        }

        if (args.length <= 0) {
            this.help(sender);
            return true;
        }

        if (args[0].equalsIgnoreCase("setContents")) {
            if (args.length < 2) {
                player.sendMessage(this.getInvalidPrefix() + "setContents <kitName>");
                return true;
            }

            Kit kit = Duel.getKitManager().getKit(args[1]);
            if (kit == null) {
                player.sendMessage(ChatUtils.colorTranslate("&cThe kit " + args[1] + " doesn't exits!"));
                return true;
            }

            kit.setContents(player.getInventory());
            kit.setArmor(player.getInventory().getArmorContents());
            Duel.getKitManager().saveKit(kit);
            player.sendMessage(ChatUtils.colorTranslate("&aThe contents of kit " + kit.getName() + " has been set!"));
            return true;
        } else if (args[0].equalsIgnoreCase("create")) {
            if (args.length < 5) {
                player.sendMessage(
                        this.getInvalidPrefix() + "create <kitName> <kitPermission> <displayName> <itemName:DATA>");
                return true;
            }
            if (!args[4].contains(":")) {
                player.sendMessage(
                        this.getInvalidPrefix() + "create <kitName> <kitPermission> <displayName> <itemName:DATA>");
                return true;
            }
            if (Duel.getKitManager().getKit(args[1]) != null) {
                player.sendMessage(ChatUtils.colorTranslate("&cThis kit already exists!"));
                return true;
            }
            if (Duel.getKitManager().getKitByDisplayName(args[3]) != null) {
                player.sendMessage(ChatUtils.colorTranslate("&cThis display name already exists!"));
                return true;
            }
            if (Material.getMaterial(args[4].split(":")[0]) == null) {
                player.sendMessage(ChatUtils.colorTranslate("&aThe item " + args[3] + " doesn't exists!"));
                return true;
            }
            try {
                XMaterial material = XMaterial.valueOf(args[4].split(":")[0]);
                Integer value = Integer.valueOf(args[4].split(":")[1]);
                Inventory inventory = Bukkit.createInventory(null, 36);
                ItemStack[] contents = player.getInventory().getContents();
                for (int i = 0; i < 36; ++i) {
                    if (contents[i] != null) {
                        inventory.setItem(i, contents[i]);
                    }
                }
                Duel.getKitManager().createKit(args[1], args[2], args[3], new LinkedList<>(), material, value,
                        inventory, player);
                player.sendMessage(ChatUtils.colorTranslate("&aThe kit " + args[1] + " has been created!"));
            } catch (Exception e) {
                e.printStackTrace();
                player.sendMessage(ChatColor.RED + "Fail To Create Kit");
            }
            return true;
        } else if (args[0].equalsIgnoreCase("give")) {
            if (args.length < 2) {
                player.sendMessage(this.getInvalidPrefix() + "give <kitName>");
                return true;
            }
            Kit kit = Duel.getKitManager().getKit(args[1]);
            if (kit == null) {
                player.sendMessage(ChatUtils.colorTranslate("&cThis kit doesn't exist!"));
                return true;
            }
            KitUtils.giveKit(player, kit);
            player.sendMessage(
                    ChatUtils.colorTranslate("&aYou have successfully received your &e" + args[1] + " &akit!"));
            return true;
        } else if (args[0].equalsIgnoreCase("delete")) {
            if (args.length < 2) {
                player.sendMessage(this.getInvalidPrefix() + "delete <kitName>");
                return true;
            }
            if (Duel.getKitManager().getKit(args[1]) == null) {
                player.sendMessage(ChatUtils.colorTranslate("&cThe kit " + args[1] + " doesn't exits!"));
                return true;
            }
            Kit kit = Duel.getKitManager().getKit(args[1]);
            Duel.getKitManager().deleteKit(kit);
            player.sendMessage(ChatUtils.colorTranslate("&aYou deleted the kit " + kit.getName() + "!"));
            return true;
        } else if (args[0].equalsIgnoreCase("addUnbreakable")) {
            ItemStack itemStack = player.getInventory().getItem(player.getInventory().getHeldItemSlot());
            if (itemStack == null || itemStack.getType() == Material.AIR) {
                sender.sendMessage(ChatColor.DARK_RED + "Invalid Item!");
                return true;
            }
            itemStack = ItemUtils.setUnbreakable(itemStack, true);
            player.getInventory().setItem(player.getInventory().getHeldItemSlot(), itemStack);
            return true;
        } else {
            if (args[0].equalsIgnoreCase("list")) {
                Iterator<Kit> iterator = Duel.getKitManager().getKits().iterator();
                while (iterator.hasNext()) {
                    sender.sendMessage(iterator.next().getKitStatus());
                }
            }
            return true;
        }
    }

    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        List<String> tab = new LinkedList<>();
        if (args.length == 1 && sender.hasPermission("duel.command.kit")) {
            tab.addAll(Arrays.asList("create", "setcontents", "give", "delete", "addUnbreakable"));
        }
        return tab;
    }

    private void help(CommandSender sender) {
        sender.sendMessage(ChatUtils.colorTranslate("&f&m-----------------------------"));
        sender.sendMessage(ChatUtils.colorTranslate("   &bDuel by Yenil"));
        sender.sendMessage("");
        sender.sendMessage(ChatUtils.colorTranslate(
                "  &b\u25b6 &7/kit create " + Duel.getMessageConfig().getString("commands.descriptions.kit.create")));
        sender.sendMessage(ChatUtils.colorTranslate("  &b\u25b6 &7/kit setContents "
                + Duel.getMessageConfig().getString("commands.descriptions.kit.setcontents")));
        sender.sendMessage(ChatUtils.colorTranslate(
                "  &b\u25b6 &7/kit give " + Duel.getMessageConfig().getString("commands.descriptions.kit.give")));
        sender.sendMessage(ChatUtils.colorTranslate(
                "  &b\u25b6 &7/kit delete " + Duel.getMessageConfig().getString("commands.descriptions.kit.delete")));
        sender.sendMessage(ChatUtils.colorTranslate("  &b\u25b6 &7/kit addUnbreakable "
                + Duel.getMessageConfig().getString("commands.descriptions.kit.addunbreakable")));
        sender.sendMessage(ChatUtils.colorTranslate(
                "  &b\u25b6 &7/kit list " + Duel.getMessageConfig().getString("commands.descriptions.kit.list")));
        sender.sendMessage("");
    }

    private String getInvalidPrefix() {
        return ChatUtils.colorTranslate("&cInsufficient arguments: /kit ");
    }
}