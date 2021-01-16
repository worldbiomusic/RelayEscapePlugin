package com.wbm.plugin.util.shop;

public enum GoodsRole {
    /*
     * 각 Goods가 사용될 RelayTime과 관련딘 enum
     */
    WAITING,

    MAKING,

    TESTING,

    CHALLENGING,

    VIEWING,

    FUN,

    MAKING_BLOCK,
    
    ROOM_SETTING,
    
    ALWAYS,
    
    // CHAT같은 굿즈로 아이템으로 가지고 있을 필요가 없는 궂즈 롤
    IN_POCKET,
    
    // battle관련 미니게임에서 사용하는 궂즈 롤
    BATTLE;
}
