package com.wbm.plugin.util.minigame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.entity.Player;

import com.wbm.plugin.util.config.DataMember;
import com.wbm.plugin.util.enums.MiniGameType;
import com.wbm.plugin.util.general.BroadcastTool;

import net.md_5.bungee.api.ChatColor;

public class MiniGameRankManager implements DataMember {
	/*
	 * 모든 미니게임의 랭크 데이터를 관리하는 클래스
	 */

	private Map<MiniGameType, Map<String, Integer>> miniGameRankData;

	public MiniGameRankManager() {
		this.miniGameRankData = new HashMap<>();

		this.registerMiniGameToRankManager();
	}

	private void registerMiniGameToRankManager() {
		// rankData에 모든 미니게임 등록 (업데이트 역할도 됨)
		for (MiniGameType gameType : MiniGameType.values()) {
			if (!this.containsMiniGame(gameType)) {
//		System.out.println(ChatColor.RED + "ADD NEW MINIGAME DATA: " + gameType.name());
				this.registerMiniGames(gameType);
			}
		}
	}

	public void registerMiniGames(MiniGameType type) {
		// RankData사용할 미니게임(타입) 등록
		this.miniGameRankData.put(type, new HashMap<>());
	}

	public boolean containsMiniGame(MiniGameType type) {
		return this.miniGameRankData.containsKey(type);
	}

	private Map<String, Integer> getMiniGameRankData(MiniGameType type) {
		return this.miniGameRankData.get(type);
	}

	// <name, score>: score기준 내림차순 정렬
	public List<Entry<String, Integer>> getDescendingSortedMapEntrys(Map<String, Integer> rankData) {
		List<Entry<String, Integer>> list = new ArrayList<>(rankData.entrySet());

		Collections.sort(list, new Comparator<Entry<String, Integer>>() {

			@Override
			public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
				return o2.getValue() - o1.getValue();
			}

		});

		return list;
	}

	// <name, score>: score기준 오름차순 정렬
	public List<Entry<String, Integer>> getAscendingSortedMapEntrys(Map<String, Integer> rankData) {
		List<Entry<String, Integer>> list = new ArrayList<>(rankData.entrySet());

		Collections.sort(list, new Comparator<Entry<String, Integer>>() {

			@Override
			public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
				return o1.getValue() - o2.getValue();
			}

		});

		return list;
	}

	public String getRankPlayer(MiniGameType type, int n) {
		Map<String, Integer> rankData = getMiniGameRankData(type);
		int index = 0;
		for (Entry<String, Integer> entry : getDescendingSortedMapEntrys(rankData)) {
			if (index == n - 1) {
				return entry.getKey();
			}
			index++;
		}

		return null;
	}

	public int getScore(MiniGameType type, String name) {
		Map<String, Integer> rankData = getMiniGameRankData(type);
		if (isExist(type, name)) {
			return rankData.get(name);
		} else {
			BroadcastTool.reportBug("player rank data is not exist (or minigame basic data)");
			return -99999999;
		}
	}

	public int getRank(MiniGameType type, Player p) {
		// TODO: 구현하기
		return -1;
	}

	public void printAllRank(MiniGameType type, Player p) {
		/*
		 * 전체를 보여주면 너무 많기 때문에 4분위수 값 경계점수만 보여줌
		 */
		BroadcastTool.sendMessage(p, ChatColor.BOLD + "[랭크]");

		for (int i = 1; i <= 4; i++) {
			// 한단계 낮은 분위수
			String previousName = getQuartilePlayerName(type, i - 1);
			String previousScore = "" + ChatColor.RED + ChatColor.BOLD + getScore(type, previousName) + ChatColor.WHITE;

			// 현재 분위수
			String name = getQuartilePlayerName(type, i);
			String score = "" + ChatColor.RED + ChatColor.BOLD + getScore(type, name) + ChatColor.WHITE;

			// print
			BroadcastTool.sendMessage(p,
					String.format("상위[%d퍼]: %s(%s) ~ %s(%s)", (100 -(i * 25)), previousScore, previousName, score, name));
		}
	}

	public boolean isExist(MiniGameType type, String name) {
		Map<String, Integer> rankData = getMiniGameRankData(type);
		if (name == null) {
			return false;
		}
		// CooperativeMiniGame일때
		if (name.contains(",")) {
			String[] names = name.split(",");

			out: for (String keyName : rankData.keySet()) {
				for (String eachName : names) {
					if (!keyName.contains(eachName)) {
						continue out;
					}
				}
				// eachName이 다 들어있을때 같은 key로 판별
				return true;
			}

			return false;
		}
		// SoloMiniGame일때
		else // if (rankData.containsKey(name)) {
		{
			return rankData.containsKey(name);
		}

	}

	public void updatePlayerRankData(MiniGameType type, String name, int score) {
		/*
		 * 이번게임에 달성한 스코어가 더 크면 업데이트하기
		 * 
		 * name에는 1명이 올 수도 있고, 2명 이상(콤마로 구분)이 올 수도 있음
		 */
		Map<String, Integer> rankData = getMiniGameRankData(type);
		if (isNewRecordScore(type, name, score)) {
			rankData.put(name, score);
		}
	}

	public boolean isNewRecordScore(MiniGameType type, String name, int score) {
		if (isExist(type, name)) {
			String nameKey = getKeyWithName(type, name);
			int previousScore = getScore(type, nameKey);

			// 이번게임에 달성한 스코어가 더 크면 true
			return score > previousScore;
		} else {
			// 처음 도전한것이므로 newRecordScore 임
			return true;
		}

	}

	private String getKeyWithName(MiniGameType type, String name) {
		/*
		 * name이 여러개 나열되있을떄 저장된 나열 key를 정확히 반환하는 메소드
		 */
		Map<String, Integer> rankData = getMiniGameRankData(type);
		if (name.contains(",")) {
			String[] names = name.split(",");

			out: for (String keyName : rankData.keySet()) {
				for (String eachName : names) {
					if (!keyName.contains(eachName)) {
						continue out;
					}
				}
				// eachName이 다 들어있을때 같은 key로 판별
				return keyName;
			}
			// 모든 name이 들어간 key가 없을 때
			return null;
		} else {
			return name;
		}
	}

	public String getQuartilePlayerName(MiniGameType type, int n) {
		/*
		 * rankData에서 n분위수의 플레이어를 구하는 메소드
		 * 
		 * n분위수 값 score를 가진 플레이어 반환
		 * 
		 * 분위수 순서: 오름차순 (예)1분위부터 4분위까지 올라갈 수 록 상위 점수
		 */

		// 오름차순 정렬 리스트
		Map<String, Integer> rankData = getMiniGameRankData(type);
		List<Entry<String, Integer>> sortedData = getAscendingSortedMapEntrys(rankData);
		int totalCount = sortedData.size();

		// 아무것도 없을때 기본 기준값은 0
		if (totalCount == 0) {
			return null;
		}

		/*
		 * 20 5 / 5 / 5 / 5 5, 10, 15
		 * 
		 * n분위수: (totalCount / 4) * n
		 */

		// 반올림해서 index구함 (배열은 0부터 시작해서 -1 해 줌)
		int NQuartileIndex = (int) Math.round((totalCount / (double) 4) * n) - 1;
		if (NQuartileIndex < 0) {
			NQuartileIndex = 0;
		}

		return sortedData.get(NQuartileIndex).getKey();
	}

	public String getFirstPlacePlayer(MiniGameType type) {
		Map<String, Integer> rankData = getMiniGameRankData(type);
		List<Entry<String, Integer>> sortedData = getDescendingSortedMapEntrys(rankData);
		if (sortedData.size() > 0) {
			return sortedData.get(0).getKey();
		} else {
			return null;
		}
	}

	public String getLastPlacePlayer(MiniGameType type) {
		Map<String, Integer> rankData = getMiniGameRankData(type);
		List<Entry<String, Integer>> sortedData = getAscendingSortedMapEntrys(rankData);
		if (sortedData.size() > 0) {
			return sortedData.get(0).getKey();
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void installData(Object obj) {
		this.miniGameRankData = (Map<MiniGameType, Map<String, Integer>>) obj;

		// 새로 추가된 미니게임 있을시 추가
		this.registerMiniGameToRankManager();
	}

	@Override
	public Object getData() {
		return this.miniGameRankData;
	}

	@Override
	public String getDataMemberName() {
		return "miniGameRankData";
	}

}