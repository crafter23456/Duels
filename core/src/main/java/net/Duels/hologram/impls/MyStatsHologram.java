package net.Duels.hologram.impls;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.google.common.util.concurrent.AtomicDouble;

import net.Duels.Duel;
import net.Duels.Duel.LOG_LEVEL;
import net.Duels.hologram.HologramObject;
import net.Duels.nms.Hologram;
import net.Duels.player.PlayerObject;
import net.Duels.utility.Pair;
import net.Duels.utility.RankUtils;

public class MyStatsHologram extends HologramObject {

	public MyStatsHologram(UUID uuid, List<String> lines, Location location) {
		super(uuid, lines, location);
	}

	@Override
	public void spawnHologram() {
		PlayerObject playerObject = this.getPlayerObject();
		if (playerObject == null) {
			return;
		}
		
		Player player = playerObject.getPlayer();

		if (!this.isInWorld(player.getWorld())) {
			return;
		}
		
		if (this.getHolograms().size() >= 1) {
			return;
		}

		List<Hologram> holograms = new LinkedList<>();
		AtomicDouble stackHeight = new AtomicDouble(0.0D);

		this.getLines().forEach(line -> {
			if (!isValidLine(line)) {
				Duel.log(LOG_LEVEL.WARNING, "While reading and reflecting the hologram line, there is a wrong line and flip it: " + line);
				return;
			}
			
			Pair<Double, String> value = lineToData(line);
			String text = value.getB();

			Location targetLocation = this.getLocation().clone().subtract(0.0D, stackHeight.addAndGet(value.getA()),
					0.0D);

			if (text.trim().isEmpty()) {
				return;
			}

			text = this.replaceStats(playerObject, text);

			Hologram hologram = Duel.getNms().createHologram();
			hologram.spawn(targetLocation, text);
			holograms.add(hologram);
		});

		holograms.forEach(hologram -> hologram.sendTo(player));
		this.getHolograms().addAll(holograms);
	}

	@Override
	public void destoryHologram() {
		PlayerObject playerObject = this.getPlayerObject();
		if (playerObject == null) {
			return;
		}
		
		Player player = playerObject.getPlayer();

		if (!this.isInWorld(player.getWorld())) {
			this.getHolograms().clear();
			return;
		}
		
		if (this.getHolograms().size() <= 0) {
			return;
		}

		this.getHolograms().forEach(hologram -> hologram.remove(player));
		this.getHolograms().clear();
	}

	@Override
	public void updateHologram() {
		PlayerObject playerObject = this.getPlayerObject();
		if (playerObject == null) {
			return;
		}
		
		Player player = playerObject.getPlayer();

		if (!this.isInWorld(player.getWorld())) {
			return;
		}
		
		if (this.getHolograms().size() <= 0) {
			this.spawnHologram();
			return;
		}

		AtomicInteger index = new AtomicInteger(0);
		this.getHolograms().forEach(hologram -> {
			String line = this.getLines().get(index.getAndIncrement());

			if (!isValidLine(line)) {
				Duel.log(LOG_LEVEL.WARNING, "While reading and reflecting the hologram line, there is a wrong line and flip it: " + line);
				return;
			}
			
			Pair<Double, String> value = lineToData(line);
			String text = value.getB();

			if (text.trim().isEmpty()) {
				return;
			}

			text = this.replaceStats(playerObject, text);
			hologram.setArmorStandText(text);
			hologram.update(player);
		});
	}

	private String replaceStats(PlayerObject playerObject, String text) {
		return text.replace("<rank>", RankUtils.getRank(playerObject.getScore()))
				.replace("<progress>", RankUtils.getRankProcces(playerObject.getScore()))
				.replace("<totalScore>", String.valueOf(playerObject.getScore()))
				.replace("<totalDeaths>", String.valueOf(playerObject.getDeaths()))
				.replace("<totalKills>", String.valueOf(playerObject.getKills()))
				.replace("<totalWins>", String.valueOf(playerObject.getWins()))
				.replace("<totalBestStreak>", String.valueOf(playerObject.getBestStreak()))
				.replace("<totalCoins>", String.valueOf(playerObject.getCoin()))
				.replace("<totalXp>", String.valueOf(playerObject.getXp()));
	}

}
