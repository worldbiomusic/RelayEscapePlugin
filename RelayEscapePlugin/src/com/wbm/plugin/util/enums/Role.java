package com.wbm.plugin.util.enums;

import org.bukkit.GameMode;

public enum Role {
    WAITER(GameMode.SURVIVAL),
    MAKER(GameMode.CREATIVE),
    TESTER(GameMode.ADVENTURE),
    CHALLENGER(GameMode.ADVENTURE),
    VIEWER(GameMode.ADVENTURE);

    GameMode gamemode;

    Role(GameMode gamemode) {
	this.gamemode = gamemode;
    }
    
    public GameMode getGameMode() {
	return this.gamemode;
    }
}
