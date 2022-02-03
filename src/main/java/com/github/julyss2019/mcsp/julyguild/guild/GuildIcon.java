package com.github.julyss2019.mcsp.julyguild.guild;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class GuildIcon {
    private Guild guild;
    private UUID uuid;
    private String displayName;
    private Material material;
    private short durability;
    private String firstLore;

    public GuildIcon(@NotNull Guild guild, @NotNull UUID uuid) {
        this.guild = guild;
        this.uuid = uuid;

        ConfigurationSection iconSection = guild.getYaml().getConfigurationSection("icons").getConfigurationSection(uuid.toString());

        this.material = Material.valueOf(iconSection.getString("material"));
        this.durability = (short) iconSection.getInt("durability");
        this.firstLore = iconSection.getString("first_lore");
        this.displayName = iconSection.getString("display_name");
    }

    public UUID getUuid() {
        return uuid;
    }

    public Guild getGuild() {
        return guild;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Material getMaterial() {
        return material;
    }

    public short getDurability() {
        return durability;
    }

    public String getFirstLore() {
        return firstLore;
    }

    public boolean isValid() {
        return guild.getIcons().contains(this);
    }
}
