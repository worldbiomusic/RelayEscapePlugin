package com.wbm.plugin.util.discord;

import java.time.LocalDateTime;
import java.util.List;

import com.wbm.plugin.util.PlayerDataManager;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.TextChannel;

public class DiscordBot {
	JDA jda;
	PlayerDataManager pDataManager;

	public DiscordBot(PlayerDataManager pDataManager) {
		this.pDataManager = pDataManager;
		this.setupDiscordBot();
	}

	public void setupDiscordBot() {
		JDABuilder jdaBuilder = JDABuilder.createDefault("ODAwNjg5NTE0MDgyMjA1NzM3.YAVyOA.sUdAxssCSU0Wko4alhsFj1tT6GE");

		try {
			jda = jdaBuilder.build();
			jda.getPresence().setStatus(OnlineStatus.ONLINE);
			jda.getPresence().setActivity(Activity.playing("마크"));

			jda.addEventListener(new ChatListener(this.pDataManager));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void sendMsgToChannelWithTime(String channelName, String msg) {
		LocalDateTime now = LocalDateTime.now();
		String timeStr = now.getHour() + ":" + now.getMinute() + " ";
		List<TextChannel> channels = this.jda.getTextChannelsByName(channelName, true);
		for (TextChannel ch : channels) {
			ch.sendMessage(timeStr + msg).queue();
		}

	}
}
