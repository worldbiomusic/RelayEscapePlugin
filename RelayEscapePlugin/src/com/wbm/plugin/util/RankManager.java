package com.wbm.plugin.util;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.bukkit.Bukkit;

import com.wbm.plugin.Main;
import com.wbm.plugin.data.PlayerData;
import com.wbm.plugin.util.config.DataMember;
import com.wbm.plugin.util.discord.DiscordBot;

public class RankManager implements DataMember {
	/*
	 * [rank list] token challengingCount clearCount RoomCount
	 */
	PlayerDataManager pDataManager;
	RoomManager roomManager;
	DiscordBot discordBot;

	// 마지막으로 랭크 보상이 지급된 날짜
	private LocalDateTime lastRewardDateTime;

	public RankManager(PlayerDataManager pDataManager, RoomManager roomManager,DiscordBot discordBot) {
		this.pDataManager = pDataManager;
		this.roomManager = roomManager;
		this.discordBot = discordBot;
	}

	public List<PlayerData> getTokenRankList() {
		List<PlayerData> list = new ArrayList<PlayerData>(this.pDataManager.getPlayerData().values());

		Comparator<PlayerData> comparator = new Comparator<PlayerData>() {

			@Override
			public int compare(PlayerData o1, PlayerData o2) {
				// descending (내림차순)
				return o2.getToken() - o1.getToken();
			}

		};

		Collections.sort(list, comparator);

//		BroadcastTool.debug(list.toString());
		return list;
	}

	public List<PlayerData> getChallengingCountRankList() {
		List<PlayerData> list = new ArrayList<PlayerData>(this.pDataManager.getPlayerData().values());

		Collections.sort(list, (o1, o2) -> o2.getChallengingCount() - o1.getChallengingCount());
//		BroadcastTool.debug(list.toString());

		return list;
	}

	public List<PlayerData> getClearCountRankList() {
		List<PlayerData> list = new ArrayList<PlayerData>(this.pDataManager.getPlayerData().values());

		Collections.sort(list, (o1, o2) -> o2.getClearCount() - o1.getClearCount());
//		BroadcastTool.debug(list.toString());

		return list;
	}

	public List<PlayerData> getRoomCountRankList() {
		/*
		 * player가 가지고 있는 room갯수에 따른 rank
		 */
		List<PlayerData> list = new ArrayList<PlayerData>(this.pDataManager.getPlayerData().values());

		Comparator<PlayerData> comparator = new Comparator<PlayerData>() {

			@Override
			public int compare(PlayerData o1, PlayerData o2) {
				int o1Count = roomManager.getOwnRooms(o1.getName()).size();
				int o2Count = roomManager.getOwnRooms(o2.getName()).size();
				return o2Count - o1Count;
			}

		};

		Collections.sort(list, comparator);
//		BroadcastTool.debug(list.toString());

		return list;
	}

	public void rewardRankPlayers() {
		// 각 rank 분야에서 1,2,3 등 보상 지급 (100, 50, 30)
		// 매주 일요일에 지급됨
		// 예외적인 상황으로 못 지급될 수도 있기 때문에 flag하나 설정해서 사용하기

		Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), new Runnable() {

			@Override
			public void run() {
				LocalDateTime now = LocalDateTime.now();
				int dayOfMonth = now.getDayOfMonth();
				DayOfWeek dayOfWeek = now.getDayOfWeek();
				
				// 매주 토요일에 검사
				if (dayOfWeek == DayOfWeek.SATURDAY) {
					// 저장된 lastRewardDateTime의 dayOfMonth가 현재 dayOfMonth와 다를시 보상 지급후,
					// lastRewardDateTime을 now로 변경
					if (lastRewardDateTime == null || lastRewardDateTime.getDayOfMonth() != dayOfMonth) {
//						System.out.println(ChatColor.RED + "REWARD!!!!!!!!!!!!!");
//						System.out.println("this.lastRewardDateTime.getDayOfMonth() : "+this.lastRewardDateTime.getDayOfMonth() );
//						System.out.println("now dayOfMonth: " + dayOfMonth);

						// discord 알림
						int month = now.getMonthValue();
						discordBot.sendMsgToChannelWithTime(Setting.DISCORD_CH_SERVER_RANK,
								"========== " + month + "/" + dayOfMonth + " ==========");

						// reward 지급
						List<PlayerData> tokenRank = getTokenRankList();
						List<PlayerData> challengingRank = getChallengingCountRankList();
						List<PlayerData> clearRank = getClearCountRankList();
						List<PlayerData> roomCountRank = getRoomCountRankList();

						String[] listStr = { "tokenRank", "challengingRank", "clearRank", "roomCountRank" };
						List<List<PlayerData>> rankListList = new ArrayList<List<PlayerData>>();
						rankListList.add(tokenRank);
						rankListList.add(challengingRank);
						rankListList.add(clearRank);
						rankListList.add(roomCountRank);

						for (int i = 0; i < rankListList.size(); i++) {
							List<PlayerData> rankList = rankListList.get(i);
							discordBot.sendMsgToChannelWithTime(Setting.DISCORD_CH_SERVER_RANK, "[" + listStr[i] + "]");
							for (int x = 0; x < rankList.size(); x++) {
								PlayerData pData = rankList.get(x);
								switch (x) {
								case 0:
									pData.plusToken(Setting.RANK_FIRST_TOKEN);
									discordBot.sendMsgToChannelWithTime(Setting.DISCORD_CH_SERVER_RANK,
											"[" + 1 + "]" + pData.getName());
									break;
								case 1:
									pData.plusToken(Setting.RANK_SECOND_TOKEN);
									discordBot.sendMsgToChannelWithTime(Setting.DISCORD_CH_SERVER_RANK,
											"[" + 2 + "]" + pData.getName());
									break;
								case 2:
									pData.plusToken(Setting.RANK_THIRD_TOKEN);
									discordBot.sendMsgToChannelWithTime(Setting.DISCORD_CH_SERVER_RANK,
											"[" + 3 + "]" + pData.getName());
									break;
								}
							}
							discordBot.sendMsgToChannelWithTime(Setting.DISCORD_CH_SERVER_RANK, "==========");
						}

						// lastRewardDateTime를 now로 변경
						lastRewardDateTime = LocalDateTime.now();
					}
				}
			}
		}, 0, Setting.REWARD_RANK_DELAY_TIME);

	}

	@Override
	public void installData(Object obj) {
		this.lastRewardDateTime = (LocalDateTime) obj;
	}

	@Override
	public Object getData() {
		return this.lastRewardDateTime;
	}

	@Override
	public String getDataMemberName() {
		return "lastRewardDateTime";
	}

}
