package net.snuck.clans.util;

import net.snuck.clans.Main;

public class ConfigUtil {

    public static String getString(String path) {
        return Main.getPlugin().getConfig().getString(path).replace("&", "§");
    }

}
