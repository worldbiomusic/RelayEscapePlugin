package com.wbm.plugin.util.general;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;

public class TPManager {

    /*
     * token을 지불하고 tp할 수 있는 표지판
     */
    
    private static Map<String, Location> tpData = new HashMap<>();
    
    public static void registerLocation(String title, Location loc) {
	tpData.put(title, loc);
    }
    
    public static Location getLocation(String title) {
	return tpData.get(title);
    }
}
