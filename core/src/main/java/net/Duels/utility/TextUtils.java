package net.Duels.utility;

public class TextUtils {
	
	public static String replaceText(String text) {
		return text.replace("->", "\u00BB").replace("<-", "\u00AB").replace(">>", "\u27a3");
	}
	
}
