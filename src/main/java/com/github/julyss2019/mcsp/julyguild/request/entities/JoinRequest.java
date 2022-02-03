package com.github.julyss2019.mcsp.julyguild.request.entities;

import com.github.julyss2019.mcsp.julyguild.guild.Guild;
import com.github.julyss2019.mcsp.julyguild.config.setting.MainSettings;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import com.github.julyss2019.mcsp.julyguild.request.BaseRequest;
import org.jetbrains.annotations.NotNull;

public class JoinRequest extends BaseRequest<GuildPlayer, Guild> {
    public JoinRequest() {}

    public JoinRequest(@NotNull GuildPlayer sender, @NotNull Guild receiver) {
        super(sender, receiver);
    }

    @Override
    public Type getType() {
        return Type.JOIN;
    }

    @Override
    public boolean isValid() {
        return (System.currentTimeMillis() - getCreationTime()) / 1000L < MainSettings.getGuildRequestJoinTimeout() && !getSender().isInGuild() && getReceiver().isValid();
    }
}
