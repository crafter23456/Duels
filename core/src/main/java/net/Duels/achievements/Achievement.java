package net.Duels.achievements;

import java.util.LinkedList;
import java.util.List;

import lombok.Getter;
import org.bukkit.command.CommandSender;

import net.Duels.Duel;
import net.Duels.player.PlayerObject;
import net.Duels.utility.ChatUtils;

@Getter
public class Achievement {

    private final AchievementType type;
    private final AchievementCommandType commandType;
    private final String name;
    private final String description;
    private final String sound;
    private final int amount;
    private final int xp;
    private final int coin;
    private final List<String> commands;
    private final List<String> messages;

    public Achievement(AchievementType type, AchievementCommandType commandType, String name, String description,
                       String sound, int amount, int xp, int coin, List<String> commands, List<String> messages) {
        this.commands = new LinkedList<>();
        this.messages = new LinkedList<>();
        this.type = type;
        this.commandType = commandType;
        this.name = name;
        this.description = description;
        this.sound = sound;
        this.amount = amount;
        this.xp = xp;
        this.coin = coin;
        if (commands != null && !commands.isEmpty()) {
            this.commands.addAll(commands);
        }
        if (messages != null && !messages.isEmpty()) {
            this.messages.addAll(ChatUtils.colorTranslate(messages));
        }
    }

    public void reward(PlayerObject playerObject) {
        playerObject.setCoin(playerObject.getCoin() + this.coin);
        playerObject.setXp(playerObject.getXp() + this.xp);
        this.commands.forEach(command -> {
            command = command.replace("%%player%%", playerObject.getName());
            Duel.getInstance().getServer()
                    .dispatchCommand((this.commandType == AchievementCommandType.PLAYER)
                            ? playerObject.getPlayer()
                            : Duel.getInstance().getServer().getConsoleSender(), command);
        });
        if (playerObject.isOnline()) {
            playerObject.playSound(this.sound);
            this.messages.forEach(message -> {
                message = message.replace("%%player%%", playerObject.getName());
                playerObject.getPlayer().sendMessage(message);
            });
        }
    }
}