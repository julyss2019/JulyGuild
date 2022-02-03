package com.github.julyss2019.mcsp.julyguild.request;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.request.entities.TpAllRequest;
import com.github.julyss2019.mcsp.julyguild.request.entities.JoinRequest;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface Request<T1 extends Sender, T2 extends Receiver> {
    enum Type {
        TP_ALL(TpAllRequest.class),
        JOIN(JoinRequest.class);

        private Class<? extends Request> clazz;

        Type(Class<? extends Request> clazz) {
            this.clazz = clazz;
        }

        public Class<? extends Request> getClazz() {
            return clazz;
        }
    }

    T1 getSender();

    T2 getReceiver();

    long getCreationTime();

    UUID getUuid();

    Type getType();

    void onSave(@NotNull ConfigurationSection section);

    void onLoad(@NotNull ConfigurationSection section);

    boolean isValid();

    default void delete() {
        JulyGuild.inst().getRequestManager().deleteRequest(this);
    }

    default void send() {
        JulyGuild.inst().getRequestManager().sendRequest(this);
    }
}
