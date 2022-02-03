package com.github.julyss2019.mcsp.julyguild.gui.entities;

import com.github.julyss2019.mcsp.julyguild.DebugMessage;
import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import com.github.julyss2019.mcsp.julyguild.config.gui.IndexConfigGUI;
import com.github.julyss2019.mcsp.julyguild.config.gui.item.GUIItemManager;
import com.github.julyss2019.mcsp.julyguild.config.setting.MainSettings;
import com.github.julyss2019.mcsp.julyguild.gui.GUI;
import com.github.julyss2019.mcsp.julyguild.gui.BasePageableGUI;
import com.github.julyss2019.mcsp.julyguild.guild.Guild;
import com.github.julyss2019.mcsp.julyguild.guild.GuildIcon;
import com.github.julyss2019.mcsp.julyguild.guild.member.GuildMember;
import com.github.julyss2019.mcsp.julyguild.guild.member.GuildPermission;
import com.github.julyss2019.mcsp.julyguild.logger.JulyGuildLogger;
import com.github.julyss2019.mcsp.julyguild.placeholder.PlaceholderContainer;
import com.github.julyss2019.mcsp.julyguild.util.Util;
import com.github.julyss2019.mcsp.julylibrary.inventory.InventoryListener;
import com.github.julyss2019.mcsp.julylibrary.inventory.ItemListener;
import com.github.julyss2019.mcsp.julylibrary.item.ItemBuilder;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class GuildIconRepositoryGUI extends BasePageableGUI {
    private JulyGuild plugin = JulyGuild.inst();
    private final ConfigurationSection thisGUISection = plugin.getGUIYaml("GuildIconRepositoryGUI");
    private final List<Integer> itemIndexes; // 得到所有可供公会设置的位置
    private int itemIndexCount;
    private Player bukkitPlayer = getBukkitPlayer();
    private GuildMember guildMember;
    private List<GuildIcon> icons = new ArrayList<>();
    private int iconCount;
    private Guild guild;

    public GuildIconRepositoryGUI(@Nullable GUI lastGUI, GuildMember guildMember) {
        super(lastGUI, Type.ICON_REPOSITORY, guildMember.getGuildPlayer());

        this.guildMember = guildMember;
        this.guild = guildMember.getGuild();

        JulyGuildLogger.debug("开始: 加载 'items.guild_icon.indexes'.");
        this.itemIndexes = Util.getIndexes(thisGUISection.getString("items.guild_icon.indexes"));
        JulyGuildLogger.debug("结束: 加载 'items.guild_icon.indexes'.");
        this.itemIndexCount = itemIndexes.size();
    }

    @Override
    public void update() {
        this.icons = guild.getIcons();

        icons.add(0, null);
        icons.sort((o1, o2) -> o1.equals(guild.getCurrentIcon()) ? -1 : 1);

        this.iconCount = icons.size();
        setPageCount(iconCount % itemIndexCount == 0 ? iconCount / itemIndexCount : iconCount / itemIndexCount + 1);
    }

    @Override
    public boolean canUse() {
        return guildMember.isValid() && guildMember.hasPermission(GuildPermission.USE_ICON_REPOSITORY);
    }

    @Override
    public Inventory createInventory() {
        Map<Integer, GuildIcon> indexMap = new HashMap<>();
        IndexConfigGUI.Builder guiBuilder = new IndexConfigGUI.Builder();

        JulyGuildLogger.debug(DebugMessage.BEGIN_GUI_LOAD_BASIC);
        guiBuilder.fromConfig(thisGUISection, bukkitPlayer, new PlaceholderContainer()
                .add("total_page", getPageCount())
                .add("page", getCurrentPage() + 1));
        JulyGuildLogger.debug(DebugMessage.BEGIN_GUI_LOAD_BASIC);


        guiBuilder.listener(new InventoryListener() {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        int slot = event.getRawSlot();

                        if (!indexMap.containsKey(slot)) {
                            return;
                        }

                        GuildIcon guildIcon = indexMap.get(slot);

                        if (guildIcon == null) {
                            guild.setCurrentIcon(null);
                        } else {
                            if (!guildIcon.isValid()) {
                                reopen();
                                return;
                            }

                            guild.setCurrentIcon(guildIcon);
                        }

                        reopen();
                    }
                });


        JulyGuildLogger.debug(DebugMessage.BEGIN_GUI_LOAD_ITEM, "items.back");
        guiBuilder.item(GUIItemManager.getIndexItem(thisGUISection.getConfigurationSection("items.back"), bukkitPlayer), new ItemListener() {
            @Override
            public void onClick(InventoryClickEvent event) {
                back();
            }
        });
        JulyGuildLogger.debug(DebugMessage.END_GUI_LOAD_ITEM, "items.back");

        if (getPageCount() > 0) {
            int itemCounter = getCurrentPage() * itemIndexCount;
            int loopCount = iconCount - itemCounter < itemIndexCount ? iconCount - itemCounter : itemIndexCount; // 循环次数，根据当前能够显示的数量决定

            for (int i = 0; i < loopCount; i++) {
                GuildIcon guildIcon = icons.get(itemCounter++);
                String path = "items.guild_icon." + (Objects.equals(guildIcon, guild.getCurrentIcon()) ? "using" : "not_using") + ".icon";

                JulyGuildLogger.debug(DebugMessage.BEGIN_GUI_LOAD_ITEM, path);
                ItemBuilder itemBuilder = GUIItemManager.getItemBuilder(thisGUISection.getConfigurationSection(path)
                        , bukkitPlayer);
                JulyGuildLogger.debug(DebugMessage.END_GUI_LOAD_ITEM, path);

                if (guildIcon == null) {
                    itemBuilder.material(MainSettings.getGuildIconDefaultMaterial()).durability(MainSettings.getGuildIconDefaultDurability());

                    if (!StringUtils.isEmpty(MainSettings.getGuildIconDefaultFirstLore())) {
                        itemBuilder.insertLore(0, MainSettings.getGuildIconDefaultFirstLore());
                    }
                } else {
                    itemBuilder.material(guildIcon.getMaterial()).durability(guildIcon.getDurability());

                    if (guildIcon.getFirstLore() != null) {
                        itemBuilder.insertLore(0, guildIcon.getFirstLore());
                    }
                }

                if (guildIcon != null) {
                    itemBuilder.displayName(guildIcon.getDisplayName());
                }

                guiBuilder.item(itemIndexes.get(i), itemBuilder.build());
                indexMap.put(itemIndexes.get(i), guildIcon);
            }
        }

        return guiBuilder.build();
    }
}
