package com.wbm.plugin.util.discord;

import com.wbm.plugin.data.PlayerData;
import com.wbm.plugin.util.PlayerDataManager;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ChatListener extends ListenerAdapter {
    PlayerDataManager pDataManager;

    public ChatListener(PlayerDataManager pDataManager) {
	this.pDataManager = pDataManager;
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
	if (e.getChannel().getName().equalsIgnoreCase("mcbot")) {
	    String[] msg = e.getMessage().getContentRaw().split(" ");
	    String first = msg[0];
	    if (first.equalsIgnoreCase("mcbot")) {
		e.getChannel().sendMessage("HI I'm MCBOT").queue();
	    } else if (first.equalsIgnoreCase("player")) {
		String pName = msg[1];

		PlayerData pData = this.pDataManager.getPlayerData(pName);

		e.getChannel().sendMessage(pData.toString()).queue();
	    }
	}
    }
}
