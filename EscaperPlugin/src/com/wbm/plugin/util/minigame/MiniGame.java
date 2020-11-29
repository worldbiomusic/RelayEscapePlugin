package com.wbm.plugin.util.minigame;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import com.wbm.plugin.Main;
import com.wbm.plugin.data.MiniGameLocation;
import com.wbm.plugin.util.enums.MiniGameType;
import com.wbm.plugin.util.general.BroadcastTool;
import com.wbm.plugin.util.general.InventoryTool;
import com.wbm.plugin.util.general.SpawnLocationTool;
import com.wbm.plugin.util.general.TeleportTool;

import net.md_5.bungee.api.ChatColor;

public abstract class MiniGame implements Serializable
{
	private static final long serialVersionUID=1L;
	/*
	 * 모든 미니게임은 이 클래스를 상속받아서 만들어져야 함
	 * 
	 * 튜닝할 수 있는 것
	 * -게임 아이템 추가: runTaskAfterStartGame() 메소드 오버라이딩하면 시작시 실행해줌
	 * 
	 * [주의]
	 * timeLimit, gameType, rankData는 파일로 저장되야되서 transient 선언안함
	 * 
	 * [미니게임 추가하는법]
	 * 1.MiniGame클래스를 상속하는 클래스를 하나 만들고 필요한 메소드를 다 오바라이딩해서 구현한다
	 * 2.MiniGameManager의 생성자에서 "allGame.add(new FindTheRed())" 처럼 등록한다
	 * (이유: 처음에 미니게임 데이터가 없는것을 초기화해서 파일저장하려고, 나중에 파일에 저장되면 데이터 불러올때 저장된것으로 대체가 됨 = 처음에 한번 초기화를 위해서 필요한 코드)
	 */
	transient private Player player;
	transient private boolean inUse;
	transient private int score;
	private int timeLimit;
	private MiniGameType gameType;
	
	// 파일에 저장시킬 필요 없는데 값은 기본값인 0이 아니어서 여기서 static 할당 
	transient private static int waitingTime = 5;
	
	// 각 미니게임의 랭크데이터 관리 변수
	private Map<String, Integer> rankData;
	
	public MiniGame(MiniGameType gameType, int timeLimit) {
		this.player = null;
		this.inUse = false;
		this.score = 0;
		this.timeLimit = timeLimit;
		this.gameType = gameType;
		
		this.rankData = new HashMap<>();
	}
	
	public void initGame() {
		this.player = null;
		this.inUse = false;
		this.score = 0;
	}
	
	public void startGame(Player p) {
		// setup variables
		this.initGame();
		this.player = p;
		
		// player에게 정보 전달
		this.printGameTutorial();
		BroadcastTool.sendMessage(this.player, this.gameType.name() + " game starts in "+this.waitingTime+ " sec");
		
		// inUse = true: this.waitingTime 초 후 실행
		Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable()
		{
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				inUse = true;
				BroadcastTool.sendTitle(player, "START", "");
				
				// start game 후에 실행할 작업 
				runTaskAfterStartGame();
			}
		}, 20 * this.waitingTime);
		
		
		
		// exitGame(): this.waitingTime + this.timeLimit 초 후 실행
		Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable()
		{
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				exitGame();
			}
		}, 20 * (this.waitingTime + this.timeLimit));
		
		// print all rank
		this.printAllRank();
	}
	
	public void exitGame() {
		/*
		 * player 퇴장 (lobby로)
		 * inventory 초기화
		 * score rank 처리
		 * score 공개
		 * 순위 공개
		 * 초기화
		 */
		
		// player lobby로 tp
		TeleportTool.tp(this.player, SpawnLocationTool.LOBBY);
		
		
		// inventory 초기화
		InventoryTool.clearPlayerInv(this.player);
		
		// score rank 처리
		this.updatePlayerRankData();
		
		// print all rank data
		this.printAllRank();
		
		// score 공개
		BroadcastTool.sendMessage(this.player, "=================================");
		BroadcastTool.sendMessage(this.player, "=========== " + ChatColor.RED + ChatColor.BOLD + "Game End" + ChatColor.WHITE + " ===========");
		BroadcastTool.sendMessage(this.player, "=================================");
		BroadcastTool.sendMessage(this.player, "Your score: " + this.score);
		
		// TODO: 순위 공개
		
		
		// 초기화
		this.initGame();
	}
	
	/*
	 * 이 메소드는 미니게임에서 플레이어들이 발생한 이벤트를 각 게임에서 처리해주는 범용 메소드
	 * 예) 
	 * if(event instanceof BlockBreakEvent)
		{
			BlockBreakEvent e = (BlockBreakEvent) event;
			// 생략
		}
	 */
	public abstract void processEvent(Event event);
	
	// tutorial strings
	public abstract String[] getGameTutorialStrings();
	
	
	public void printGameTutorial() {
		/*
		 * 기본적으로 출력되는 정보
		 * -game name
		 * -time limit
		 * -waiting time
		 * 
		 * getGameTutorialStrings()에 추가해야 하는 정보
		 * -game rule
		 */
		BroadcastTool.sendMessage(this.player, "=================================");
		BroadcastTool.sendMessage(this.player, "" + ChatColor.RED + ChatColor.BOLD + this.gameType.name() + ChatColor.WHITE);
		BroadcastTool.sendMessage(this.player, "=================================");
		BroadcastTool.sendMessage(this.player, "Time Limit: " + this.timeLimit);
		
		// print rule
		BroadcastTool.sendMessage(this.player, "");
		BroadcastTool.sendMessage(this.player, ChatColor.BOLD + "[Rule]");
		for(String msg : this.getGameTutorialStrings()) {
			BroadcastTool.sendMessage(this.player, msg);
		}
		
		BroadcastTool.sendMessage(this.player, "");
		BroadcastTool.sendMessage(this.player, "game starts in " + this.waitingTime+ " sec");
	}
	
	public void runTaskAfterStartGame() {
	}
	
	public int getGameBlockCount() {
		return MiniGameLocation.getGameBlockCount(this.gameType);
	}
	
	
	
	
	
	// RANK SYSTEM METHOD ============================================
	
	
	// <name, score>: score기준 내림차순 정렬
	public List<Entry<String, Integer>> getSortedMapEntry() {
		List<Entry<String, Integer>> list = new ArrayList<>(this.rankData.entrySet());
		
		Collections.sort(list, new Comparator<Entry<String, Integer>>() {

			@Override
			public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2)
			{
				return o2.getValue() - o1.getValue();
			}
			
		});
		
		return list;
	}
	
	public String getRankPlayer(int n) {
		int index = 0;
		for(Entry<String, Integer> entry : this.getSortedMapEntry()) {
			if(index == n-1) {
				return entry.getKey();
			}
			index++;
		}
		
		return null;
	}
	public int getScore(Player p) {
		if(this.isExist(p)) {
			return this.rankData.get(p.getName());
		} else {
			BroadcastTool.reportBug("player rank data is not exist");
			return -99999999;
		}
	}
	
	public int getRank(Player p) {
		// TODO: 구현하기
		return -1;
	}
	
	public int getQuartileScore(int n) {
		// TODO: 구현하기
		return -1;
	}
	
	public void printAllRank() {
		BroadcastTool.sendMessage(this.player, "==========All Rank==========");
		for(Entry<String, Integer> entry : this.getSortedMapEntry()) {
			BroadcastTool.sendMessage(this.player, entry.getKey() + ": " + entry.getValue());
		}
	}
	
	
	public boolean isExist(Player p) {
		return this.rankData.containsKey(p.getName());
	}
	
	public void updatePlayerRankData() {
		/*
		 * 이번게임에 달성한 스코어가 더 크면 업데이트하기
		 */
		if(this.isNewRecordScore()) {
			this.rankData.put(this.player.getName(),  this.score);
		}
	}
	
	public boolean isNewRecordScore() {
		if(this.isExist(this.player)) {
			int previousScore = this.getScore(this.player);
			
			//이번게임에 달성한 스코어가 더 크면 true
			return this.score > previousScore;
		} else {
			// 처음 도전한것이므로 newRecordScore 임
			return true;
		}
		
	}
	
	private Player getHighScorePlayer(Player target, Player other) {
		// 두 player의 score를 비교하는것
		if(this.isExist(target) && this.isExist(other)) {
			int diff = this.getScore(target) - this.getScore(other);  
			if(diff == 0) { 
				// 같을때 null반환
				return null;
			}else if(diff > 0) {
				return target;
			} else { // diff < 0
				return other;
			}
		} else {
			BroadcastTool.reportBug("cannot compare not exist player");
			return null;
		}
	}
	
	
	
	
	
	
	// GETTER, SETTER =============================================

	public Player getPlayer()
	{
		return player;
	}

	public void setPlayer(Player player)
	{
		this.player=player;
	}

	public boolean isInUse()
	{
		return inUse;
	}

	public void setInUse(boolean inUse)
	{
		this.inUse=inUse;
	}
	
	public void plusScore(int amount) {
		this.score += amount;
	}
	
	public void minusScore(int amount) {
		this.score -= amount;
	}

	public int getScore()
	{
		return score;
	}

	public void setScore(int score)
	{
		this.score=score;
	}
	
	public int getTimeLimit()
	{
		return timeLimit;
	}

	public void setTimeLimit(int timeLimit)
	{
		this.timeLimit=timeLimit;
	}

	public MiniGameType getGameType()
	{
		return gameType;
	}

	public void setGameType(MiniGameType gameType)
	{
		this.gameType=gameType;
	}

	@Override
	public String toString()
	{
		return "MiniGame "
				+ "\nplayer="+player+
				", \ninUse="+inUse+
				", \nscore="+score+
				", \ntimeLimit="+timeLimit+
				", \ngameType="+gameType+"]";
	}
	
	
}
























