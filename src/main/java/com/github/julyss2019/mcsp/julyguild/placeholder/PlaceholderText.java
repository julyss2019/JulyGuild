package com.github.julyss2019.mcsp.julyguild.placeholder;

import com.github.julyss2019.mcsp.julyguild.JulyGuild;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceholderText {
    /**
     * 通过 PlaceholderAPI
     * @return
     */
    public static List<String> replacePlaceholders(@NotNull List<String> list, @NotNull Player player) {
        return JulyGuild.inst().isPlaceHolderAPIEnabled() ? PlaceholderAPI.setPlaceholders(player, list) : list;
    }

    /**
     * 通过 PlaceholderAPI
     * @return
     */
    public static String replacePlaceholders(@NotNull String s, @NotNull Player player) {
        return JulyGuild.inst().isPlaceHolderAPIEnabled() ? PlaceholderAPI.setPlaceholders(player, s) : s;
    }

    public static List<String> replacePlaceholders(List<String> list, PlaceholderContainer placeholderContainer) {
        List<String> result = new ArrayList<>();

        for (String s : list) {
            result.add(PlaceholderText.replacePlaceholders(s, placeholderContainer));
        }

        return result;
    }

    public static List<String> replacePlaceholders(@NotNull List<String> list, @NotNull PlaceholderContainer placeholderContainer, @NotNull Player player) {
        List<String> result = new ArrayList<>();

        for (String s : list) {
            result.add(replacePlaceholders(replacePlaceholders(s, placeholderContainer), player));
        }

        return result;
    }

    public static String replacePlaceholders(@NotNull String s, @NotNull PlaceholderContainer placeholderContainer, @NotNull Player player) {
        return replacePlaceholders(replacePlaceholders(s, placeholderContainer), player);
    }


    public static String replacePlaceholders(@NotNull String s, @NotNull PlaceholderContainer placeholderContainer) {
        String result = s;

        for (Placeholder placeholder : placeholderContainer.getPlaceholders()) {
            // 对正则符号进行转义
            result = ignoreCaseReplace(result,
                    ("{" + placeholder.getKey() + "}")
                    .replace("\\", "\\\\").replace("*", "\\*")
                    .replace("+", "\\+").replace("|", "\\|")
                    .replace("{", "\\{").replace("}", "\\}")
                    .replace("(", "\\(").replace(")", "\\)")
                    .replace("^", "\\^").replace("$", "\\$")
                    .replace("[", "\\[").replace("]", "\\]")
                    .replace("?", "\\?").replace(",", "\\,")
                    .replace(".", "\\.").replace("&", "\\&"), placeholder.getValue().toString());
        }

        return result;
    }

    private static String ignoreCaseReplace(@NotNull String source, @NotNull String target, @NotNull String replacement) {
        Pattern p = Pattern.compile(target, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(source);

        return m.replaceAll(replacement);
    }
}
