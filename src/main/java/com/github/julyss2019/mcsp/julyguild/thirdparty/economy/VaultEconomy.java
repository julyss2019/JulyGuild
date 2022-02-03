package com.github.julyss2019.mcsp.julyguild.thirdparty.economy;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;

public class VaultEconomy {
    private Economy economy;

    public VaultEconomy(Economy economy) {
        this.economy = economy;
    }

    public boolean has(Player player, double amount) {
        if (amount <= 0) {
            throw new RuntimeException("数量不合法: " + amount);
        }

        return getBalance(player) >= amount;
    }

    public void withdraw(Player player, double amount) {
        if (amount <= 0) {
            throw new RuntimeException("数量不合法: " + amount);
        }

        if (economy.withdrawPlayer(player, amount).type != EconomyResponse.ResponseType.SUCCESS) {
            throw new RuntimeException("扣除金币失败");
        }
    }

    public void deposit(Player player, double amount) {
        if (amount <= 0) {
            throw new RuntimeException("数量不合法: " + amount);
        }

        if (economy.depositPlayer(player, amount).type != EconomyResponse.ResponseType.SUCCESS) {
            throw new RuntimeException("扣除金币失败");
        }
    }

    public double getBalance(Player player) {
        return economy.getBalance(player);
    }
}
