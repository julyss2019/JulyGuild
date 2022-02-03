package com.github.julyss2019.mcsp.julyguild.request;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;

import java.util.List;

public interface Sender {
    enum Type {
        GUILD, GUILD_PLAYER, GUILD_MEMBER
    }

    default List<Request> getSentRequests() {
        return JulyGuild.inst().getRequestManager().getSentRequests(this);
    }
}
