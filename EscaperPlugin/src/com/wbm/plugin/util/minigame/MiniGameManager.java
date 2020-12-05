package com.wbm.plugin.util.minigame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockEvent;

import com.wbm.plugin.data.MiniGameLocation;
import com.wbm.plugin.data.PlayerData;
import com.wbm.plugin.util.PlayerDataManager;
import com.wbm.plugin.util.config.DataMember;
import com.wbm.plugin.util.enums.MiniGameType;
import com.wbm.plugin.util.general.BroadcastTool;
import com.wbm.plugin.util.general.TeleportTool;

public class MiniGameManager implements DataMember {
    // MiniGame 체크하기 위한 Map (이용중이면 true, 비어있으면 false)
    private Map<MiniGameType, MiniGame> games;

    PlayerDataManager pDataManager;

    public MiniGameManager(PlayerDataManager pDataManager) {
	this.pDataManager = pDataManager;

	this.games = new HashMap<>();

	List<MiniGame> allGame = new ArrayList<>();
	allGame.add(new FindTheRed());
	allGame.add(new Painter());

	// 모든 미니게임 games에 등록
	for (MiniGame game : allGame) {
	    this.games.put(game.getGameType(), game);
	}
    }

    public boolean isSomeoneInGameRoom(MiniGameType gameType) {
	// 해당 게임룸에 누군가 플레이 중인지 반환
	return this.games.get(gameType).isSomeoneInGameRoom();
    }

    public void enterRoom(MiniGameType gameType, int fee, Player p) {
	/*
	 * 1.미니게임 표지판 확인 2.사람 들어있는지 확인 3.token충분한지 확인 4.title 확인해서 게임룸 위치로 tp 5.시작
	 */
	PlayerData pData = this.pDataManager.getPlayerData(p.getUniqueId());

	// 2.사람 들어있는지 확인
	if (this.isSomeoneInGameRoom(gameType)) {
	    BroadcastTool.sendMessage(p, "someone is already playing game");
	    return;
	}

	// 3.token충분한지 확인 후 감소
	if (pData.getToken() < fee) {
	    BroadcastTool.sendMessage(p, "you need more token");
	    return;
	}
	// 감소
	pData.subToken(fee);

	// 4.title 확인해서 게임룸 위치로 tp
	Location gameRoom = gameType.getRoomLocation();
	TeleportTool.tp(p, gameRoom);

	// player가 들어갔으므로 이용중(true)으로 변경 -> MiniGame의 start()메소드에서 처리할것
	this.games.get(gameType).startGame(p, this.pDataManager);
	;
    }

    // MiniGame의 exitGame()메소드에서 처리할것임
//	public void exitRoom(MiniGameType game) {
//		this.games.put(game, false);
//	}

    public void processBlockEvent(BlockEvent e) {
	/*
	 * MiniGame 블럭관련 이벤트에 취할 행동들 (블럭이벤트 위치에 따라 각 미니게임 클래스로 넘겨주기)
	 * 
	 * Event클래스로 하려했는데 Location을 못 가져와서 BlockEvent 이벤트로 함
	 */

	Location loc = e.getBlock().getLocation();

	MiniGameType gameType = MiniGameLocation.getMiniGameWithLocation(loc);

	MiniGame game = this.games.get(gameType);

	// gameRoom블럭이 활성화 됫을시에만 반응
	if (game.isActivated()) {
	    game.processEvent(e);
	}

    }

    public void handlePlayerCurrentMiniGameAndExitGame(Player p) {
	/*
	 * 플레이어가 진행중이던 game이 있을 때, 강제로 멈춰야 할때 사용하는 메소드
	 * 
	 * -MiniGame의 startGame에서 예약해놓은 exitGame() thread 없에야 함
	 * 
	 * [주의]이 메소드를 호출하는 위치는 거의
	 * 앞쪽이여야 함(인벤, 위치 설정이 되기 때문에)
	 */
	if (this.isPlayerPlayingGame(p)) {
	    MiniGame game = this.getPlayingGame(p);
	    game.stopAllTasks();
	    game.exitGame(this.pDataManager);
	    
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
	 *[주의] MiniGame클래스의 생성자에서 만들어도 여기서 저장된 데이터가 불러들이면 생성자에서 한 행동은 모두 없어지고 저장되었던
	 * 데이터로 "교체"됨! -> 생성자에서 특정 변수 선언하지 말고, static class나 method에 인자로 넘겨서 사용
	 */
	this.games = (Map<MiniGameType, MiniGame>) obj;
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
