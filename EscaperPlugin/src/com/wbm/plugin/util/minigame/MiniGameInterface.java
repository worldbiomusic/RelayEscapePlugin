package com.wbm.plugin.util.minigame;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import com.wbm.plugin.util.PlayerDataManager;
import com.wbm.plugin.util.enums.MiniGameType;

public interface MiniGameInterface {
    /*
     * 나중에 바꿀 것
     * 
     * 클래스이름 MiniGame으로 바꾸고 abstract로 선언
     * 
     * 공통 메소드, 변수들은 미리 구현해놓기
     */
    public void enterRoom(Player p, PlayerDataManager pDataManager);
    public boolean isActivated();
    public void processEvent(Event event);
    public boolean isPlayerPlayingGame(Player p);
    public void stopAllTasks();
    public void exitGame(PlayerDataManager pDataManager);
    public MiniGameType getGameType();
    public void initGameSettings();
}
