package net.Duels.hologram;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.Location;

public class HologramGroup {

	private final Map<Location, HologramObject> holograms = new LinkedHashMap<>();
	
	public void addHologram(Location key, HologramObject value) {
		this.holograms.put(key, value);
	}
	
	public void removeHologram(Location key) {
		this.holograms.remove(key);
	}
	
	public Set<Location> getKeyAll() {
		return this.holograms.keySet();
	}
	
	public Collection<HologramObject> getValueAll() {
		return this.holograms.values();
	}
	
}
