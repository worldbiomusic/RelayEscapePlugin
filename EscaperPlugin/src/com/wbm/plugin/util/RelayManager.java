package com.wbm.plugin.util;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import com.wbm.plugin.Main;
import com.wbm.plugin.data.PlayerData;

public class RelayManager
{
	// 게임의 흐름 (시간, 유저)를 관리하는 클래스
	
	// TODO: enum RelayTime만들기 
	final int waitForStartTime = 10;
	final int makingTime = 10;
	final int testTime = 10;
	final int challengeTime = 30;
	
	PlayerDataManager pDataManager;
	
	Player maker;
	PlayerData makerPData;
	
	public RelayManager(PlayerDataManager pDataManager) {
		this.pDataManager = pDataManager;
	}
	
	public void readyForNewRelay() {
		this.makerPData = this.pDataManager.getMakerPlayerData();
		this.maker = Bukkit.getPlayer(this.makerPData.getUUID());
		this.maker.sendMessage("making time starts in 30 sec.");
		
		Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable()
		{
			
			@Override
			public void run()
			{
				startMaking();
			}
		}, 20 * this.waitForStartTime);
	}
	
	public void startMaking() {
		this.maker.sendMessage("making time!");
		// 역할 변경
		this.pDataManager.changePlayerRole(this.makerPData.getUUID(), Role.MAKER);
		
		
		Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable()
		{
			
			@Override
			public void run()
			{
				startTest();
			}
		}, 20 * this.makingTime);
	}
	
	public void startTest() {
		this.maker.sendMessage("test time!");
		this.pDataManager.changePlayerRole(this.makerPData.getUUID(), Role.TESTER);
		
		Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable()
		{
			
			@Override
			public void run()
			{	
				startView();
			}
		}, 20 * this.testTime);
	}
	
	public void startView() {
		this.maker.sendMessage("view time!");
		this.pDataManager.changePlayerRole(this.makerPData.getUUID(), Role.VIEWER);
		
		
	}
}
