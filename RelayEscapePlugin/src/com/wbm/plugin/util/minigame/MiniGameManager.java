package com.wbm.plugin.util.minigame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.player.PlayerEvent;

import com.wbm.plugin.util.PlayerDataManager;
import com.wbm.plugin.util.config.DataMember;
import com.wbm.plugin.util.enums.MiniGameType;
import com.wbm.plugin.util.general.BroadcastTool;
import com.wbm.plugin.util.minigame.games.BattleTown;
import com.wbm.plugin.util.minigame.games.Critical;
import com.wbm.plugin.util.minigame.games.FindTheBlue;
import com.wbm.plugin.util.minigame.games.FindTheRed;
import com.wbm.plugin.util.minigame.games.FindTheYellow;
import com.wbm.plugin.util.minigame.games.FitTool;
import com.wbm.plugin.util.minigame.games.JumpMap;
import com.wbm.plugin.util.minigame.games.Painter;

public class MiniGameManager implements DataMember {
    // MiniGame 체크하기 위한 Map (이용중이면 true, 비어있으면 false)
    private Map<MiniGameType, MiniGame> games;

    PlayerDataManager pDataManager;

    public MiniGameManager(PlayerDataManager pDataManager) {
	this.pDataManager = pDataManager;

	this.games = new HashMap<>();

	this.registerGames();
    }

    public void registerGames() {
	MiniGame.pDataManager = this.pDataManager;

	List<MiniGame> allGame = new ArrayList<>();
	allGame.add(new FindTheRed());
	allGame.add(new Painter());
	allGame.add(new FindTheYellow());
	allGame.add(new FindTheBlue());
	allGame.add(new BattleTown());
	allGame.add(new JumpMap());
	allGame.add(new FitTool());
	allGame.add(new Critical());
	

	// 모든 미니게임 games에 등록
	for (MiniGame game : allGame) {
	    if (!this.games.containsKey(game.getGameType())) {
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

	MiniGame game = this.games.get(gameType);

	// 시작
	// activated상태이면 참가 불가능
	if (game.isActivated()) {
	    BroadcastTool.sendMessage(p, "game is already started");
	    return;
	} else {
	    game.enterRoom(p, this.pDataManager);
	}
    }

    public void processEvent(Event event) {

	/*
	 * MiniGame에서 이벤트에 취할 행동들 (이벤트 위치에 따라 각 미니게임 클래스로 넘겨주기)
	 *
	 * [주의] 이벤트 넘겨주기 전에 꼭! 미니게임 장소에서 발생한 이벤트인지 체크해야 함!
	 */
	Location eventLoc;

	if (event instanceof BlockEvent) { // 블럭 관련 이벤트
	    BlockEvent blockEvent = (BlockEvent) event;
	    eventLoc = blockEvent.getBlock().getLocation();
	}else if (event instanceof EntityEvent) { // 엔티티 관련 이벤트
	    EntityEvent entityEvent = (EntityEvent) event;
	    eventLoc = entityEvent.getEntity().getLocation();
	} else if (event instanceof PlayerEvent) { // 플레이어 관련 이벤트
	    PlayerEvent playerEvent = (PlayerEvent) event;
	    eventLoc = playerEvent.getPlayer().getLocation();
	} else {
	    // 처리할 이벤트 대상이 아닐땐 반환
	    return;
	}
	
	MiniGameType gameType =  MiniGameType.getMiniGameWithLocation(eventLoc);
	
	// ㅇ
	MiniGame game = this.games.get(gameType);
	
	if (game != null) {

	    // gameRoom블럭이 활성화 됫을시에만 반응
	    if (game.isActivated()) {
		game.processEvent(event);
	    }
	}
    }

    public void handleMiniGameExitDuringPlaying(Player p, MiniGame.ExitReason reason) {
	/*
	 * 플레이어가 진행중이던 game이 있을 때, 강제로 멈춰야 할때 사용하는 메소드
	 * 
	 * -MiniGame의 startGame에서 예약해놓은 exitGame() thread 없에야 함
	 * 
	 * [주의]이 메소드를 호출하는 위치는 거의 앞쪽이여야 함(인벤, 위치 설정이 되기 때문에)
	 * 
	 * 각 미니게임에서 processHandlingMiniGameExitDuringPlaying() 메소드 실행하기
	 * 
	 * SoloMiniGame - stopAllTasks(), exitGame()
	 * 
	 * CooperativeMiniGame - players에서 탈퇴, 보상 못받음
	 * 
	 * BattleMiniGame - players에서 탈퇴, 보상 못받음
	 * 
	 * 각 나가는 상황이 어쩔 수 없는 경우에는(RelayTime으로 인한 핸들링) 게임 자체를 정상적으로 종료하기
	 */
	if (this.isPlayerPlayingGame(p)) {
	    MiniGame game = this.getPlayingGame(p);
	    if (game != null) {
		game.processHandlingMiniGameExitDuringPlaying(p, this.pDataManager, reason);
	    }

	    BroadcastTool.debug("handle exit game!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
	}
    }

    public boolean isPlayerPlayingGame(Player p) {
	/*
	 * player가 게임 중인지?
	 */
	for (MiniGame game : this.games.values()) {
	    if (game.isPlayerPlayingGame(p)) {
		return true;
	    }
	}
	return false;
    }

    public MiniGame getPlayingGame(Player p) {
	for (MiniGame game : this.games.values()) {
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
	 * [주의] MiniGame클래스의 생성자에서 만들어도 여기서 저장된 데이터가 불러들이면 생성자에서 한 행동은 모두 없어지고 저장되었던
	 * 데이터로 "교체"됨! -> 생성자에서 특정 변수 선언하지 말고, static class나 method에 인자로 넘겨서 사용
	 */
	this.games = (Map<MiniGameType, MiniGame>) obj;

	// 추가된 미니게임 있으면 추가
	this.registerGames();

	// 저장된 미니게임을 불러오면 transient변수들은 초기화를 따로 해줘야 함
	for (MiniGame game : this.games.values()) {
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
