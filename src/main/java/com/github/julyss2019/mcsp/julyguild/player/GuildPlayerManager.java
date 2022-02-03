package com.github.julyss2019.mcsp.julyguild.player;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julylibrary.utils.YamlUtil;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class GuildPlayerManager {
    private JulyGuild plugin = JulyGuild.inst();
    private Map<UUID, GuildPlayer> guildPlayerMap = new HashMap<>();

    public GuildPlayer getGuildPlayer(@NotNull UUID uuid) {
        if (!guildPlayerMap.containsKey(uuid)) {
            guildPlayerMap.put(uuid, isRegistered(uuid) ? new GuildPlayer(getGuildPlayerFile(uuid)) : registerGuildPlayer(uuid));
        }


        return guildPlayerMap.get(uuid);
    }

    public GuildPlayer registerGuildPlayer(@NotNull UUID uuid) {
        if (isRegistered(uuid)) {
            throw new IllegalArgumentException("该玩家已注册 GuildPlayer");
        }

        File file = getGuildPlayerFile(uuid);
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);

        yml.set("uuid", uuid.toString());
        yml.set("register_time", System.currentTimeMillis());
        YamlUtil.saveYaml(yml, file);
        return new GuildPlayer(file);
    }

    public boolean isRegistered(@NotNull Player player) {
        return isRegistered(player.getUniqueId());
    }

    public boolean isRegistered(@NotNull UUID uuid) {
        return getGuildPlayerFile(uuid).exists();
    }

    private File getGuildPlayerFile(@NotNull UUID uuid) {
        return new File(plugin.getDataFolder(), "data" + File.separator + "players" + File.separator + uuid + ".yml");
    }

    public GuildPlayer getGuildPlayer(@NotNull Player player) {
        return getGuildPlayer(player.getUniqueId());
    }

    public Collection<GuildPlayer> getOnlineGuildPlayers() {
        return guildPlayerMap.size() == 0 ? new ArrayList<>() : guildPlayerMap.values().stream().filter(GuildPlayer::isOnline).collect(Collectors.toList());
    }

    public Collection<GuildPlayer> getLoadedGuildPlayers() {
        return guildPlayerMap.values();
    }
}
