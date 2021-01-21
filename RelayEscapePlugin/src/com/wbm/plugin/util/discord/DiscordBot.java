package com.wbm.plugin.util.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

public class DiscordBot {
    
    public DiscordBot() {
//	this.setupDiscordBot();
    }

    public void setupDiscordBot() {
	JDABuilder jdaBuilder = JDABuilder.createDefault("ODAwNjg5NTE0MDgyMjA1NzM3.YAVyOA.sUdAxssCSU0Wko4alhsFj1tT6GE");
	
	JDA jda;
	try {
	    jda = jdaBuilder.build();
	    jda.getPresence().setStatus(OnlineStatus.ONLINE);
	    jda.getPresence().setActivity(Activity.playing("Relay Escape"));
	} catch (Exception e) {
	    e.printStackTrace();
	}
	
    }
}
