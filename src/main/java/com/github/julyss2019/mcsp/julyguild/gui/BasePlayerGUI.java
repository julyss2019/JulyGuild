package com.github.julyss2019.mcsp.julyguild.gui;

import com.github.julyss2019.mcsp.julyguild.logger.JulyGuildLogger;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 一个GUI的实现类
 */
public abstract class BasePlayerGUI implements GUI {
    protected final GUI lastGUI;
    protected final GUI.Type type;
    protected final GuildPlayer guildPlayer;

    protected BasePlayerGUI(@Nullable GUI lastGUI, @NotNull GUI.Type guiType, @NotNull GuildPlayer guildPlayer) {
        this.lastGUI = lastGUI;
        this.type = guiType;
        this.guildPlayer = guildPlayer;

        JulyGuildLogger.debug("开始创建 GUI 类 " + getClass().getName() + ".");
    }

    @Override
    public GUI getLastGUI() {
        return lastGUI;
    }

    @Override
    public GuildPlayer getGuildPlayer() {
        return guildPlayer;
    }

    @Override
    public GUI.Type getGUIType() {
        return type;
    }
}
