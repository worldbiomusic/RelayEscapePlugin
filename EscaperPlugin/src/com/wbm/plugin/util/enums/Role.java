package com.wbm.plugin.util.enums;

import org.bukkit.GameMode;

public enum Role {
    WAITER(GameMode.SURVIVAL),
    MAKER(GameMode.CREATIVE),
    TESTER(GameMode.SURVIVAL),
    CHALLENGER(GameMode.SURVIVAL),
    VIEWER(GameMode.SPECTATOR);

    GameMode gamemode;

    Role(GameMode gamemode) {
	this.gamemode = gamemode;
    }
    
    public GameMode getGameMode() {
	return this.gamemode;
    }
}
