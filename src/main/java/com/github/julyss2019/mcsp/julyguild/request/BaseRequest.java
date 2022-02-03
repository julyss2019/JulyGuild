package com.github.julyss2019.mcsp.julyguild.request;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.guild.Guild;
import com.github.julyss2019.mcsp.julyguild.guild.GuildManager;
import com.github.julyss2019.mcsp.julyguild.guild.member.GuildMember;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayerManager;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public abstract class BaseRequest<T1 extends Sender, T2 extends Receiver> implements Request {
    private JulyGuild plugin = JulyGuild.inst();
    private GuildManager guildManager = plugin.getGuildManager();
    private GuildPlayerManager guildPlayerManager = plugin.getGuildPlayerManager();
    private long creationTime;
    private UUID uuid;
    private T1 sender;
    private T2 receiver;

    public BaseRequest() {}

    public BaseRequest(@NotNull T1 sender, @NotNull T2 receiver) {
        if (sender.equals(receiver)) {
            throw new RuntimeException("Sender 和 Receiver 不能为同一个对象");
        }

        this.sender = sender;
        this.receiver = receiver;
        this.uuid = UUID.randomUUID();
        this.creationTime = System.currentTimeMillis();
    }

    @Override
    public long getCreationTime() {
        return creationTime;
    }

    @Override
    public UUID getUuid() {
        return uuid;
    }

    @Override
    public T1 getSender() {
        return sender;
    }

    @Override
    public T2 getReceiver() {
        return receiver;
    }

    @Override
    public void onSave(@NotNull ConfigurationSection section) {
        section.set("creation_time", getCreationTime());
        section.set("uuid", getUuid().toString());
        section.set("type", getType().name());

        if (sender instanceof GuildPlayer) {
            section.set("sender.type", Sender.Type.GUILD_PLAYER.name());

            GuildPlayer guildPlayer = (GuildPlayer) sender;

            section.set("sender.guild_player.uuid", guildPlayer.getUuid().toString());
        } else if (sender instanceof GuildMember) {
            section.set("sender.type", Sender.Type.GUILD_MEMBER.name());

            GuildMember guildMember = (GuildMember) sender;

            section.set("sender.guild.uuid", guildMember.getGuild().getUuid().toString());
            section.set("sender.guild_member.uuid", guildMember.getUuid().toString());
        } else if (sender instanceof Guild) {
            section.set("sender.type", Sender.Type.GUILD.name());

            Guild guild = (Guild) sender;

            section.set("sender.guild.uuid", guild.getUuid().toString());
        }

        if (receiver instanceof GuildPlayer) {
            section.set("receiver.type", Receiver.Type.GUILD_PLAYER.name());

            GuildPlayer guildPlayer = (GuildPlayer) receiver;

            section.set("receiver.guild_player.uuid", guildPlayer.getUuid().toString());
        } else if (receiver instanceof GuildMember) {
            section.set("receiver.type", Receiver.Type.GUILD_MEMBER.name());

            GuildMember guildMember = (GuildMember) receiver;

            section.set("receiver.guild.uuid", guildMember.getGuild().getUuid().toString());
            section.set("receiver.guild_member.uuid", guildMember.getUuid().toString());
        } else if (receiver instanceof Guild) {
            section.set("receiver.type", Receiver.Type.GUILD.name());

            Guild guild = (Guild) receiver;

            section.set("receiver.guild.uuid", guild.getUuid().toString());
        }
    }



    @Override
    public void onLoad(@NotNull ConfigurationSection section) {
        this.creationTime = section.getLong("creation_time");
        this.uuid = UUID.fromString(section.getString("uuid"));

        switch (Sender.Type.valueOf(section.getString("sender.type"))) {
            case GUILD:
                this.sender = (T1) guildManager.getGuild(UUID.fromString(section.getString("sender.guild.uuid")));
                break;
            case GUILD_PLAYER:
                this.sender = (T1) guildPlayerManager.getGuildPlayer(UUID.fromString(section.getString("sender.guild_player.uuid")));
                break;
            case GUILD_MEMBER:
                this.sender = (T1) guildManager.getGuild(UUID.fromString(section.getString("sender.guild.uuid"))).getMember(UUID.fromString(section.getString("sender.guild_member.uuid")));
                break;
        }

        switch (Receiver.Type.valueOf(section.getString("receiver.type"))) {
            case GUILD:
                this.receiver = (T2) JulyGuild.inst().getGuildManager().getGuild(UUID.fromString(section.getString("receiver.guild.uuid")));
                break;
            case GUILD_PLAYER:
                this.receiver = (T2) JulyGuild.inst().getGuildPlayerManager().getGuildPlayer(UUID.fromString(section.getString("receiver.guild_player.uuid")));
                break;
            case GUILD_MEMBER:
                this.receiver = (T2) guildManager.getGuild(UUID.fromString(section.getString("receiver.guild.uuid"))).getMember(UUID.fromString(section.getString("receiver.guild_member.uuid")));
                break;
        }
    }
}
