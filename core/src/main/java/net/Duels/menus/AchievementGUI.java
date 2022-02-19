package net.Duels.menus;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import net.Duels.Duel;
import net.Duels.achievements.Achievement;
import net.Duels.achievements.AchievementType;
import net.Duels.player.PlayerObject;
import net.Duels.utility.Pair;
import net.Duels.utility.ItemUtils;

public class AchievementGUI {
	private final PlayerObject playerObject;

	public AchievementGUI(PlayerObject playerObject) {
		this.playerObject = playerObject;
	}

	public void createAchievementMenu(int page) {
		Player player = this.playerObject.getPlayer();
		Inventory inventory = Bukkit.getServer().createInventory(null, 54,
				Duel.getMessageConfig().getString("guis.achievement.title",
						Collections.singletonList(new Pair<>("%%player%%", player.getName()))));
		ItemStack titleWinItem = Duel.getItemConfig()
				.getAchievementConfigItem("gui-achievement.achievement-title-wins-item").toItem(this.playerObject);
		ItemStack titleKillItem = Duel.getItemConfig()
				.getAchievementConfigItem("gui-achievement.achievement-title-kills-item").toItem(this.playerObject);
		ItemStack titleScoreItem = Duel.getItemConfig()
				.getAchievementConfigItem("gui-achievement.achievement-title-score-item").toItem(this.playerObject);
		ItemStack borderItem = Duel.getItemConfig().getAchievementConfigItem("gui-achievement.achievement-border-item")
				.toItem(this.playerObject);
		ItemStack borderLeftItem = this.replaceDisplayName(borderItem.clone(), "%%arrow%%", "\u2192");
		ItemStack borderRightItem = this.replaceDisplayName(borderItem.clone(), "%%arrow%%", "\u2190");
		ItemStack nextPageItem = this.replaceDisplayName(Duel.getItemConfig()
				.getAchievementConfigItem("gui-achievement.next-page-item").toItem(this.playerObject), "%%next_page%%",
				String.valueOf(page + 1));
		ItemStack backPageItem = this.replaceDisplayName(Duel.getItemConfig()
				.getAchievementConfigItem("gui-achievement.previous-page-item").toItem(this.playerObject),
				"%%previous_page%%", String.valueOf(page - 1));
		ItemStack closeItem = Duel.getItemConfig().getAchievementConfigItem("gui-achievement.close-item")
				.toItem(this.playerObject);
		ItemStack lockedItem = Duel.getItemConfig().getAchievementConfigItem("gui-achievement.achievement-locked-item")
				.toItem(this.playerObject);
		ItemStack unlockedItem = Duel.getItemConfig()
				.getAchievementConfigItem("gui-achievement.achievement-unlocked-item").toItem(this.playerObject);
		nextPageItem = Duel.getNms().addCustomData(nextPageItem, "page", String.valueOf(page + 1));
		backPageItem = Duel.getNms().addCustomData(backPageItem, "page", String.valueOf(page - 1));
		inventory.setItem(2, titleWinItem);
		inventory.setItem(3, titleKillItem);
		inventory.setItem(4, titleScoreItem);
		inventory.setItem(49, closeItem);
		int[] leftSlots = { 0, 9, 18, 27, 36 };
		int[] rightSlots = { 8, 17, 26, 35, 44 };

		for (int slot : leftSlots) {
			if (Duel.getItemConfig().getConfig().getBoolean("gui-achievement.achievement-border-item.enable")) {
				inventory.setItem(slot, borderLeftItem);
			}
		}
		
		for (int slot : rightSlots) {
			if (Duel.getItemConfig().getConfig().getBoolean("gui-achievement.achievement-border-item.enable")) {
				inventory.setItem(slot, borderRightItem);
			}
		}

		Map<AchievementType, List<Achievement>> achievements = Duel.getAchievementConfig().getAchievements();
		for (AchievementType type : AchievementType.values()) {
			if (achievements.get(type).size() >= page * 4) {
				if (Duel.getItemConfig().getConfig().getBoolean("gui-achievement.next-page-item.enable")) {
					inventory.setItem(53, nextPageItem);
				}
			}

			if (page >= 2) {
				if (Duel.getItemConfig().getConfig().getBoolean("gui-achievement.previous-page-item.enable")) {
					inventory.setItem(45, backPageItem);
				}
			}
		}
		
		int min = (page - 1) * 4;
		int currentIndex = 2;

		for (AchievementType type : AchievementType.values()) {
			List<Achievement> list = achievements.get(type);
			for (int i = 1; i < 5 && list.size() > i - 1 + min; ++i) {
				Achievement achievement = list.get(i - 1 + min);
				if (((type == AchievementType.KILLS) ? this.playerObject.getKills()
						: this.playerObject.getWins()) < achievement.getAmount()) {
					ItemStack currentLocked = lockedItem.clone();
					this.replaceDisplayName(currentLocked, "%%name%%", achievement.getName());
					this.replaceLore(currentLocked, "%%coin%%", String.valueOf(achievement.getCoin()));
					this.replaceLore(currentLocked, "%%xp%%", String.valueOf(achievement.getXp()));
					this.replaceLore(currentLocked, "%%name%%", achievement.getName());
					this.replaceLore(currentLocked, "%%description%%", achievement.getDescription());
					this.replaceLore(currentLocked, "%%current%%",
							String.valueOf(type == AchievementType.KILLS ? this.playerObject.getKills() : (type == AchievementType.WINS ? this.playerObject.getWins() : this.playerObject.getScore())));
					this.replaceLore(currentLocked, "%%amount%%", String.valueOf(achievement.getAmount()));
					inventory.setItem(currentIndex + 9 * i, currentLocked);
				} else {
					ItemStack currentUnLocked = unlockedItem.clone();
					this.replaceDisplayName(currentUnLocked, "%%name%%", achievement.getName());
					this.replaceLore(currentUnLocked, "%%coin%%", String.valueOf(achievement.getCoin()));
					this.replaceLore(currentUnLocked, "%%xp%%", String.valueOf(achievement.getXp()));
					this.replaceLore(currentUnLocked, "%%name%%", achievement.getName());
					this.replaceLore(currentUnLocked, "%%description%%", achievement.getDescription());
					this.replaceLore(currentUnLocked, "%%current%%",
							String.valueOf((type == AchievementType.KILLS ? this.playerObject.getKills() : (type == AchievementType.WINS ? this.playerObject.getWins() : this.playerObject.getScore()))));
					this.replaceLore(currentUnLocked, "%%amount%%", String.valueOf(achievement.getAmount()));
					inventory.setItem(currentIndex + 9 * i, currentUnLocked);
				}
			}
			++currentIndex;
		}
		
		if (player.getOpenInventory() != null) {
			player.closeInventory();
			
			new BukkitRunnable() {
				public void run() {
					player.openInventory(inventory);
				}
			}.runTaskLater(Duel.getInstance(), 3L);
		} else {
			player.openInventory(inventory);
		}
	}

	public ItemStack replaceDisplayName(ItemStack itemStack, String key, String value) {
		if (itemStack.getItemMeta() == null) {
			return itemStack;
		}
		if (itemStack.getItemMeta().getDisplayName() == null) {
			return itemStack;
		}
		ItemMeta itemMeta = itemStack.getItemMeta();
		ItemUtils.addhideFlag(itemStack);
		itemMeta.setDisplayName(itemMeta.getDisplayName().replace(key, value));
		itemStack.setItemMeta(itemMeta);
		return itemStack;
	}

	public ItemStack replaceLore(ItemStack itemStack, String key, String value) {
		if (itemStack.getItemMeta() == null) {
			return itemStack;
		}
		if (itemStack.getItemMeta().getLore() == null) {
			return itemStack;
		}
		List<String> lore = new LinkedList<>();
		ItemMeta itemMeta = itemStack.getItemMeta();
		for (String oldlore : itemMeta.getLore()) {
			lore.add(oldlore.replace(key, value));
		}
		ItemUtils.addhideFlag(itemStack);
		itemMeta.setLore(lore);
		itemStack.setItemMeta(itemMeta);
		return itemStack;
	}
}
