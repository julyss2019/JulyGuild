package com.github.julyss2019.mcsp.julyguild.gui;

import com.github.julyss2019.mcsp.julyguild.guild.Guild;
import com.github.julyss2019.mcsp.julyguild.guild.GuildBank;
import com.github.julyss2019.mcsp.julyguild.guild.member.GuildMember;
import com.github.julyss2019.mcsp.julyguild.placeholder.PlaceholderContainer;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public abstract class ShopItemConfirmGUI extends BaseConfirmGUI {
    private Guild guild;
    private double price;

    public ShopItemConfirmGUI(@NotNull GUI lastGUI, @NotNull GuildMember guildMember, @NotNull ConfigurationSection section, @NotNull PlaceholderContainer placeholderContainer, double price) {
        super(lastGUI, guildMember.getGuildPlayer(), section, placeholderContainer);

        this.guild = guildMember.getGuild();
        this.price = price;
    }

    public Guild getGuild() {
        return guild;
    }

    @Override
    public boolean canUse() {
        return guild.getGuildBank().has(GuildBank.BalanceType.GMONEY, price);
    }

    @Override
    public void onConfirm() {
        guild.getGuildBank().withdraw(GuildBank.BalanceType.GMONEY, price);
        onPaid();
    }

    public abstract void onPaid();

    @Override
    public void onCancel() {
        back();
    }

    @Override
    public GUI.Type getGUIType() {
        return GUI.Type.SHOP_CONFIRM;
    }
}
