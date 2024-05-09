package it.einjojo.akani.dungeon.command;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import it.einjojo.akani.dungeon.listener.DungeonWorldListener;
import org.bukkit.entity.Player;

@CommandAlias("build")
public class BuildCommand {

    private final DungeonWorldListener worldListener;

    public BuildCommand(DungeonWorldListener worldListener) {
        this.worldListener = worldListener;
    }

    @Default
    public void toggleBuild(Player sender, @Optional OnlinePlayer target) {
        if (target == null) {
            sender.sendMessage("You are now in build mode.");
            worldListener.toggleBuild(sender.getUniqueId());
        } else {
            sender.sendMessage(target.getPlayer().getName() + " is now in build mode.");
            worldListener.toggleBuild(target.getPlayer().getUniqueId());
        }
    }

}
