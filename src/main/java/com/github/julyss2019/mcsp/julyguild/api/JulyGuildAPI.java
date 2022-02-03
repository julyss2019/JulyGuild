package com.github.julyss2019.mcsp.julyguild.api;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.guild.GuildManager;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayerManager;

public class JulyGuildAPI {
    public static GuildManager getGuildManager() {
        return JulyGuild.inst().getGuildManager();
    }

    public static GuildPlayerManager getGuildPlayerManager() {
        return JulyGuild.inst().getGuildPlayerManager();
    }
}
