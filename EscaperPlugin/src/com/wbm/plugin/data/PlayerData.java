package com.wbm.plugin.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import com.wbm.plugin.util.enums.Role;
import com.wbm.plugin.util.shop.GoodsRole;
import com.wbm.plugin.util.shop.ShopGoods;

public class PlayerData implements Serializable {
    /*
     * 속성 추가할때 고칠것 1.get, set method 2.toString()에 추가 3.constructor 관리 4.data저장하는것이면
     * data부분도 관리
     */
    private static final long serialVersionUID = 1L;
    private  UUID uuid;
    private String name;
    private transient Role role;
    private int token;

    private  int challengingCount;
    private  int clearCount;
    private int voted;

    private List<ShopGoods> goods;

    public PlayerData(UUID uuid, String name, Role role) {
	this(uuid, name, role, 0, 0, 0, 0);
    }

    public PlayerData(UUID uuid, String name, Role role, int token, int challengingCount, int clearCount, int voted) {
	this.uuid = uuid;
	this.name = name;
	this.role = role;
	this.token = token;
	this.challengingCount = challengingCount;
	this.clearCount = clearCount;
	this.voted = voted;

	this.goods = new ArrayList<>();
    }

    public UUID getUUID() {
	return uuid;
    }

    public void setUUID(UUID uuid) {
	this.uuid = uuid;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public Role getRole() {
	return role;
    }

    public void setRole(Role role) {
	/*
	 * Role과 GameMode는 동시에 이루어져야 하기 때문에 밑에 Role에 따라 Game모드 설정을 따로 추가함
	 */
	this.role = role;
	this.setPlayerGameModeWithRole();
    }

    public void setPlayerGameModeWithRole() {
	Player p=Bukkit.getPlayer(uuid);
	p.setGameMode(this.role.getGameMode());
	
	// VIEWER && GHOST 굿즈가 있으면 서바이벌 -> 관전자 모드로 변경
	if(this.role == Role.VIEWER && this.doesHaveGoods(ShopGoods.GHOST)) {
	    p.setGameMode(GameMode.SPECTATOR);
	}
    }

    public int getToken() {
	return token;
    }

    // 토큰 추적을 위해 사용 금지
//    public void setToken(int token) {
//	this.token = token;
//    }

    public void plusToken(int token) {
	/*
	 * pData plus할때는 서버 토큰에서 minus
	 */
	this.token += token;
    }

    public boolean minusToken(int token) {
	/*
	 * pData minus할때는 서버 토큰에서 plus
	 */
	if(this.token >= token) {
	    this.token -= token;
	    return true;
	}
	
	return false;
    }

    public int getChallengingCount() {
	return challengingCount;
    }

    public void setChallengingCount(int challengingCount) {
	this.challengingCount = challengingCount;
    }

    public void addChallengingCount(int challengingCount) {
	this.challengingCount += challengingCount;
    }

    public void subChallengingCount(int challengingCount) {
	this.challengingCount -= challengingCount;
    }

    public int getClearCount() {
	return clearCount;
    }

    public void setClearCount(int clearCount) {
	this.clearCount = clearCount;
    }

    public void addClearCount(int clearCount) {
	this.clearCount += clearCount;
    }

    public void subClearCount(int clearCount) {
	this.clearCount -= clearCount;
    }

    public int getVoted() {
	return voted;
    }

    public void setVoted(int voted) {
	this.voted = voted;
    }

    public void addVoted(int voted) {
	this.voted += voted;
    }

    public void subVoted(int voted) {
	this.voted -= voted;
    }

    public List<ShopGoods> getGoods() {
	return goods;
    }

    public void setGoods(List<ShopGoods> makingGoods) {
	this.goods = makingGoods;
    }

    public void addGoods(ShopGoods goods) {
	this.goods.add(goods);
    }

    public boolean doesHaveGoods(ShopGoods goods) {
	return this.goods.contains(goods);
    }
    
    public void makeEmptyGoods() {
	this.goods = new ArrayList<>();
    }
    
    public int getRoomSettingGoodsHighestValue(String kind) {
	/*
	 * RoomSetting들중 kind에서 가장 높은 값 반환
	 */
	int maxValue = Integer.MIN_VALUE;
	// 굿즈중에서 가장 높은 굿즈 검색
	for (ShopGoods good : this.getGoods()) {
	    if (good.getGoodsRole() == GoodsRole.ROOM_SETTING) {
		// 이름이 kind로 시작할떄 뒤에 숫자만 가져오기
		if (good.name().startsWith(kind)) {
		    String goodsStirng = good.name().split("_")[1];
		    int goodsValue = Integer.parseInt(goodsStirng);
		    if (goodsValue > maxValue) {
			maxValue = goodsValue;
		    }
		}
	    }
	}
	return maxValue;
    }


    @Override
    public String toString() {
	return "PlayerData " + ", \nuuid: " + this.uuid + ", \nname: " + this.name + ", \nrole: " + this.role
		+ ", \ntoken: " + this.token + ", \nchallengingCount: " + this.challengingCount + ", \nclearCount: "
		+ this.clearCount + ", \nvoted: " + this.voted + ", \ngoods: " + this.goods;
    }

}
