package net.Duels.utility;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.Duels.font.DefaultFontInfo;

public class ChatUtils {

	public static String colorTranslate(String text) {
		if (text == null) {
			return null;
		}
		return ChatColor.translateAlternateColorCodes('&', text);
	}

	public static List<String> colorTranslate(List<String> list) {
		List<String> texts = new LinkedList<>();
		list.forEach(text -> texts.add(colorTranslate(text)));
		return texts;
	}

	public static void sendCenteredMessage(Player player, String message) {
		if (message == null || message.equals("")) {
			player.sendMessage("");
			return;
		}
		message = ChatColor.translateAlternateColorCodes('&', message);
		int messagePxSize = 0;
		boolean previousCode = false;
		boolean isBold = false;
		char[] charArray;
		for (int length = (charArray = message.toCharArray()).length, i = 0; i < length; ++i) {
			char c = charArray[i];
			if (c == '§') {
				previousCode = true;
			} else if (previousCode) {
				previousCode = false;
				isBold = (c == 'l' || c == 'L');
			} else {
				DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
				messagePxSize += (isBold ? dFI.getBoldLength() : dFI.getLength());
				++messagePxSize;
			}
		}
		int halvedMessageSize = messagePxSize / 2;
		int toCompensate = 154 - halvedMessageSize;
		int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
		int compensated = 0;
		StringBuilder sb = new StringBuilder();
		while (compensated < toCompensate) {
			sb.append(" ");
			compensated += spaceLength;
		}
		player.sendMessage(sb + message);
	}
}
