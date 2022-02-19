package net.Duels.utility;

import java.util.List;

public class DataUtils {

	public static boolean containsIgnoreCase(List<String> list, String target) {
		return list.stream().anyMatch(value -> value.equalsIgnoreCase(target));
	}

}
