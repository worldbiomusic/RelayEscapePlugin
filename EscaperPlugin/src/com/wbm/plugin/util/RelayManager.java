package com.wbm.plugin.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import com.wbm.plugin.Main;
import com.wbm.plugin.util.enums.RelayTime;
import com.wbm.plugin.util.enums.Role;
import com.wbm.plugin.util.general.BroadcastTool;


/* TODO: 기본적인 역할인 Maker와 Challenger가 Role의 역할들과 겹처서혼란이 있기 때문에
 * 이름을 다른것으로 바꿀것임! (ex. Maker-> Builder / Challenger-> Breaker)
 * 
 * 중요) 기본적인 maker는 PlayerDataManager에 변수로 등록해놓음
 * 
 * [시간에 따른 Maker와 Challenger의 역할 변화]
 * Role: Maker, Tester, Challenger, Viewer, Waiter
 * 
 * Role\RelayTime	Making	Testing	Challenging	Waiting	
 * -Maker			Maker	Tester	Viewer		Waiter
 * -Challenger		Waiter	Waiter	Challenger	Waiter
 */



public class RelayManager
{
	// 게임의 흐름 (시간, 유저)를 관리하는 클래스
	// 중요) startWaiting(), startMaking(), startChallenging(), startWaiting()
	// 메소드를 절대 직접 호출하지 말기 (시간이 다 되어서 자동으로 넘어갈 때만 사용해야 함)
	// 대신 stopTaskAndStartNextTime() 사용하기! (task멈추는 코드 있기때문)
	
	PlayerDataManager pDataManager;
	
	Player maker;
	
	// TODO: RelayTime이름보단 다른것찾아보기 예> RelayTurn ??
	RelayTime currentTime;

	BukkitTask currentCountDownTask;
	
	public RelayManager(PlayerDataManager pDataManager) {
		this.pDataManager = pDataManager;
		this.currentTime = RelayTime.CHALLENGING;
		
		System.out.println(ChatColor.RED + "RelayTime.WAITING: " + RelayTime.WAITING);
		System.out.println(ChatColor.RED + "isSame TESTING, WAITING: " + RelayTime.WAITING);
	}
	
	
	private void startWaiting() {
		// RelayTime 관리
		this.currentTime = RelayTime.WAITING;
		
		// maker 관리
		this.maker = this.pDataManager.getMaker();
		this.maker.sendMessage("you are now Maker");
		this.pDataManager.changePlayerRole(this.maker.getUniqueId(), Role.WAITER);
		
		// maker제외한 challenger 관리 
		for(Player p : this.getChallengers()) {
			this.pDataManager.changePlayerRole(p.getUniqueId(), Role.WAITER);
		}
		
		// message 관리
		BroadcastTool.sendMessageToEveryone(
				"waitingTime: makingTime starts in " + RelayTime.WAITING.getAmount() + " sec");
		
		
		// MakingTime 카운트다운
		this.currentCountDownTask = Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable()
		{
			
			@Override
			public void run()
			{
				startMaking();
			}
		}, 20 * RelayTime.WAITING.getAmount());
	}
	
	private void startMaking() {
		// RelayTime 관리
		this.currentTime = RelayTime.MAKING;
				
		// maker 관리
		this.pDataManager.changePlayerRole(this.maker.getUniqueId(), Role.MAKER);
		
		// maker제외한 challenger 관리 
		for(Player p : this.getChallengers()) {
			this.pDataManager.changePlayerRole(p.getUniqueId(), Role.WAITER);
		}
		
		// message 관리
		BroadcastTool.sendMessageToEveryone(
				"makingTime: testingTime starts in " + RelayTime.MAKING.getAmount() + " sec");
		
		
		// TestingTime 카운트다운
		this.currentCountDownTask = Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable()
		{
			
			@Override
			public void run()
			{
				startTesting();
			}
		}, 20 * RelayTime.MAKING.getAmount());
	}
	
	private void startTesting() {
		// RelayTime 관리
		this.currentTime = RelayTime.TESTING;
				
		// maker 관리
		this.pDataManager.changePlayerRole(this.maker.getUniqueId(), Role.TESTER);
		
		// maker제외한 challenger 관리 
		for(Player p : this.getChallengers()) {
			this.pDataManager.changePlayerRole(p.getUniqueId(), Role.WAITER);
		}
		
		// message 관리
		BroadcastTool.sendMessageToEveryone(
				"testTime: challengingTime starts in " + RelayTime.TESTING.getAmount() + " sec");
		
				
				
		// ChallengingTime 카운트다운
		this.currentCountDownTask = Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable()
		{
			
			@Override
			public void run()
			{	
				startChallenging();
			}
		}, 20 * RelayTime.TESTING.getAmount());
	}
	
	private void startChallenging() {
		// RelayTime 관리
		this.currentTime = RelayTime.CHALLENGING;
				
		// maker 관리
		this.pDataManager.changePlayerRole(this.maker.getUniqueId(), Role.VIEWER);
		
		// maker제외한 challenger 관리 
		for(Player p : this.getChallengers()) {
			this.pDataManager.changePlayerRole(p.getUniqueId(), Role.CHALLENGER);
		}
		
		// message 관리
		BroadcastTool.sendMessageToEveryone(
				"challengingTime: waitingTime starts in " + RelayTime.CHALLENGING.getAmount() + " sec");
		
		
		
		// WaitingTime 카운트다운
		this.currentCountDownTask = Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable()
		{
			
			@Override
			public void run()
			{	
				startWaiting();
			}
		}, 20 * RelayTime.CHALLENGING.getAmount());
		
	}

	public List<Player> getChallengers() {
		List<Player> challengers = new ArrayList<Player>(Bukkit.getOnlinePlayers());
		for(Player p : challengers) {
			if(p.getUniqueId() == this.maker.getUniqueId()) {
				challengers.remove(p);
				break;
			}
		}
		
		return challengers;
	}
	
	public void stopCurrentTaskAndStartNextTime() {
		if(this.currentCountDownTask != null) {
			this.currentCountDownTask.cancel();
		}
		
		RelayTime t = this.currentTime;
		
		if(t == RelayTime.WAITING) {
			this.startMaking();
		} else if(t == RelayTime.MAKING) {
			this.startTesting();
		} else if(t == RelayTime.TESTING) {
			this.startChallenging();
		} else if(t == RelayTime.CHALLENGING) {
			this.startWaiting();
		}
	}
	
	
	
	
	
	
	
	public RelayTime getCurrentTime()
	{
		return currentTime;
	}


	public void setCurrentTime(RelayTime currentTime)
	{
		this.currentTime=currentTime;
	}


	public BukkitTask getCurrentCountDownTask()
	{
		return currentCountDownTask;
	}


	public void setCurrentCountDownTask(BukkitTask currentCountDownTask)
	{
		this.currentCountDownTask=currentCountDownTask;
	}
}
























