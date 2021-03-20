package com.wbm.plugin.util.shop;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.wbm.plugin.data.PlayerData;
import com.wbm.plugin.util.PlayerDataManager;
import com.wbm.plugin.util.enums.Role;
import com.wbm.plugin.util.general.InventoryTool;
import com.wbm.plugin.util.general.ItemStackTool;

public enum ShopGoods {
	/*
	 * 서버에서 파는 다양한 물건 저장소 makingBlock, makingTool, toy 등
	 * 
	 * [주의] 먹는것 추가하면 안됨!
	 */

	// makingBlock

	// 1f
	조약돌(ItemStackTool.item(Material.COBBLESTONE), GoodsRole.MAKING_BLOCK),
	잭오랜턴(ItemStackTool.item(Material.JACK_O_LANTERN), GoodsRole.MAKING_BLOCK),
	유리(ItemStackTool.item(Material.GLASS), GoodsRole.MAKING_BLOCK),
	참나무_울타리(ItemStackTool.item(Material.OAK_FENCE), GoodsRole.MAKING_BLOCK),
	하얀_양털(ItemStackTool.item(Material.WHITE_WOOL)), 
	검정_양털(ItemStackTool.item(Material.BLACK_WOOL)),

	민들레(ItemStackTool.item(Material.DANDELION), GoodsRole.MAKING_BLOCK),
	양귀비(ItemStackTool.item(Material.POPPY), GoodsRole.MAKING_BLOCK),
	노트블럭(ItemStackTool.item(Material.NOTE_BLOCK), GoodsRole.MAKING_BLOCK),
	사다리(ItemStackTool.item(Material.LADDER), GoodsRole.MAKING_BLOCK),
	연꽃잎(ItemStackTool.item(Material.LILY_PAD), GoodsRole.MAKING_BLOCK),
	책장(ItemStackTool.item(Material.BOOKSHELF), GoodsRole.MAKING_BLOCK),

	// 2f
	석탄블럭(ItemStackTool.item(Material.COAL_BLOCK), GoodsRole.MAKING_BLOCK),
	레드스톤블럭(ItemStackTool.item(Material.REDSTONE_BLOCK), GoodsRole.MAKING_BLOCK),
	청금석블럭(ItemStackTool.item(Material.LAPIS_BLOCK), GoodsRole.MAKING_BLOCK),
	철블럭(ItemStackTool.item(Material.IRON_BLOCK), GoodsRole.MAKING_BLOCK),
	금블럭(ItemStackTool.item(Material.GOLD_BLOCK), GoodsRole.MAKING_BLOCK),
	다이아몬드블럭(ItemStackTool.item(Material.DIAMOND_BLOCK), GoodsRole.MAKING_BLOCK),
	
	하얀_양탄자(ItemStackTool.item(Material.WHITE_CARPET), GoodsRole.MAKING_BLOCK),
	검정_양탄자(ItemStackTool.item(Material.BLACK_CARPET), GoodsRole.MAKING_BLOCK),
	빨간_양탄자(ItemStackTool.item(Material.RED_CARPET), GoodsRole.MAKING_BLOCK),
	노랑_양탄자(ItemStackTool.item(Material.YELLOW_CARPET), GoodsRole.MAKING_BLOCK),
	하늘_양탄자(ItemStackTool.item(Material.LIGHT_BLUE_CARPET), GoodsRole.MAKING_BLOCK),
	연두_양탄자(ItemStackTool.item(Material.LIME_CARPET), GoodsRole.MAKING_BLOCK),
	


	// 3f
	참나무_판자(ItemStackTool.item(Material.OAK_WOOD), GoodsRole.MAKING_BLOCK),
	아카시아_계단(ItemStackTool.item(Material.ACACIA_STAIRS), GoodsRole.MAKING_BLOCK),
	참나무_반블럭(ItemStackTool.item(Material.OAK_SLAB), GoodsRole.MAKING_BLOCK),
	참나무_원목(ItemStackTool.item(Material.OAK_LOG), GoodsRole.MAKING_BLOCK),
	자작나무_원목(ItemStackTool.item(Material.BIRCH_LOG), GoodsRole.MAKING_BLOCK),
	정글나무_원목(ItemStackTool.item(Material.JUNGLE_LOG), GoodsRole.MAKING_BLOCK),

	// 4f
	잔디블럭(ItemStackTool.item(Material.GRASS), GoodsRole.MAKING_BLOCK),
	회백토(ItemStackTool.item(Material.DIRT), GoodsRole.MAKING_BLOCK),
	자갈(ItemStackTool.item(Material.GRAVEL), GoodsRole.MAKING_BLOCK),
	돌(ItemStackTool.item(Material.STONE), GoodsRole.MAKING_BLOCK),
	안산암(ItemStackTool.item(Material.STONE), GoodsRole.MAKING_BLOCK),
	화강암(ItemStackTool.item(Material.STONE), GoodsRole.MAKING_BLOCK),

	// 5f
	사암(ItemStackTool.item(Material.SANDSTONE), GoodsRole.MAKING_BLOCK),
	깍인사암(ItemStackTool.item(Material.CUT_SANDSTONE), GoodsRole.MAKING_BLOCK),
	사암_계단(ItemStackTool.item(Material.SANDSTONE_STAIRS), GoodsRole.MAKING_BLOCK),
	사암_반블럭(ItemStackTool.item(Material.SANDSTONE_SLAB), GoodsRole.MAKING_BLOCK),
	조각된_사암(ItemStackTool.item(Material.SANDSTONE), GoodsRole.MAKING_BLOCK),
	모래(ItemStackTool.item(Material.SAND), GoodsRole.MAKING_BLOCK),

	// 6f
	물_양동이(ItemStackTool.item(Material.WATER_BUCKET), GoodsRole.MAKING_BLOCK),
	슬라임블럭(ItemStackTool.item(Material.SLIME_BLOCK), GoodsRole.MAKING_BLOCK),
	베리어(ItemStackTool.item(Material.BARRIER), GoodsRole.MAKING_BLOCK),
	얼음(ItemStackTool.item(Material.PACKED_ICE), GoodsRole.MAKING_BLOCK),
	코어(ItemStackTool.item(Material.GLOWSTONE), GoodsRole.MAKING_BLOCK),
	흙(ItemStackTool.item(Material.DIRT), GoodsRole.MAKING_BLOCK),

	// 7f
	선인장(ItemStackTool.item(Material.CACTUS), GoodsRole.MAKING_BLOCK),
	거미줄(ItemStackTool.item(Material.COBWEB), GoodsRole.MAKING_BLOCK),
	램프(ItemStackTool.item(Material.REDSTONE_LAMP), GoodsRole.MAKING_BLOCK),
	돌_압력판(ItemStackTool.item(Material.STONE_PRESSURE_PLATE), GoodsRole.MAKING_BLOCK),
	레버(ItemStackTool.item(Material.LEVER), GoodsRole.MAKING_BLOCK),
	참나무_다락문(ItemStackTool.item(Material.OAK_TRAPDOOR), GoodsRole.MAKING_BLOCK),

	// 8f
	철문(ItemStackTool.item(Material.IRON_DOOR), GoodsRole.MAKING_BLOCK),
	소울샌드(ItemStackTool.item(Material.SOUL_SAND), GoodsRole.MAKING_BLOCK),
	아카시아_문(ItemStackTool.item(Material.ACACIA_DOOR), GoodsRole.MAKING_BLOCK),
	눈(ItemStackTool.item(Material.SNOW), GoodsRole.MAKING_BLOCK),
	눈블럭(ItemStackTool.item(Material.SNOW_BLOCK), GoodsRole.MAKING_BLOCK),

	// event making block
	점핑블럭(ItemStackTool.item(Material.WHITE_STAINED_GLASS, "점핑블럭", "슈퍼 점프 이벤트 블럭"), GoodsRole.MAKING_BLOCK),
	리스폰블럭(ItemStackTool.item(Material.ORANGE_STAINED_GLASS, "리스폰블럭", "리스폰 이벤트 블럭"), GoodsRole.MAKING_BLOCK),
	함정블럭(ItemStackTool.item(Material.MAGENTA_STAINED_GLASS, "함정블럭", "랜덤 디버프  이벤트 블럭"), GoodsRole.MAKING_BLOCK),
//	FLICKING(ItemStackTool.item(Material.LIGHT_BLUE_STAINED_GLASS, 1, (short) 1, (byte) 3, "FLICKING", "flicking event block"),
//			GoodsRole.MAKING_BLOCK),
	소리테러블럭(ItemStackTool.item(Material.YELLOW_STAINED_GLASS, "소리테러블럭", "소릴 테러  이벤트 블럭"),
			GoodsRole.MAKING_BLOCK),
	상처블럭(ItemStackTool.item(Material.LIME_STAINED_GLASS, "상처블럭", "다치게 하는  이벤트 블럭"), GoodsRole.MAKING_BLOCK),
	회복블럭(ItemStackTool.item(Material.PINK_STAINED_GLASS, "회복블럭", "힐  이벤트 블럭"), GoodsRole.MAKING_BLOCK),
	위_순간이동(ItemStackTool.item(Material.GRAY_STAINED_GLASS, "위_순간이동", "3칸 위로 TP 이벤트 블럭"), GoodsRole.MAKING_BLOCK),
	아래_순간이동(ItemStackTool.item(Material.LIGHT_GRAY_STAINED_GLASS, "아래_순간이동", "3칸 아래로 TP 이벤트 블럭"),
			GoodsRole.MAKING_BLOCK),

	// makingTool
	// 이 템으로 클릭시 list나오고 명령어로 가능하게 하기(명령어에서 이 템 가지고 있나 체크)
	맵_관리(ItemStackTool.item(Material.BOOK, "맵_관리", "이전에 만들었던 맵을 불러오는 도구"), GoodsRole.MAKING),
	공중블럭(ItemStackTool.item(Material.STICK, "공중블럭", "발밑에 블럭을 생성하는 도구"), GoodsRole.MAKING),
	스폰(ItemStackTool.item(Material.GOLD_NUGGET, "스폰", "스폰으로 이동하는 도구"), GoodsRole.MAKING, GoodsRole.TESTING,
			GoodsRole.CHALLENGING, GoodsRole.VIEWING),
	상자(ItemStackTool.item(Material.BRICK, "상자", "맵 만들 블럭을 꺼내는 도구"), GoodsRole.MAKING),
	테스트(ItemStackTool.item(Material.ARROW, "테스트", "테스트시간 시작하는 도구"), GoodsRole.MAKING),
//	BLOCK_CHANGER(
//			ItemStackTool.item(Material.WOOD_PICKAXE, "BLOCK_CHANGER",
//					"Change the block immediately with the block you are holding", "===Mode===", "off"),
//			GoodsRole.MAKING),
	은신(ItemStackTool.item(Material.BOWL, "은신", "자신을 숨기는 도구", "===Mode===", "off"), GoodsRole.MAKING,
			GoodsRole.TESTING),
	겜모변경(ItemStackTool.item(Material.FLINT, "겜모변경", "게임 모드를 변경하는 도구"), GoodsRole.MAKING),
	/*
	 * 밑의 ROOM_SETTING 관련 굿즈 제작시 지켜야 하는 사항
	 * 
	 * kind_# (kind = 굿즈 종류, #숫자)
	 * 
	 * PlayerData에서 getRoomSettingGoodsHighestValue(kind)메소드로 최대값 가져올 수 있게 규칙을 정함
	 */
	// ROOM_SETTING
	높이제한_5(ItemStackTool.item(Material.TRIPWIRE_HOOK, "높이제한_5", "맵 제작시 높이 5칸까지 설치가능"), GoodsRole.ROOM_SETTING),
	높이제한_10(ItemStackTool.item(Material.TRIPWIRE_HOOK, "높이제한_10", "맵 제작시 높이 10칸까지 설치가능"), GoodsRole.ROOM_SETTING),
	높이제한_15(ItemStackTool.item(Material.TRIPWIRE_HOOK, "높이제한_15", "맵 제작시 높이 15칸까지 설치가능"), GoodsRole.ROOM_SETTING),
	높이제한_20(ItemStackTool.item(Material.TRIPWIRE_HOOK, "높이제한_20", "맵 제작시 높이 20칸까지 설치가능"), GoodsRole.ROOM_SETTING),
	높이제한_25(ItemStackTool.item(Material.TRIPWIRE_HOOK, "높이제한_25", "맵 제작시 높이 25칸까지 설치가능"), GoodsRole.ROOM_SETTING),

	// 제작시간 굿즈
	제작시간_5(ItemStackTool.item(Material.TRIPWIRE_HOOK, "제작시간_5", "5분동안 멥 제작 가능"), GoodsRole.ROOM_SETTING),
	제작시간_10(ItemStackTool.item(Material.TRIPWIRE_HOOK, "제작시간_10", "10분동안 멥 제작 가능"), GoodsRole.ROOM_SETTING),
	제작시간_15(ItemStackTool.item(Material.TRIPWIRE_HOOK, "제작시간_15", "15분동안 멥 제작 가능"), GoodsRole.ROOM_SETTING),

	// challengerTool
	도전시간_줄이기(ItemStackTool.item(Material.CLOCK, "HALF_TIME", "도전 시간을 줄입니다"), GoodsRole.CHALLENGING),
	슈퍼스타(ItemStackTool.item(Material.GLOWSTONE_DUST, "SUPER_STAR", "발광 효과 부여", "===Mode===", "off"),
			GoodsRole.ALWAYS),
	로비(ItemStackTool.item(Material.DIAMOND, "LOBBY", "로비로 이동합니다"), GoodsRole.CHALLENGING, GoodsRole.VIEWING),

	// viewrTool
	고스트(ItemStackTool.item(Material.GHAST_TEAR, "GHOST", "관전모드로 변경가능한 도구"), GoodsRole.VIEWING),

	// ALWAYS
	굿즈_컬렉션(ItemStackTool.item(Material.PAPER, "GOODS_LIST", "굿즈 컬렉션을 여는 도구"), GoodsRole.ALWAYS),

	/*
	 * CASH
	 */
//	CHAT(ItemStackTool.item(Material.TORCH, "CHAT", "player can chat"), GoodsRole.IN_POCKET),
	토큰_500(ItemStackTool.item(Material.EMERALD, "TOKEN_500", "500토큰 보상"), GoodsRole.ALWAYS),
	컬러_채팅(ItemStackTool.item(Material.NAME_TAG, "COLOR_CHAT", "채팅 이름 컬러 변경하는 도구"), GoodsRole.ALWAYS);

	// battle kit
//    CHAIN_BOOTS(ItemStackTool.item(Material.CHAINMAIL_BOOTS, "CHAIN_BOOTS", "it's just chain boots"), GoodsRole.BATTLE);

	public ItemStack item;
	public List<GoodsRole> goodsRole;

	ShopGoods(ItemStack item, GoodsRole... goodsRole) {
		this.item = item;

		this.goodsRole = new ArrayList<>();
		for (GoodsRole role : goodsRole) {
			this.goodsRole.add(role);
		}
	}

	public ItemStack getItemStack() {
		return this.item;
	}

	public boolean equals(ShopGoods other) {
		return this.name().equals(other.name());
	}

	public static List<ShopGoods> getGoodsWithGoodsRole(GoodsRole role) {
		/*
		 * 굿즈 역할로 구분된 굿즈 리스트 반환
		 */
		List<ShopGoods> goods = new ArrayList<>();
		for (ShopGoods good : ShopGoods.values()) {
			if (good.isGoodsRoleGoods(role)) {
				goods.add(good);
			}
		}
		return goods;

	}

	public boolean isGoodsRoleGoods(GoodsRole r) {
		// 굿즈의 역할이 맞는지
		return this.goodsRole.contains(r);
	}

	public static List<ShopGoods> getPlayerRoleGoods(Role role) {
		/*
		 * 플레이어 역할에 맞는 굿즈 리스트 반환
		 */
		List<ShopGoods> goods = new ArrayList<>();
		for (ShopGoods good : ShopGoods.values()) {
			if (good.isRoleGood(role)) {
				goods.add(good);
			}
		}
		return goods;
	}

	public boolean isRoleGood(Role role) {
		/*
		 * 해당 굿즈가 역할에 맞는 굿즈인지 검사
		 */

		switch (role) {
		case 웨이터:
			return this.goodsRole.contains(GoodsRole.WAITING);
		case 메이커:
			return this.goodsRole.contains(GoodsRole.MAKING);
		case 테스터:
			return this.goodsRole.contains(GoodsRole.TESTING);
		case 챌린저:
			return this.goodsRole.contains(GoodsRole.CHALLENGING);
		case 뷰어:
			return this.goodsRole.contains(GoodsRole.VIEWING);
		default:
			return false;
		}
	}

	public static void giveGoodsToPlayer(PlayerDataManager pDataManager, Player p) {
		/*
		 * playerData가 가지고 있는 good중 해당 role에 맞는 good만을 인벤토리에 추가함 이 메소드가 실행되기 전에 선행되야 하는
		 * 것: player role 변경!
		 * 
		 * * 각 Role에 맞는 Goods중에서 가지고 있는 Goods 인벤에 지급
		 */
		PlayerData pData = pDataManager.getPlayerData(p.getUniqueId());
		// 플레이어 역할에 맞는 굿즈 제공
		for (ShopGoods good : ShopGoods.getPlayerRoleGoods(pData.getRole())) {
			if (pData.hasGoods(good)) {
				InventoryTool.addItemToPlayer(p, good.getItemStack());
			}
		}
		// 항상 가지고 있어야 하는 굿즈(ALWAYS) 제공
		for (ShopGoods good : ShopGoods.getGoodsWithGoodsRole(GoodsRole.ALWAYS)) {
			if (pData.hasGoods(good)) {
				InventoryTool.addItemToPlayer(p, good.getItemStack());
			}
		}
	}

	public static void giveGoodsToPleyers(PlayerDataManager pDataManager, List<Player> players) {
		for (Player p : players) {
			giveGoodsToPlayer(pDataManager, p);
		}
	}
}
