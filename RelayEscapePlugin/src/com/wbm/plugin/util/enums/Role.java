package com.wbm.plugin.util.enums;

import org.bukkit.GameMode;

public enum Role {
    웨이터(GameMode.SURVIVAL),
    메이커(GameMode.CREATIVE),
    테스터(GameMode.ADVENTURE),
    챌린저(GameMode.ADVENTURE),
    뷰어(GameMode.ADVENTURE);

    GameMode gamemode;

    Role(GameMode gamemode) {
	this.gamemode = gamemode;
    }
    
    public GameMode getGameMode() {
	return this.gamemode;
    }
}
