package com.wbm.plugin.util.minigame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockEvent;

import com.wbm.plugin.data.MiniGameLocation;
import com.wbm.plugin.util.PlayerDataManager;
import com.wbm.plugin.util.config.DataMember;
import com.wbm.plugin.util.enums.MiniGameType;
import com.wbm.plugin.util.general.BroadcastTool;
import com.wbm.plugin.util.minigame.games.FindTheYellow;
import com.wbm.plugin.util.minigame.games.FindTheRed;
import com.wbm.plugin.util.minigame.games.Painter;

public class MiniGameManager implements DataMember {
    // MiniGame 체크하기 위한 Map (이용중이면 true, 비어있으면 false)
    private Map<MiniGameType, MiniGameInterface> games;

    PlayerDataManager pDataManager;

    public MiniGameManager(PlayerDataManager pDataManager) {
	this.pDataManager = pDataManager;

	this.games = new HashMap<>();

	this.registerGames();
    }
    
    public void registerGames() {
	List<MiniGameInterface> allGame = new ArrayList<>();
	allGame.add(new FindTheRed());
	allGame.add(new Painter());
	allGame.add(new FindTheYellow());

	// 모든 미니게임 games에 등록
	for (MiniGameInterface game : allGame) {
	    if(!this.games.containsKey(game.getGameType())) {
		this.games.put(game.getGameType(), game);
	    }
	}
    }

    public void enterRoom(MiniGameType gameType, Player p) {
	/*
	 * 모든 미니게임]
	 *  
	 * 1.SoloMiniGame( 1인용)
	 * 
	 * 2.MultiCooperativeMiniGame(다인용 협력)
	 * 
	 * 3.MultiBattleMiniGame(다인용 배틀)
	 * 
	 */
	
	MiniGameInterface game = this.games.get(gameType);
	
	// 시작
	game.enterRoom(p, this.pDataManager);
    }

    public void processBlockEvent(BlockEvent e) {
	/*
	 * MiniGame 블럭관련 이벤트에 취할 행동들 (블럭이벤트 위치에 따라 각 미니게임 클래스로 넘겨주기)
	 * 
	 * Event클래스로 하려했는데 Location을 못 가져와서 BlockEvent 이벤트로 함
	 */

	Location loc = e.getBlock().getLocation();

	MiniGameType gameType = MiniGameLocation.getMiniGameWithLocation(loc);

	MiniGameInterface game = this.games.get(gameType);

	// gameRoom블럭이 활성화 됫을시에만 반응
	if (game.isActivated()) {
	    game.processEvent(e);
	}

    }

    public void handlePlayerCurrentMiniGameExiting(Player p) {
	/*
	 * 플레이어가 진행중이던 game이 있을 때, 강제로 멈춰야 할때 사용하는 메소드
	 * 
	 * -MiniGame의 startGame에서 예약해놓은 exitGame() thread 없에야 함
	 * 
	 * [주의]이 메소드를 호출하는 위치는 거의
	 * 앞쪽이여야 함(인벤, 위치 설정이 되기 때문에)
	 */
	if (this.isPlayerPlayingGame(p)) {
	    MiniGameInterface game = this.getPlayingGame(p);
	    game.stopAllTasks();
	    game.exitGame(this.pDataManager);
	    
	    BroadcastTool.debug("handle exit game!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
	}
    }
    


    public boolean isPlayerPlayingGame(Player p) {
	/*
	 * player가 게임 중인지?
	 */
	for (MiniGameInterface game : this.games.values()) {
	    if (game.isPlayerPlayingGame(p)) {
		return true;
	    }
	}
	return false;
    }

    public MiniGameInterface getPlayingGame(Player p) {
	for (MiniGameInterface game : this.games.values()) {
	    if (game.isPlayerPlayingGame(p)) {
		return game;
	    }
	}
	return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void installData(Object obj) {
	/*
	 *[주의] MiniGame클래스의 생성자에서 만들어도 여기서 저장된 데이터가 불러들이면 생성자에서 한 행동은 모두 없어지고 저장되었던
	 * 데이터로 "교체"됨! -> 생성자에서 특정 변수 선언하지 말고, static class나 method에 인자로 넘겨서 사용
	 */
	this.games = (Map<MiniGameType, MiniGameInterface>) obj;
	
	// 추가된 미니게임 있으면 추가
	this.registerGames();
	
	// 저장된 미니게임을 불러오면 transient변수들은 초기화를 따로 해줘야 함
	for(MiniGameInterface game : this.games.values()) {
	    game.initGameSettings();
	}
    }

    @Override
    public Object getData() {
	return this.games;
    }

    @Override
    public String getDataMemberName() {
	// TODO Auto-generated method stub
	return "minigame";
    }

}
