package it.einjojo.akani.dungeon.command;

import it.einjojo.akani.util.commands.BaseCommand;
import it.einjojo.akani.util.commands.annotation.CommandAlias;
import it.einjojo.akani.util.commands.annotation.CommandPermission;
import it.einjojo.akani.util.commands.annotation.Default;
import it.einjojo.akani.util.commands.annotation.Optional;
import it.einjojo.akani.util.commands.bukkit.contexts.OnlinePlayer;
import com.google.common.base.Preconditions;
import it.einjojo.akani.dungeon.listener.DungeonWorldListener;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@CommandAlias("build")
@CommandPermission("akani.dungeons.build")
public class BuildCommand extends BaseCommand {

    private final DungeonWorldListener worldListener;

    public BuildCommand(@NotNull DungeonWorldListener worldListener) {
        Preconditions.checkNotNull(worldListener);
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
