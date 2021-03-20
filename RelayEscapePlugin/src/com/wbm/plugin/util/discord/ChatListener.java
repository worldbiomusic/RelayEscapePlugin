package com.wbm.plugin.util.discord;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;

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
		String channelName = e.getChannel().getName();

		switch (channelName) {
		case "mcbot":
			this.mcbot_player_cmd(e);
		case "mcbot-cmd":
			this.mcbot_op_cmd(e);
			break;
		case "server-cmd":
			this.server_cmd(e);
			break;
		}
	}

	private void mcbot_player_cmd(GuildMessageReceivedEvent e) {
		String[] msg = e.getMessage().getContentRaw().split(" ");
		String first = msg[0];

		if (first.equalsIgnoreCase("mcbot")) {
			e.getChannel().sendMessage("Command List").queue();
			this.getMcbotTutorial().forEach((cmd) -> e.getChannel().sendMessage(cmd));
		} else if (first.equalsIgnoreCase("player")) {
			// player <player>
			String pName = msg[1];

			PlayerData pData = this.pDataManager.getPlayerData(pName);
			if (pData != null) {
				e.getChannel().sendMessage(pData.toString()).queue();
			} else {
				this.getMcbotTutorial().forEach((cmd) -> e.getChannel().sendMessage(cmd));
			}
		} else {
			e.getChannel().sendMessage("사용법: player 플레이어닉네임").queue();
		}
	}

	List<String> getMcbotTutorial() {
		List<String> cmdList = new ArrayList<>();
		cmdList.add("mcbot: print tutorial");
		cmdList.add("player <player>: print player data");

		return cmdList;
	}

	private void server_cmd(GuildMessageReceivedEvent e) {
		String cmd = e.getMessage().getContentRaw();

		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
	}

	private void mcbot_op_cmd(GuildMessageReceivedEvent e) {
		String[] msg = e.getMessage().getContentRaw().split(" ");
		String first = msg[0];
		String sec = msg[1];

		if (first.equalsIgnoreCase("token")) {
			// token <cmd> <player> <amount>
			String pName = msg[2];
			PlayerData pData = this.pDataManager.getPlayerData(pName);
			if (sec.equalsIgnoreCase("plus")) {
				int oldToken = pData.getToken();
				int token = Integer.parseInt(msg[3]);
				pData.plusToken(token);
				e.getChannel().sendMessage("Token add from " + oldToken + " to " + pData.getToken());
			} else if (sec.equalsIgnoreCase("minus")) {
				int oldToken = pData.getToken();
				int token = Integer.parseInt(msg[3]);
				pData.minusToken(token);
				e.getChannel().sendMessage("Token minus from " + oldToken + " to " + pData.getToken());
			} else if (sec.equalsIgnoreCase("info")) {
				e.getChannel().sendMessage(pData.toString()).queue();
			}
		}
	}
}

//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
