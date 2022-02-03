package com.github.julyss2019.mcsp.julyguild.gui;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.gui.entities.MainGUI;
import com.github.julyss2019.mcsp.julyguild.logger.JulyGuildLogger;
import com.github.julyss2019.mcsp.julyguild.player.GuildPlayer;
import com.github.julyss2019.mcsp.julyguild.util.Util;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Optional;

/**
 * 采用被动式，容错式更新设计，每次点击如果遇到无效的情况则强制更新，否则继续使用
 */
public interface GUI {
    enum Type {
        CREATE,
        INFO,
        MEMBER_LIST,
        MINE,
        MAIN,
        PLAYER_JOIN_CHECK,
        MEMBER_MANAGE,
        DONATE,
        SHOP,
        CONFIRM,
        SHOP_CONFIRM,
        ICON_REPOSITORY,
        BAG,
        WAR
    }

    /**
     * 决定GUI能否被看到
     * @return
     */
    boolean canUse();

    GUI getLastGUI();

    GuildPlayer getGuildPlayer();

    Inventory createInventory();

    Type getGUIType();

    default Player getBukkitPlayer() {
        return getGuildPlayer().getBukkitPlayer();
    }

    default void openLater(long tick) {
        new BukkitRunnable() {
            @Override
            public void run() {
                open();
            }
        }.runTaskLater(JulyGuild.inst(), tick);
    }

    default void open() {
        // 检查能否使用
        if (!canUse()) {
            Util.sendMsg(getBukkitPlayer(), "&f当前 GUI 暂时无法使用.");

            GUI lastGUI = getLastGUI();

            if (lastGUI != null) {
                lastGUI.open();
            } else {
                new MainGUI(getGuildPlayer()).open();
            }

            return;
        }

        String className = this.getClass().getSimpleName();

        if (className.equalsIgnoreCase("")) {
            className = this.getClass().getTypeName();
        }

        JulyGuildLogger.debug("=== 尝试创建GUI " + className + " ===");

        Inventory inventory = createInventory();

        if (inventory == null) {
            throw new RuntimeException("getInventory() 不能返回 null");
        }

        JulyGuildLogger.debug("=== 创建GUI " + className + " 完毕 ===");

        getGuildPlayer().getBukkitPlayer().openInventory(inventory);
        getGuildPlayer().setUsingGUI(this);

        JulyGuildLogger.debug("玩家 '" + getGuildPlayer().getName() + "' 打开了 GUI '" + className + "'.");
    }

    default boolean canBack() {
        return getLastGUI() != null;
    }

    default void back() {
        close();

        GUI lastGUI = Optional.ofNullable(getLastGUI()).orElseThrow(() -> new RuntimeException("没有上一个GUI了"));

        lastGUI.open();
    }

    /**
     * 先关闭等待later秒后再返回
     * @param later
     */
    default void back(long later) {
        close();

        GUI lastGUI = Optional.ofNullable(getLastGUI()).orElseThrow(() -> new RuntimeException("没有上一个GUI了"));

        new BukkitRunnable() {
            @Override
            public void run() {
                lastGUI.open();
            }
        }.runTaskLater(JulyGuild.inst(), later);
    }

    default void close() {
        if (!this.equals(getGuildPlayer().getUsingGUI())) {
            throw new RuntimeException("当前GUI没在使用");
        }

        getGuildPlayer().closeInventory();
    }

    /**
     * 关闭，打开
     */

    default void reopen() {
        close();
        open();
    }

    /**
     * 关闭，延时，打开
     * @param later
     */
    default void reopen(long later) {
        close();

        new BukkitRunnable() {
            @Override
            public void run() {
                open();
            }
        }.runTaskLater(JulyGuild.inst(), later);
    }
}
