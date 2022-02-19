package net.Duels.arenas.phase;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.scheduler.BukkitRunnable;

import lombok.Getter;
import net.Duels.Duel;

public class PhaseSeries {

	@Getter
	private final List<Phase> phases = new LinkedList<>();

	@Getter
	private int currentIndex = 0;

	@Getter
	private BukkitRunnable runnable;

	public void add(Phase phase) {
		this.phases.add(phase);
	}

	public void add(List<Phase> newPhases) {
		this.phases.addAll(newPhases);
	}

	public boolean contains(Class<? extends Phase> clazz) {
		return this.phases.stream().anyMatch(phase -> phase.getClass().getSimpleName().equals(clazz.getSimpleName()));
	}

	public void start() {
		if (this.phases.size() <= this.currentIndex) {
			return;
		}

		Phase phase = this.getCurrentPhase();
		phase.start();

		if (phase.getLoopDelay() != -0L) {
			this.runnable = new BukkitRunnable() {
				@Override
				public void run() {
					update();
				}
			};
			
			if (phase.getLoopDelay() == -1L) {
				this.getRunnable().runTaskLater(Duel.getInstance(), phase.getLoopStartDelay());
			} else {
				this.getRunnable().runTaskTimer(Duel.getInstance(), phase.getLoopStartDelay(), phase.getLoopDelay());
			}
		}
	}

	public void update() {
		if (this.phases.size() <= this.currentIndex) {
			return;
		}

		this.getCurrentPhase().update();
	}

	public void end() {
		if (this.phases.size() <= this.currentIndex) {
			return;
		}

		if (this.getRunnable() != null) {
			this.getRunnable().cancel();
		}
		this.getCurrentPhase().end();

		++this.currentIndex;
	}

	public void gotoPhaseForFirstIndex(Class<? extends Phase> targetPhase) {
		this.end();

		for (int i = this.currentIndex; i < this.phases.size(); i++) {
			Phase phase = this.phases.get(i);
			if (phase.getClass().getSimpleName().equals(targetPhase.getSimpleName())) {
				this.currentIndex = i;
				break;
			}
		}
	}

	public void next() {
		this.end();
		this.start();
	}

	public boolean isReadyToEnd() {
		return this.phases.size() <= this.currentIndex;
	}

	public Phase getCurrentPhase() {
		return this.phases.get(this.currentIndex);
	}

}
