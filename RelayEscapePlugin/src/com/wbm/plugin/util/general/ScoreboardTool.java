package com.wbm.plugin.util.general;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class ScoreboardTool
{
	private static Scoreboard board;
	private static Map<DisplaySlot, Objective> objectives;
	private static Map<DisplaySlot, Map<String, Score>> scores;
	
	
	public ScoreboardTool() {
		board = Bukkit.getScoreboardManager().getNewScoreboard();
		objectives = new HashMap<>();
		scores = new HashMap<>();
	}
	
	public static boolean addObjective(String name, String criteria, DisplaySlot slot, String displayName) {
		// 같은 displayslot 중복 금지
		if(!objectives.containsKey(slot)) {
			Objective obj = board.registerNewObjective(name, criteria);
			obj.setDisplaySlot(slot);
			obj.setDisplayName(displayName);
			// put to map
			objectives.put(slot, obj);
			
			// init scores List
			scores.put(slot, new HashMap<>());
			return true;
		}
		
		return false;
	}
	
	public static boolean initScore(DisplaySlot slot, String name, String displayName, int priorityScore) {
		if(objectives.containsKey(slot)) {
			Objective obj = objectives.get(slot);
			Score score = obj.getScore(displayName);
			score.setScore(priorityScore);
			
			scores.get(slot).put(name, score);
			
			return true;
		}
		
		return false;
	}
	
//	public static void changeScoreDisplaynameToAll(DisplaySlot slot, String name, String displayName) {
//		if(objectives.containsKey(slot)) {
//			Objective obj = objectives.get(slot).
//			Score score = scores.get(slot).get(name);
//			
//		}
//	}
}

























