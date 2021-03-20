package com.wbm.plugin.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import com.wbm.plugin.util.enums.MiniGameType;
import com.wbm.plugin.util.enums.Role;
import com.wbm.plugin.util.shop.GoodsRole;
import com.wbm.plugin.util.shop.ShopGoods;

public class PlayerData implements Serializable {
	/*
	 * 속성 추가할때 고려할 것
	 *
	 * 1.get, set method
	 *
	 * 2.toString()에 추가
	 *
	 * 3.constructor에서 초기화
	 *
	 * 4.transient 여부
	 */
	private transient static final long serialVersionUID = 1L;
	private UUID uuid;
	private String name;
	private transient Role role;
	private int token;
	private int cash;

	private int challengingCount;
	private int clearCount;
	private int voted;

	private transient MiniGameType minigame;

	private List<ShopGoods> goods;

	// 부가적인 플레이어 기록 & 이스터에그 기록 & 비상용으로 추가할 변수
	// 추후에 언제든지 CheckList을 추가할 수 있음 (CheckList추가하는 곳은 생성자에 말고, GameManager의
	// TODOListWhenplayerJoinServer()에 추가함 (생성자는 처음 들어온 사람들에게만 실행되므로
	// 이미 접속했던 유저들은 새로운 CheckList들을 업데이트 받지 못하기 때문에)
	private Map<CheckList, Object> checkList;

	public enum CheckList {
		/*
		 * 꼭 int가 아니어도 됨
		 */
		CHAT_COUNT(0), KILL_COUNT(0), JOIN_COUNT(0);

		Object initValue;

		CheckList(Object initValue) {
			this.initValue = initValue;
		}

		public Object getInitValue() {
			return this.initValue;
		}
	}

	public PlayerData(UUID uuid, String name, Role role) {
		this(uuid, name, role, 0, 0, 0, 0, 0);
	}

	public PlayerData(UUID uuid, String name, Role role, int token, int cash, int challengingCount, int clearCount,
			int voted) {
		this.uuid = uuid;
		this.name = name;
		this.role = role;
		this.token = token;
		this.cash = cash;
		this.challengingCount = challengingCount;
		this.clearCount = clearCount;
		this.voted = voted;

		this.goods = new ArrayList<>();

		this.checkList = new HashMap<>();

		this.minigame = null;
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
		Player p = Bukkit.getPlayer(uuid);
		p.setGameMode(this.role.getGameMode());

		// VIEWER && GHOST 굿즈가 있으면 서바이벌 -> 관전자 모드로 변경
		if (this.role == Role.뷰어 && this.hasGoods(ShopGoods.고스트)) {
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

	public int plusToken(int token) {
		/*
		 * pData plus할때는 서버 토큰에서 minus
		 */
		return (this.token += token);
	}

	public boolean minusToken(int token) {
		/*
		 * pData minus할때는 서버 토큰에서 plus
		 */
		if (this.token >= token) {
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

	public boolean addGoods(ShopGoods goods) {
		// 이미 가지고 있는 굿즈는 추가 안함
		if (this.hasGoods(goods)) {
			return false;
		} else {
			this.goods.add(goods);
			return true;
		}
	}

	public void removeGoods(ShopGoods goods) {
		if (this.hasGoods(goods)) {
			this.goods.remove(goods);
		}
	}

	public boolean hasGoods(ShopGoods goods) {
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
			if (good.isGoodsRoleGoods(GoodsRole.ROOM_SETTING)) {
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

	public MiniGameType getMinigame() {
		return minigame;
	}

	public boolean isPlayingMiniGame() {
		return this.minigame != null;
	}

	public void setMinigame(MiniGameType minigame) {
		this.minigame = minigame;
	}

	public void setNull() {
		this.minigame = null;
	}

	public Map<CheckList, Object> getCheckList() {
		return checkList;
	}

	public void setCheckList(CheckList key, Object value) {
		this.checkList.put(key, value);
	}

	public void registerCheckList(CheckList key, Object value) {
		if (!this.checkList.containsKey(key)) {
			this.checkList.put(key, value);
		}
	}

	public Object getCheckListValue(CheckList key) {
		return this.checkList.get(key);
	}

	public int getCash() {
		return cash;
	}

	public void plusCash(int amount) {
		this.cash += amount;
	}

	public boolean minusCash(int amount) {
		// cash도 마이너스가 될 순 없음
		if (this.cash >= amount) {
			this.cash -= amount;
			return true;
		}

		return false;
	}

	@Override
	public String toString() {
		return "PlayerData " + ", \nuuid: " + this.uuid + ", \nname: " + this.name + ", \nrole: " + this.role
				+ ", \ntoken: " + this.token + ", \nchallengingCount: " + this.challengingCount + ", \nclearCount: "
				+ this.clearCount + ", \nvoted: " + this.voted + ", \ngoods: " + this.goods + ", \nminigame: "
				+ this.minigame;
	}

}
