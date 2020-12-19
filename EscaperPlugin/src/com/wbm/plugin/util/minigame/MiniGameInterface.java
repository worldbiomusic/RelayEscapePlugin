package com.wbm.plugin.util.minigame;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import com.wbm.plugin.util.PlayerDataManager;
import com.wbm.plugin.util.enums.MiniGameType;

public interface MiniGameInterface {
    public void enterRoom(Player p, PlayerDataManager pDataManager);
    public boolean isActivated();
    public void processEvent(Event event);
    public boolean isPlayerPlayingGame(Player p);
    public void stopAllTasks();
    public void exitGame(PlayerDataManager pDataManager);
    public MiniGameType getGameType();
    public void initGame();
}
