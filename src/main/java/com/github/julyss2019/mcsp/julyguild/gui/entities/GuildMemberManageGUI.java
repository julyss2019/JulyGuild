package com.github.julyss2019.mcsp.julyguild.gui.entities;

import com.github.julyss2019.mcsp.julyguild.DebugMessage;
import com.github.julyss2019.mcsp.julyguild.config.gui.PriorityConfigGUI;
import com.github.julyss2019.mcsp.julyguild.config.gui.item.PriorityItem;
import com.github.julyss2019.mcsp.julyguild.guild.Guild;
import com.github.julyss2019.mcsp.julyguild.guild.member.GuildPermission;
import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.config.gui.item.GUIItemManager;
import com.github.julyss2019.mcsp.julyguild.gui.BaseConfirmGUI;
import com.github.julyss2019.mcsp.julyguild.gui.BasePlayerGUI;
import com.github.julyss2019.mcsp.julyguild.gui.GUI;
import com.github.julyss2019.mcsp.julyguild.guild.member.GuildMember;
import com.github.julyss2019.mcsp.julyguild.logger.JulyGuildLogger;
import com.github.julyss2019.mcsp.julyguild.placeholder.PlaceholderContainer;
import com.github.julyss2019.mcsp.julylibrary.inventory.ItemListener;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class GuildMemberManageGUI extends BasePlayerGUI {
    private final JulyGuild plugin = JulyGuild.inst();
    private final ConfigurationSection thisGUISection = plugin.getGUIYaml("GuildMemberManageGUI");
    private final Guild guild;
    private final GuildMember managerGuildMember;
    private final GuildMember targetGuildMember;
    private final Player targetBukkitPlayer;
    private Set<GuildPermission> ownedPermissions;


    public GuildMemberManageGUI(@Nullable GUI lastGUI, @NotNull GuildMember mangerGuildMember, @NotNull GuildMember targetGuildMember) {
        super(lastGUI, Type.MEMBER_MANAGE, mangerGuildMember.getGuildPlayer());

        this.managerGuildMember = mangerGuildMember;
        this.targetGuildMember = targetGuildMember;
        this.targetBukkitPlayer = targetGuildMember.getGuildPlayer().getBukkitPlayer();
        this.guild = mangerGuildMember.getGuild();
    }

    @Override
    public Inventory createInventory() {
        this.ownedPermissions = targetGuildMember.getPermissions(); // 确保设置的和界面显示的一直（期间可能会被修改）

        PriorityConfigGUI.Builder guiBuilder = new PriorityConfigGUI.Builder();

        JulyGuildLogger.debug(DebugMessage.BEGIN_GUI_LOAD_BASIC);
        guiBuilder.fromConfig(thisGUISection, targetBukkitPlayer, new PlaceholderContainer().addGuildMemberPlaceholders(targetGuildMember));
        JulyGuildLogger.debug(DebugMessage.END_GUI_LOAD_BASIC);

        JulyGuildLogger.debug(DebugMessage.BEGIN_GUI_LOAD_ITEM, "items.back");
        guiBuilder.item(GUIItemManager.getIndexItem(thisGUISection.getConfigurationSection("items.back"), targetBukkitPlayer), new ItemListener() {
            @Override
            public void onClick(InventoryClickEvent event) {
                if (canBack()) {
                    back();
                }
            }
        });
        JulyGuildLogger.debug(DebugMessage.END_GUI_LOAD_ITEM, "items.back");

        // 如果管理者和目标玩家一样
        if (managerGuildMember.equals(targetGuildMember)) {
            return guiBuilder.build();
        }

        // 是会长或自己有权限且对方无踢人权限
        if (guild.isOwner(managerGuildMember) || managerGuildMember.hasPermission(GuildPermission.MEMBER_KICK) && !targetGuildMember.hasPermission(GuildPermission.MEMBER_KICK)) {
            JulyGuildLogger.debug(DebugMessage.BEGIN_GUI_LOAD_ITEM, "items.member_kick");
            guiBuilder.item(GUIItemManager.getPriorityItem(thisGUISection.getConfigurationSection("items.member_kick"), targetBukkitPlayer, new PlaceholderContainer().addGuildMemberPlaceholders(targetGuildMember)), new ItemListener() {
                @Override
                public void onClick(InventoryClickEvent event) {
                    if (!checkManagerPerOrReopen(GuildPermission.MEMBER_KICK)) {
                        return;
                    }

                    new BaseConfirmGUI(GuildMemberManageGUI.this
                            , guildPlayer
                            , thisGUISection.getConfigurationSection("items.member_kick.ConfirmGUI")
                            , new PlaceholderContainer().add("target", targetGuildMember.getName())) {
                        @Override
                        public boolean canUse() {
                            return targetGuildMember.isValid() && managerGuildMember.isValid()
                                    && (guild.isOwner(managerGuildMember) ||
                                    (managerGuildMember.hasPermission(GuildPermission.MEMBER_KICK) && !targetGuildMember.hasPermission(GuildPermission.MEMBER_KICK)));
                        }

                        @Override
                        public void onCancel() {
                            back();
                        }

                        @Override
                        public void onConfirm() {
                            guild.removeMember(targetGuildMember);
                            back();
                        }
                    }.open();
                }
            });
            JulyGuildLogger.debug(DebugMessage.END_GUI_LOAD_ITEM, "items.member_kick");
        }

        if (managerGuildMember.hasPermission(GuildPermission.MANAGE_PERMISSION)) {
            setPermissionItem(guiBuilder, GuildPermission.MEMBER_KICK);
            setPermissionItem(guiBuilder, GuildPermission.SET_MEMBER_DAMAGE);
            setPermissionItem(guiBuilder, GuildPermission.PLAYER_JOIN_CHECK);
            setPermissionItem(guiBuilder, GuildPermission.SET_ANNOUNCEMENTS);
            setPermissionItem(guiBuilder, GuildPermission.USE_SHOP);
            setPermissionItem(guiBuilder, GuildPermission.USE_ICON_REPOSITORY);
        }

        return guiBuilder.build();
    }

    /**
     * 检查管理者权限，如果没权限则尝试重开GUI
     * @param guildPermission
     * @return
     */
    private boolean checkManagerPerOrReopen(@NotNull GuildPermission guildPermission) {
        if (!managerGuildMember.hasPermission(guildPermission) || targetGuildMember.hasPermission(guildPermission)) {
            reopen();
            return false;
        }

        return true;
    }

    private void setPermissionItem(@NotNull PriorityConfigGUI.Builder guiBuilder, @NotNull GuildPermission guildPermission) {
        guiBuilder.item(getPermissionItem(guildPermission), getPermissionItemListener(guildPermission));
    }

    private ItemListener getPermissionItemListener(@NotNull GuildPermission guildPermission) {
        return new ItemListener() {
            @Override
            public void onClick(InventoryClickEvent event) {
                if (!checkManagerPerOrReopen(GuildPermission.MANAGE_PERMISSION)) {
                    return;
                }

                targetGuildMember.setPermission(guildPermission, !ownedPermissions.contains(guildPermission));
                reopen();
            }
        };
    }

    /**
     * 得到权限状态物品（give，take两种状态）
     */
    private PriorityItem getPermissionItem(@NotNull GuildPermission guildPermission) {
        String path = "items.per_" + guildPermission.name().toLowerCase() + "." + (targetGuildMember.hasPermission(guildPermission) ? "take" : "give");
        ConfigurationSection section = thisGUISection.getConfigurationSection(path);

        JulyGuildLogger.debug(DebugMessage.BEGIN_GUI_LOAD_ITEM, path);
        PriorityItem priorityItem = GUIItemManager.getPriorityItem(section, targetBukkitPlayer, new PlaceholderContainer().addGuildMemberPlaceholders(targetGuildMember));;
        JulyGuildLogger.debug(DebugMessage.END_GUI_LOAD_ITEM, path);
        return priorityItem;
    }

    @Override
    public boolean canUse() {
        if (!managerGuildMember.isValid() || !targetGuildMember.isValid()) {
            return false;
        }

        return managerGuildMember.getPermissions().size() > 0;
    }
}
