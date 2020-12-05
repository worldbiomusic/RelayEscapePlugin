package com.wbm.plugin.util.minigame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.entity.Player;

import com.wbm.plugin.util.general.BroadcastTool;

public class MiniGameRankManager {
    /*
     * 이 클래스는 static 용
     */
    
 // <name, score>: score기준 내림차순 정렬
    public static List<Entry<String, Integer>> getDescendingSortedMapEntrys(Map<String, Integer> rankData) {
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
    public static List<Entry<String, Integer>> getAscendingSortedMapEntrys(Map<String, Integer> rankData) {
	List<Entry<String, Integer>> list = new ArrayList<>(rankData.entrySet());

	Collections.sort(list, new Comparator<Entry<String, Integer>>() {

	    @Override
	    public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
		return o1.getValue() - o2.getValue();
	    }

	});

	return list;
    }

    public static String getRankPlayer(Map<String, Integer> rankData, int n) {
	int index = 0;
	for (Entry<String, Integer> entry : getDescendingSortedMapEntrys(rankData)) {
	    if (index == n - 1) {
		return entry.getKey();
	    }
	    index++;
	}

	return null;
    }

    public static int getScore(Map<String, Integer> rankData, String name) {
	if (isExist(rankData, name)) {
	    return rankData.get(name);
	} else {
	    BroadcastTool.reportBug("player rank data is not exist");
	    return -99999999;
	}
    }

    public static int getRank(Map<String, Integer> rankData, Player p) {
	// TODO: 구현하기
	return -1;
    }

    public static void printAllRank(Map<String, Integer> rankData, Player p) {
	/*
	 * 전체를 보여주면 너무 많기 때문에 4분위수 값 경계점수만 보여줌
	 */
	BroadcastTool.sendMessage(p, "==========All Rank==========");
	BroadcastTool.sendMessage(p, "[Quantile]");
	
	for (int i = 1; i <= 4; i++) {
	    // 한단계 낮은 분위수
	    String previousName = getQuartilePlayerName(rankData, i); 
	    int previousScore = getScore(rankData, previousName);
	    
	    // 현재 분위수
	    String name = getQuartilePlayerName(rankData, i);
	    int score = getScore(rankData, name);
	    
	    // print
	    BroadcastTool.sendMessage(p, String.format("Quartile[%d]: %d(%s) ~ %d(%s)", 
		    i, previousScore, previousName, score, name));
	}
    }

    public static boolean isExist(Map<String, Integer> rankData, String name) {
	return rankData.containsKey(name);
    }

    public static void updatePlayerRankData(Map<String, Integer> rankData, Player p, int score) {
	/*
	 * 이번게임에 달성한 스코어가 더 크면 업데이트하기
	 */
	if (isNewRecordScore(rankData, p, score)) {
	    rankData.put(p.getName(), score);
	}
    }

    public static boolean isNewRecordScore(Map<String, Integer> rankData, Player p, int score) {
	if (isExist(rankData, p.getName())) {
	    int previousScore = getScore(rankData, p.getName());

	    // 이번게임에 달성한 스코어가 더 크면 true
	    return score > previousScore;
	} else {
	    // 처음 도전한것이므로 newRecordScore 임
	    return true;
	}

    }
    
    public static String getQuartilePlayerName(Map<String, Integer> rankData, int n) {
	/* rankData에서 n분위수의 플레이어를 구하는 메소드
	 * n분위수 값 score를 가진 플레이어 반환 
	 * 
	 * 분위수 차순 순서: 오름차순 (예)4분위는 1등 점수
	 * => n값이 클수록 상위 점수
	*/
	
	// 오름차순 정렬 리스트
	List<Entry<String, Integer>> sortedData = getAscendingSortedMapEntrys(rankData);
	int totalCount = sortedData.size();
	
	// 아무것도 없을때 기본 기준값은 0
	if(totalCount == 0) {
	    return null;
	}
	
	/*
    	20
    	5 / 5 /  5 / 5
    	5, 10, 15
    	
    	n분위수: (totalCount / 4) * n
	*/
	
	// 반올림해서 index구함
	int NQuartileIndex = (int)Math.round((totalCount / (double)4) * n) - 1;
	if(NQuartileIndex < 0) {
	    NQuartileIndex = 0;
	}
	
	return sortedData.get(NQuartileIndex).getKey();
    }
    
    public String getFirstPlacePlayer(Map<String, Integer> rankData) {
	List<Entry<String, Integer>> sortedData = getDescendingSortedMapEntrys(rankData);
	if(sortedData.size() > 0) {
	    return sortedData.get(0).getKey();
	} else {
	    return null;
	}
    }
    
    public String getLastPlacePlayer(Map<String, Integer> rankData) {
	List<Entry<String, Integer>> sortedData = getAscendingSortedMapEntrys(rankData);
	if(sortedData.size() > 0) {
	    return sortedData.get(0).getKey();
	} else {
	    return null;
	}
    }

//    private Player getHighScorePlayer(Player target, Player other) {
//	// 두 player의 score를 비교하는것
//	if (this.isExist(target) && this.isExist(other)) {
//	    int diff = this.getScore(target) - this.getScore(other);
//	    if (diff == 0) {
//		// 같을때 null반환
//		return null;
//	    } else if (diff > 0) {
//		return target;
//	    } else { // diff < 0
//		return other;
//	    }
//	} else {
//	    BroadcastTool.reportBug("cannot compare not exist player");
//	    return null;
//	}
//    }
}









































//public class MiniGameRankManager {
//    /*
//     * 이 클래스는 instance 용
//     */
//    
//    private Map<String, Integer> rankData;
//    public MiniGameRankManager(Map<String, Integer> rankData) {
//	this.rankData = rankData;
//    }
//    
// // <name, score>: score기준 내림차순 정렬
//    public List<Entry<String, Integer>> getSortedMapEntry() {
//	List<Entry<String, Integer>> list = new ArrayList<>(this.rankData.entrySet());
//
//	Collections.sort(list, new Comparator<Entry<String, Integer>>() {
//
//	    @Override
//	    public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
//		return o2.getValue() - o1.getValue();
//	    }
//
//	});
//
//	return list;
//    }
//
//    public String getRankPlayer(int n) {
//	int index = 0;
//	for (Entry<String, Integer> entry : this.getSortedMapEntry()) {
//	    if (index == n - 1) {
//		return entry.getKey();
//	    }
//	    index++;
//	}
//
//	return null;
//    }
//
//    public int getScore(Player p) {
//	if (this.isExist(p)) {
//	    return this.rankData.get(p.getName());
//	} else {
//	    BroadcastTool.reportBug("player rank data is not exist");
//	    return -99999999;
//	}
//    }
//
//    public int getRank(Player p) {
//	// TODO: 구현하기
//	return -1;
//    }
//
//    public int getQuartileScore(int n) {
//	// TODO: 구현하기
//	return -1;
//    }
//
//    public void printAllRank(Player p) {
//	BroadcastTool.sendMessage(p, "==========All Rank==========");
//	for (Entry<String, Integer> entry : this.getSortedMapEntry()) {
//	    BroadcastTool.sendMessage(p, entry.getKey() + ": " + entry.getValue());
//	}
//    }
//
//    public boolean isExist(Player p) {
//	return this.rankData.containsKey(p.getName());
//    }
//
//    public void updatePlayerRankData(Player p, int score) {
//	/*
//	 * 이번게임에 달성한 스코어가 더 크면 업데이트하기
//	 */
//	if (this.isNewRecordScore(p, score)) {
//	    this.rankData.put(p.getName(), score);
//	}
//    }
//
//    public boolean isNewRecordScore(Player p, int score) {
//	if (this.isExist(p)) {
//	    int previousScore = this.getScore(p);
//
//	    // 이번게임에 달성한 스코어가 더 크면 true
//	    return score > previousScore;
//	} else {
//	    // 처음 도전한것이므로 newRecordScore 임
//	    return true;
//	}
//
//    }
//
////    private Player getHighScorePlayer(Player target, Player other) {
////	// 두 player의 score를 비교하는것
////	if (this.isExist(target) && this.isExist(other)) {
////	    int diff = this.getScore(target) - this.getScore(other);
////	    if (diff == 0) {
////		// 같을때 null반환
////		return null;
////	    } else if (diff > 0) {
////		return target;
////	    } else { // diff < 0
////		return other;
////	    }
////	} else {
////	    BroadcastTool.reportBug("cannot compare not exist player");
////	    return null;
////	}
////    }
//}
