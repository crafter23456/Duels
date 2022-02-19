package net.Duels.arenas.phase;

import lombok.Getter;
import net.Duels.arenas.Arena;
import net.Duels.player.PlayerObject;

public abstract class Phase {

	@Getter
	protected final Arena game;

	@Getter
	private final long loopStartDelay, loopDelay;

	public Phase(Arena game, long loopStartDelay, long loopDelay) {
		this.game = game;
		this.loopStartDelay = loopStartDelay;
		this.loopDelay = loopDelay;
	}

	public abstract void start();

	public abstract void update();

	public abstract void end();

	public abstract void addPlayer(PlayerObject playerObject);

	public abstract void removePlayer(PlayerObject playerObject);

}
