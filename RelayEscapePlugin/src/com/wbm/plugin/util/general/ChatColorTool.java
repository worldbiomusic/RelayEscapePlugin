package com.wbm.plugin.util.general;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;

public class ChatColorTool {
    public static ChatColor random() {
	List<ChatColor> color = new ArrayList<>();
	color.add(ChatColor.RED);
	color.add(ChatColor.YELLOW);
	color.add(ChatColor.GREEN);
	color.add(ChatColor.BLUE);
	color.add(ChatColor.AQUA);
	color.add(ChatColor.WHITE);
	
	int r = (int)(Math.random() * color.size());
	ChatColor randomColor = color.get(r);
	
	return randomColor;
    }
    
}
