package net.Duels.utility;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocationUtils {
	
	public static String LocationToString(Location loc) {
		if (loc == null) {
			return "Game,0,0,0,0,0";
		}
		return ((loc.getWorld() != null) ? loc.getWorld().getName() : "Game") + "," + loc.getX() + ","
				+ loc.getY() + "," + loc.getZ() + "," + loc.getYaw() + "," + loc.getPitch();
	}

	public static Location StringToLocation(String loc) {
		try {
			String[] data = loc.split(",");
			if (Bukkit.getWorld(data[0]) != null) {
				return new Location(Bukkit.getWorld(data[0]), Double.parseDouble(data[1]), Double.parseDouble(data[2]),
						Double.parseDouble(data[3]), Float.parseFloat(data[4]), Float.parseFloat(data[5]));
			}
			return new Location(null, Double.parseDouble(data[1]), Double.parseDouble(data[2]),
					Double.parseDouble(data[3]), Float.parseFloat(data[4]), Float.parseFloat(data[5]));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String convertingString(Location location) {
		return location.getWorld().getName() + "," + location.getX() + ","
				+ location.getY() + "," + location.getZ() + "," + location.getYaw() + "," + location.getPitch();
	}

	public static List<String> setConvertingLocations(List<Location> list) {
		ArrayList<String> list2 = new ArrayList<>();
		list.forEach(location -> list2.add(convertingString(location)));
		return list2;
	}
	
}
