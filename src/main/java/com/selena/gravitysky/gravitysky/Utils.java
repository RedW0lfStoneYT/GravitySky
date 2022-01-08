package com.selena.gravitysky.gravitysky;

import org.bukkit.ChatColor;

import java.util.*;

public class Utils {

    private static final SortedMap<String, String> colorMap = setUpColors();

    private static SortedMap<String, String> setUpColors() {

        SortedMap<String, String> colors = new TreeMap<>();

        colors.put("${BLACK}", "&0");
        colors.put("${DARK_BLUE}", "&1");
        colors.put("${DARK_GREEN}", "&2");
        colors.put("${DARK_AQUA}", "&3");
        colors.put("${DARK_RED}", "&4");
        colors.put("${PURPLE}", "&5");
        colors.put("${GOLD}", "&6");
        colors.put("${GREY}", "&7");
        colors.put("${GRAY}", "&7");
        colors.put("${DARK_GERY}", "&8");
        colors.put("${DARK_GARY}", "&8");
        colors.put("${BLUE}", "&9");
        colors.put("${GREEN}", "&a");
        colors.put("${AQUA}", "&b");
        colors.put("${RED}", "&c");
        colors.put("${PINK}", "&d");
        colors.put("${YELLOW}", "&e");
        colors.put("${WHITE}", "&f");
        colors.put("${BOLD}", "&l");
        colors.put("${STRIKE}", "&m");
        colors.put("${UNDERLINE}", "&n");
        colors.put("${ITALIC}", "&o");
        colors.put("${TILT}", "&o");
        colors.put("${MAGIC}", "&k");
        colors.put("${RESET}", "&r");




        return colors;

    }

    public static <T extends Enum<?>> T randomEnum(Class<T> clazz, Random random){
        int x = random.nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }

    // Useful for debugging btw
    public static int getLineNumber() {
        return Thread.currentThread().getStackTrace()[2].getLineNumber();
    }

    // Idk if this is bad or not but its better than needing to remember if aqua was &b or &d
    public static String color(String message) {
        for (String color : colorMap.keySet()) {
            message = message.replace(color, colorMap.get(color));
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }


}
