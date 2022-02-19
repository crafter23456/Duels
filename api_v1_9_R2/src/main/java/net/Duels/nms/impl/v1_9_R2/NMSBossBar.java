package net.Duels.nms.impl.v1_9_R2;

import net.Duels.nms.BossBar;
import java.util.*;
import com.google.common.collect.*;
import org.bukkit.entity.*;
import org.bukkit.boss.*;
import org.bukkit.craftbukkit.v1_9_R2.boss.*;

public class NMSBossBar implements BossBar {
    private final Map<UUID, org.bukkit.boss.BossBar> bossBars;
    private final Map<UUID, String> titles;

    public NMSBossBar() {
        this.bossBars = Maps.newLinkedHashMap();
        this.titles = Maps.newLinkedHashMap();
    }

    @Override
    public void addPlayer(Player player, String title) {
        UUID uuid = player.getUniqueId();
        if (this.titles.containsKey(uuid) || this.bossBars.containsKey(uuid)) {
            return;
        }
        org.bukkit.boss.BossBar bossBar = new CraftBossBar(title, BarColor.WHITE, BarStyle.SOLID);
        bossBar.setTitle(title);
        bossBar.setVisible(false);
        bossBar.addPlayer(player);
        this.bossBars.put(uuid, bossBar);
        this.titles.put(uuid, title);
    }

    @Override
    public void removePlayer(Player player) {
        UUID uuid = player.getUniqueId();
        if (!this.bossBars.containsKey(uuid) || !this.titles.containsKey(uuid)) {
            return;
        }
        this.bossBars.remove(uuid).removeAll();
        this.titles.remove(uuid);
    }

    @Override
    public void setTitle(Player player, String title) {
        UUID uuid = player.getUniqueId();
        if (!this.bossBars.containsKey(uuid) || !this.titles.containsKey(uuid)) {
            return;
        }
        org.bukkit.boss.BossBar bossBar = this.bossBars.get(uuid);
        bossBar.setTitle(title);
        this.titles.put(uuid, title);
    }

    @Override
    public void setProgress(Player player, double progress) {
        UUID uuid = player.getUniqueId();
        if (!this.bossBars.containsKey(uuid) || !this.titles.containsKey(uuid)) {
            return;
        }
        org.bukkit.boss.BossBar bossBar = this.bossBars.get(uuid);
        bossBar.setProgress(progress);
    }

    @Override
    public void update(Player player) {
    }

    @Override
    public void show(Player player) {
        UUID uuid = player.getUniqueId();
        if (!this.bossBars.containsKey(uuid) || !this.titles.containsKey(uuid)) {
            return;
        }
        org.bukkit.boss.BossBar bossBar = this.bossBars.get(uuid);
        bossBar.setVisible(true);
    }

    @Override
    public void hide(Player player) {
        UUID uuid = player.getUniqueId();
        if (!this.bossBars.containsKey(uuid) || !this.titles.containsKey(uuid)) {
            return;
        }
        org.bukkit.boss.BossBar bossBar = this.bossBars.get(uuid);
        bossBar.setVisible(false);
    }
}
