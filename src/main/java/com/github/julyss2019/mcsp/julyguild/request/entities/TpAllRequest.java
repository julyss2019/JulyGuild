package com.github.julyss2019.mcsp.julyguild.request.entities;

import com.github.julyss2019.mcsp.julyguild.config.setting.MainSettings;
import com.github.julyss2019.mcsp.julyguild.guild.member.GuildMember;
import com.github.julyss2019.mcsp.julyguild.request.BaseRequest;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class TpAllRequest extends BaseRequest<GuildMember, GuildMember> {
    private Location location;

    public TpAllRequest() {}

    public TpAllRequest(@NotNull GuildMember sender, @NotNull GuildMember receiver, @NotNull Location location) {
        super(sender, receiver);

        this.location = location;
    }

    @Override
    public void onSave(@NotNull ConfigurationSection section) {
        super.onSave(section);


    }

    @Override
    public void onLoad(@NotNull ConfigurationSection section) {
        super.onLoad(section);
    }

    @Override
    public Type getType() {
        return Type.TP_ALL;
    }

    @Override
    public boolean isValid() {
        return (System.currentTimeMillis() - getCreationTime()) / 1000L < MainSettings.getGuildTpAllTimeout() && getSender().isValid() && getReceiver().isValid() && getSender().isOnline();
    }
}
