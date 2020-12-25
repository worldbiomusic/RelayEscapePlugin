package com.wbm.plugin.util.minigame;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import com.wbm.plugin.util.PlayerDataManager;
import com.wbm.plugin.util.enums.MiniGameType;

public interface MiniGame {
    enum ExitReason {
	SELF_EXIT,
	RELAY_TIME_CHANGED;
    }
    /*
     * 나중에 바꿀 것
     * 
     * 클래스이름 MiniGame으로 바꾸고 abstract로 선언
     * 
     * 공통 메소드, 변수들은 미리 구현해놓기
     */
    public abstract void enterRoom(Player p, PlayerDataManager pDataManager);
    public abstract boolean isActivated();
    public abstract void processEvent(Event event);
    public abstract boolean isPlayerPlayingGame(Player p);
    public abstract void stopAllTasks();
    public abstract void exitGame(PlayerDataManager pDataManager);
    public abstract MiniGameType getGameType();
    public abstract void initGameSettings();
    public abstract void processHandlingMiniGameExitDuringPlaying(Player p, PlayerDataManager pDataManager, MiniGame.ExitReason reason);
}
