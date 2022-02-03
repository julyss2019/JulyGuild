package com.github.julyss2019.mcsp.julyguild.util;

import com.github.julyss2019.mcsp.julyguild.LangHelper;
import com.github.julyss2019.mcsp.julyguild.placeholder.PlaceholderContainer;
import com.github.julyss2019.mcsp.julyguild.placeholder.PlaceholderText;
import com.github.julyss2019.mcsp.julylibrary.message.JulyMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import parsii.eval.Parser;
import parsii.tokenizer.ParseException;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class Util {
    public static final SimpleDateFormat YYYY_MM_DD_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat HH_MM_SS_FORMAT = new SimpleDateFormat("HH:mm:ss");
    public static final DecimalFormat SIMPLE_DECIMAL_FORMAT = new DecimalFormat("0.00");

    /**
     * 通过字符串来获得整数列表
     * @param str 以 "," 作为分隔符的字符串 以 "-" 作为范围界定符的字符串 单个字符串
     * @return
     */
    public static List<Integer> getIndexes(@NotNull String str) {
        List<Integer> result = new ArrayList<>();

        try {
            String[] split1 = str.split(",");

            for (String splitStr : split1) {
                String[] split2 = splitStr.split("-");

                if (split2.length == 2) {
                    int max = Integer.parseInt(split2[1]);

                    for (int i = Integer.parseInt(split2[0]); i <= max; i++) {
                        result.add(i - 1);
                    }
                } else {
                    result.add(Integer.parseInt(splitStr) - 1);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("字符串 " + str + " 不是有效的表达式");
        }

        return result;
    }

    public static void sendMsg(@NotNull CommandSender cs, @NotNull String msg, @NotNull PlaceholderContainer placeholderContainer) {
        sendMsg(cs, PlaceholderText.replacePlaceholders(msg, placeholderContainer));
    }

    public static void sendMsg(@NotNull CommandSender cs, @NotNull String msg) {
        JulyMessage.sendColoredMessage(cs, LangHelper.Global.getPrefix() + msg);
    }

    public static void sendConsoleMsg(@NotNull String msg) {
        JulyMessage.sendColoredMessage(Bukkit.getConsoleSender(), "&a[JulyGuild] &f" + msg);
    }
}
