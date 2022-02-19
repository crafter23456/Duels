package net.Duels.utility;

import org.bukkit.Location;
import org.bukkit.World;
import net.Duels.Duel;

public class ConfigUtils {

	public static Location stringToLocation(String text) throws RuntimeException {
		String[] data = text.split(",");
		if (data.length != 6) {
			throw new RuntimeException("The length of the data is short or long while decoding Location!");
		}
		String worldName = data[0];
		World world = Duel.getInstance().getServer().getWorld(worldName);
		if (world == null) {
			throw new RuntimeException("While converting data to Location, the world could not be found! ('" + worldName + "' Not Found)");
		}
		double x = Double.parseDouble(data[1]);
		double y = Double.parseDouble(data[2]);
		double z = Double.parseDouble(data[3]);
		float yaw = Float.parseFloat(data[4]);
		float pitch = Float.parseFloat(data[5]);
		return new Location(world, x, y, z, yaw, pitch);
	}

	public static String locationToString(Location location) {
		return location.getWorld().getName() + "," + location.getX() + "," + location.getY() + ","
				+ location.getZ() + "," + location.getYaw() + "," + location.getPitch();
	}
	
}