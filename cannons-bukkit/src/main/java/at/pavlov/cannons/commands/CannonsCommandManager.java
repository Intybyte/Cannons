package at.pavlov.cannons.commands;

import at.pavlov.cannons.Enum.SelectCannon;
import at.pavlov.cannons.cannon.DesignStorage;
import at.pavlov.cannons.projectile.ProjectileStorage;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.PaperCommandManager;
import com.google.common.collect.ImmutableList;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

public class CannonsCommandManager extends PaperCommandManager {
    public CannonsCommandManager(Plugin plugin) {
        super(plugin);
        this.registerCommandContexts();
        this.registerCommandCompletions();
    }

    private void registerCommandContexts() {
        var commandContexts = this.getCommandContexts();
        commandContexts.registerContext(SelectCannon.class, c -> {
            String select = c.popFirstArg();
            switch (select.toLowerCase(Locale.ROOT)) {
                case "mob" -> {
                    return SelectCannon.TARGET_MOB;
                }

                case "player" -> {
                    return SelectCannon.TARGET_PLAYER;
                }

                case "cannon" -> {
                    return SelectCannon.TARGET_CANNON;
                }

                case "other" -> {
                    return SelectCannon.TARGET_OTHER;
                }

                default -> throw new InvalidCommandArgument("Invalid target specified, only allowed values: mob|player|cannon|other");
            }
        });
    }

    private void registerCommandCompletions() {
        var commandCompletions = this.getCommandCompletions();
        commandCompletions.registerCompletion("cannon_designs", c -> Collections.unmodifiableList(DesignStorage.getInstance().getDesignIds()));
        commandCompletions.registerCompletion("cannon_projectiles" , c -> Collections.unmodifiableList(ProjectileStorage.getProjectileIds()));
    }

    private static final Pattern COMMA = Pattern.compile(",");
    private static final Pattern PIPE = Pattern.compile("\\|");

    @Override
    public boolean hasPermission(CommandIssuer issuer, String permission) {
        if (permission == null || permission.isEmpty()) {
            return true;
        }

        //handle AND like normal using comma ","
        String[] perms = COMMA.split(permission);
        if (perms.length > 1) {
            return super.hasPermission(issuer, Set.of(perms));
        }

        //handle OR using pipe "|"
        CommandSender sender = issuer.getIssuer();
        for (String perm : PIPE.split(permission)) {
            perm = perm.trim();
            if (!perm.isEmpty() && sender.hasPermission(perm)) {
                return true;
            }
        }

        return false;
    }
}
