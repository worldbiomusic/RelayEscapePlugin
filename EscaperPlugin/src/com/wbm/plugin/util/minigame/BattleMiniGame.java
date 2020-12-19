package com.wbm.plugin.util.minigame;

import java.io.Serializable;

import org.bukkit.entity.Player;

import com.wbm.plugin.util.PlayerDataManager;
import com.wbm.plugin.util.enums.MiniGameType;

public abstract class BattleMiniGame implements Serializable, MiniGameInterface{

    private static final long serialVersionUID = 1L;

    public BattleMiniGame(MiniGameType gameType, int timeLimit, int fee) {
    }

    @Override
    public void enterRoom(Player p, PlayerDataManager pDataManager) {
	
    }

}
