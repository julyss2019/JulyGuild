package com.github.julyss2019.mcsp.julyguild.api.event;

import com.github.julyss2019.mcsp.julyguild.guild.Guild;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class GuildDeletedEvent extends Event {
    private Guild guild;
    private static HandlerList handlerList = new HandlerList();

    public GuildDeletedEvent(@NotNull Guild guild) {
        this.guild = guild;
    }

    public Guild getGuild() {
        return guild;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
