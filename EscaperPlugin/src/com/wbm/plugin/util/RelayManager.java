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

/* TODO: RelayTime의 Making -> Building, Challenging -> Finding으로 변경
 * TODO: ROle의 Maker-> Builder, Challenger -> Finder로 변경
 * 
 * 중요) 기본적인 maker는 PlayerDataManager에 변수로 등록해놓음
 * 
 * [시간에 따른 Maker와 Challenger의 역할 변화]
 * Role: Maker, Tester, Challenger, Viewer, Waiter
 * 
 * Role\RelayTime	Waiting		Making		Testing		Challenging		
 * -Maker			Waiter		Maker		Tester		Viewer		
 * -Challenger		Waiter		Waiter		Waiter		Challenger	
 */

public class RelayManager
{
	// 게임의 흐름 (시간, 유저)를 관리하는 클래스
	// 중요) startWaiting(), startMaking(), startChallenging(), startWaiting()
	// 메소드를 절대 직접 호출하지 말기 (시간이 다 되어서 자동으로 넘어갈 때만 사용해야 함)
	// 대신 stopTaskAndStartNextTime() or stopCurrentTimeAndStartAnotherTime() 사용하기!
	// (넘어가기전에 task멈춰야하기 때문)

	PlayerDataManager pDataManager;
	RoomManager roomManager;

	// TODO: RelayTime이름보단 다른것찾아보기 예> RelayTurn ??
	RelayTime currentTime;

	BukkitTask currentCountDownTask;

	private boolean corePlaced;

	public RelayManager(PlayerDataManager pDataManager, RoomManager roomManager)
	{
		this.pDataManager=pDataManager;
		this.roomManager=roomManager;
		this.currentTime=RelayTime.CHALLENGING;
		this.corePlaced=false;
	}

	// Waiting이 시작하려면 무조건 maker가 등록되어 있어야 함!
	private void startWaiting()
	{
		// RelayTime 관리
		this.currentTime=RelayTime.WAITING;

		// maker 관리
		if(this.getMaker()==null)
		{
			BroadcastTool.printConsoleMessage(ChatColor.RED+"[Bug] No Maker in WaitingTime!!!!");
		}
		this.getMaker().sendMessage("you are now Maker");
		this.pDataManager.changePlayerRole(this.getMaker().getUniqueId(), Role.WAITER);

		// maker제외한 challenger 관리
		for(Player p : this.getChallengers())
		{
			this.pDataManager.changePlayerRole(p.getUniqueId(), Role.WAITER);
		}

		// message 관리
		BroadcastTool.sendMessageToEveryone("waitingTime: makingTime starts in "+RelayTime.WAITING.getAmount()+" sec");

		// MakingTime 카운트다운
		this.currentCountDownTask=Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable()
		{

			@Override
			public void run()
			{
//				startMaking();
				startNextTime();
			}
		}, 20*RelayTime.WAITING.getAmount());
	}

	private void startMaking()
	{
		// RelayTime 관리
		this.currentTime=RelayTime.MAKING;

		// maker 관리
		this.pDataManager.changePlayerRole(this.getMaker().getUniqueId(), Role.MAKER);

		// maker제외한 challenger 관리
		for(Player p : this.getChallengers())
		{
			this.pDataManager.changePlayerRole(p.getUniqueId(), Role.WAITER);
		}

		// message 관리
		BroadcastTool.sendMessageToEveryone("makingTime: testingTime starts in "+RelayTime.MAKING.getAmount()+" sec");

		// TestingTime 카운트다운
		this.currentCountDownTask=Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable()
		{

			@Override
			public void run()
			{
				if(!isCorePlaced())
				{
					resetRelay();
				}
				else
				{
					startNextTime();
				}
			}
		}, 20*RelayTime.MAKING.getAmount());
	}

	private void startTesting()
	{
		// RelayTime 관리
		this.currentTime=RelayTime.TESTING;

		// maker 관리
		this.pDataManager.changePlayerRole(this.getMaker().getUniqueId(), Role.TESTER);

		// maker제외한 challenger 관리
		for(Player p : this.getChallengers())
		{
			this.pDataManager.changePlayerRole(p.getUniqueId(), Role.WAITER);
		}

		// message 관리
		BroadcastTool
				.sendMessageToEveryone("testTime: challengingTime starts in "+RelayTime.TESTING.getAmount()+" sec");

		// resetRelay 카운트다운
		this.currentCountDownTask=Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable()
		{
			// TestingTime에서 시간이 다되었다는것은 통과를 못했다는 뜻 -> restRelay
			@Override
			public void run()
			{
				// reset relay
				BroadcastTool.sendMessageToEveryone("Maker couldn't pass the test");
				resetRelay();
			}
		}, 20*RelayTime.TESTING.getAmount());
	}

	private void startChallenging()
	{
		// RelayTime 관리
		this.currentTime=RelayTime.CHALLENGING;

		// maker 관리
		// if문 넣은이유: Maker가 만들고 나갔을때 위해서
		if(this.pDataManager.doesMakerExist())
		{
			this.pDataManager.changePlayerRole(this.getMaker().getUniqueId(), Role.VIEWER);
		}

		// maker제외한 challenger 관리
		for(Player p : this.getChallengers())
		{
			this.pDataManager.changePlayerRole(p.getUniqueId(), Role.CHALLENGER);
		}

		// message 관리 
		BroadcastTool.sendMessageToEveryone(
				"challengingTime: new challengingTime starts in "+RelayTime.CHALLENGING.getAmount()+" sec");

		// main room 저장 (maker가 존재해야지 maker가 만든 room인것을 증명)
		if(this.pDataManager.doesMakerExist())
		{
			this.roomManager.saveRoomData(this.getMaker().getName());
		}

		// resetRelay 카운트다운
		this.currentCountDownTask=Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable()
		{
			// ChallengingTime에서 시간이 다되었다는것은 사람이 없거나 난이도가 어렵다는 뜻 -> resetRelay
			@Override
			public void run()
			{
				// reset relay
				BroadcastTool.sendMessageToEveryone("no one pass the room");
				resetRelay();
			}
		}, 20*RelayTime.CHALLENGING.getAmount());

	}

	public List<Player> getChallengers()
	{
		List<Player> challengers=new ArrayList<Player>(Bukkit.getOnlinePlayers());

		// maker가 없을경우
		if(!this.pDataManager.doesMakerExist())
		{
			return challengers;
		}

		for(Player p : challengers)
		{
			if(p.getUniqueId().equals(this.getMaker().getUniqueId()))
			{
				challengers.remove(p);
				break;
			}
		}

		return challengers;
	}

	// this.pDataManager.getMaker()가 너무 길어서 만든 메소드
	// this.pDataManager.getMaker() <- 여기서만 참조해야 데이터 무결성이 보장됨
	private Player getMaker()
	{
		return this.pDataManager.getMaker();
	}

//	// this.pDataManager.registerMaker(maker);가 너무 길어서 만든 메소드
//	// this.pDataManager.getMaker() <- 여기서만 참조해야 데이터 무결성이 보장됨
//	private void setMaker(Player maker) {
//		this.pDataManager.registerMaker(maker);
//	}

	void stopCurrentTime()
	{
		if(this.currentCountDownTask!=null)
		{
			this.currentCountDownTask.cancel();
		}
	}

	// 일반적으로 자연스러운 Time flow (시간이 다 됬을때 or 조건이 만족되었을때(명령어))
	// 다음Time 조건 검사도 해줌
	public void startNextTime()
	{
		// 먼저 현재 time task 중지
		this.stopCurrentTime();

		RelayTime t=this.currentTime;

		if(t==RelayTime.WAITING)
		{
			this.startMaking();
		}
		else if(t==RelayTime.MAKING)
		{
			this.startTesting();
		}
		else if(t==RelayTime.TESTING)
		{
			// core부수면 바로 시작
			this.startChallenging();
		}
		else if(t==RelayTime.CHALLENGING)
		{
			this.startWaiting();
		}
	}

	// 예외적인 상황이 발생했을 때 사용 (Maker가 방을 중간에 나가거나 or 조건이 불만족되었을때)
	public void startAnotherTime(RelayTime anotherTime)
	{
		// 먼저 현재 time task 중지
		this.stopCurrentTime();

		if(anotherTime==RelayTime.WAITING)
		{
			this.startWaiting();
		}
		else if(anotherTime==RelayTime.MAKING)
		{
			this.startMaking();
		}
		else if(anotherTime==RelayTime.TESTING)
		{
			this.startTesting();
		}
		else if(anotherTime==RelayTime.CHALLENGING)
		{
			this.startChallenging();
		}
	}

	public void resetRelay()
	{
		// resetSettings
		this.resetRelaySetting();

		// reset message
		BroadcastTool.sendMessageToEveryone("relay reset");

		// Room 초기화
		this.roomManager.setMainRoomToBaseRoom();

		// RelayTime set to CHALLENGING
		this.startAnotherTime(RelayTime.CHALLENGING);
	}

	public void resetRelaySetting()
	{
		// corePlaced 초기화
		this.corePlaced=false;

		// PlayerDataManager maker = null 처리
		this.pDataManager.unregisterMaker();
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

	public boolean isCorePlaced()
	{
		return corePlaced;
	}

	public void setCorePlaced(boolean corePlaced)
	{
		this.corePlaced=corePlaced;
	}

}
