package com.github.julyss2019.mcsp.julyguild.guild;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CacheGuildManager {
    private final JulyGuild plugin = JulyGuild.inst();
    private final GuildManager guildManager = plugin.getGuildManager();
    private List<Guild> sortedGuilds = new ArrayList<>();

    public CacheGuildManager() {}

    public void startTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                updateSortedGuilds();
            }
        }.runTaskTimer(plugin, 0L, 20L * 60L);
    }

    public void updateSortedGuilds() {
        sortedGuilds.clear();
        sortedGuilds.addAll(guildManager.getSortedGuilds());
    }

    public List<Guild> getSortedGuilds() {
        return new ArrayList<>(sortedGuilds);
    }

    public int getRanking(@NotNull Guild guild) {
        if (!guild.isValid()) {
            throw new IllegalArgumentException("公会无效");
        }

        return sortedGuilds.indexOf(guild) + 1;
    }

    public void reset() {
        sortedGuilds.clear();
    }
}
