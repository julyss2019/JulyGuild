package com.github.julyss2019.mcsp.julyguild.command;

import com.github.julyss2019.mcsp.julyguild.util.Util;
import com.github.julyss2019.mcsp.julylibrary.commandv2.JulyCommand;
import com.github.julyss2019.mcsp.julylibrary.commandv2.MainCommand;
import com.github.julyss2019.mcsp.julylibrary.commandv2.SenderType;
import com.github.julyss2019.mcsp.julylibrary.commandv2.SubCommand;
import com.github.julyss2019.mcsp.julylibrary.utils.ItemUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@MainCommand(firstArg = "helper", description = "帮助者相关")
public class HelperCommand implements JulyCommand {
	@SubCommand(firstArg = "getItemInfo", description = "获得物品信息", length = 0, senders = SenderType.PLAYER, permission = "JulyGuild.admin")
	public void getItemInfo(CommandSender sender, String[] args) {
		Player player = (Player) sender;
		ItemStack itemStack = player.getItemInHand();

		if (!ItemUtil.isValid(itemStack)) {
			Util.sendMsg(sender, "&c物品不合法.");
			return;
		}

		Util.sendMsg(sender, "&fmaterial = " + itemStack.getType().name());
		Util.sendMsg(sender, "&fdurability = " + itemStack.getDurability());
	}
}
