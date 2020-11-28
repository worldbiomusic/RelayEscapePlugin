package com.wbm.plugin.util.minigame;

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

public abstract class MiniGame
{
	/*
	 * 모든 미니게임은 이 클래스를 상속받아서 만들어져야 함
	 * 
	 * 튜닝할 수 있는 것
	 * -게임 아이템 추가: runTaskAfterStartGame() 메소드 오버라이딩하면 시작시 실행해줌
	 */
	private Player player;
	private boolean inUse;
	private int score;
	private int timeLimit;
	private MiniGameType gameType;
	
	private int waitingTime;
	
	public MiniGame(MiniGameType gameType, int timeLimit) {
		this.player = null;
		this.inUse = false;
		this.score = 0;
		this.timeLimit = timeLimit;
		this.gameType = gameType;
		this.waitingTime = 5;
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
		
		
	}
	
	public void exitGame() {
		/*
		 * player 퇴장 (lobby로)
		 * inventory 초기화
		 * score 공개
		 * 순위 공개
		 * 초기화
		 */
		
		// player lobby로 tp
		TeleportTool.tp(this.player, SpawnLocationTool.lobby);
		
		
		// inventory 초기화
		InventoryTool.clearPlayerInv(this.player);
		
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
























