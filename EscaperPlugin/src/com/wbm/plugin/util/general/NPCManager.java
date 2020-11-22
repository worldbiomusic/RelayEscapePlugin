package com.wbm.plugin.util.general;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.CraftServer;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.wbm.plugin.util.config.DataMember;
import com.wbm.plugin.util.general.skin.SkinData;
import com.wbm.plugin.util.general.skin.SkinManager;

import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.MinecraftServer;
import net.minecraft.server.v1_12_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_12_R1.PacketPlayOutEntityHeadRotation;
import net.minecraft.server.v1_12_R1.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_12_R1.PlayerConnection;
import net.minecraft.server.v1_12_R1.PlayerInteractManager;
import net.minecraft.server.v1_12_R1.WorldServer;

class EntityPlayerData implements Serializable
{

	/**
	 * EntityPlayer클래스가 Serialziable을 구현안해서 파일로 저장할수가 없어서 새로 상속해서 만들어서 저장할 변수만 저장하게
	 * 할 클래스 저장할 변수: world, x, y, z, yaw, pitch, name, texture, signature
	 */

	String world;
	double x, y, z;
	float yaw, pitch;
	String name;
	String texture;
	String signature;

	private static final long serialVersionUID=1L;

	public EntityPlayerData(String world, double x, double y, double z, float yaw, float pitch, String name,
			String texture, String signature)
	{
		this.world=world;
		this.x=x;
		this.y=y;
		this.z=z;
		this.yaw=yaw;
		this.pitch=pitch;
		this.name=name;

		this.texture=texture;
		this.signature=signature;
	}

//	public NPCEntityPlayer(EntityPlayer npc) {
//		this.world = npc.getWorld().getWorld().getName();
//		this.x = npc.getX();
//		this.y = npc.getY();
//		this.z = npc.getZ();
//		this.yaw = npc.yaw;
//		this.pitch = npc.pitch;
//		this.name = npc.getName();
//		
//		Property property = (Property)npc.getProfile().getProperties().get("textures");
//		this.texture = property.getValue();
//		this.signature = property.getSignature();
//	}

}

public class NPCManager implements DataMember
{
	/*
	 * 패킷을 이용하여 플레이어들에게 가짜 EntityPlayer 보여주는 클래스
	 * 
	 * file save하는 기능까지 포함된ㄷ NPCTool 클래스 DataManager, DataMember에 의존함
	 * 
	 * TODO:
	 * 
	 * @delete 만들기
	 * 
	 * @EntityPlayer 따로 관리(메모리 낭비 방지)
	 * 
	 * 사용법 /re npc create <npcName> <skin> /re npc delete <npcName> ※npcName은 단순히
	 * map서 key로 사용하는 구별하는 장치일뿐임, 실제보이는 이름은 skin으로 됨
	 * 
	 */

	// NPC(EntityPlayer)의 일부만 저장하는 리스트
	private Map<String, EntityPlayerData> entityPlayerDatas;

	private Map<String, EntityPlayer> NPCs;
	
	SkinManager skinManager;

	public NPCManager(SkinManager skinManager)
	{
		this.entityPlayerDatas=new HashMap<String, EntityPlayerData>();
		this.NPCs=new HashMap<String, EntityPlayer>();
		this.skinManager = skinManager;
	}

	public void createNPC(Location loc, String npcName, String skin)
	{
		/*
		 * player의 현재 위치에 npcName이름으로 된 skin의 이름의 스킨을 가지고 있는 NPC 생성
		 */
		MinecraftServer server=((CraftServer)Bukkit.getServer()).getServer();
		WorldServer world=((CraftWorld)Bukkit.getWorld(loc.getWorld().getName())).getHandle();
		// gameProfile의 주번째 인자는 name으로 16자가 넘어가면 안됨
		// 모든 유저의 GameProfile은 Mojang에 저장되있어서 가져와서 사용가능!(이름, uuid, 스킨 등등 의 정보가 포함되있음)
		GameProfile gameProfile=new GameProfile(UUID.randomUUID(), skin);
		EntityPlayer npc=new EntityPlayer(server, world, gameProfile, new PlayerInteractManager(world));
		npc.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());

		SkinData skinData =this.skinManager.getPlayerSkinData(skin);
		
		// 잘못된 skin인지 검사
		if(skinData==null)
		{
			BroadcastTool.debug(skin + " not exist skin");
			return;
		}
		String texture = skinData.getTexture();
		String signature = skinData.getSignature();
		// gameprofile에 skin적용
		gameProfile.getProperties().put("textures", new Property("textures", texture, signature));
		
		// 현재 접속중인 사람들에게 패킷 전송
		this.spreadNPCPacketToEveryone(npc);

		// map에 넣을 데이터 NPCEntityPlayer로 변환해서 저장(EntityPlayer클래스는 serializable이 구현 안되서 저장이 안됨..)
		Location pLoc=loc;
		EntityPlayerData npcData=new EntityPlayerData(loc.getWorld().getName(), pLoc.getX(), pLoc.getY(), pLoc.getZ(),
				pLoc.getYaw(), pLoc.getPitch(), skin, texture, signature);
		
		// put to map(entityPlayerDatas)
		this.entityPlayerDatas.put(npcName, npcData);

		// put to map(NPCs)
		this.NPCs.put(npcName, npc);

	}

	public void delete(String npcName)
	{
		if(!this.NPCs.containsKey(npcName))
		{
			BroadcastTool.debug("npc: " + npcName + "is not exist!!");
			return;
		}
			// 먼저 deletion packet 전송해서 사리지게 한 후에
			sendDeletionPacketToEveryone(this.NPCs.get(npcName));

			// map에서 삭제
			this.entityPlayerDatas.remove(npcName);
			this.NPCs.remove(npcName);
	}

	public void sendDeletionPacketToEveryone(EntityPlayer npc)
	{
		for(Player all : Bukkit.getOnlinePlayers())
		{
			PlayerConnection connection=((CraftPlayer)all).getHandle().playerConnection;
			connection.sendPacket(
					new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, npc));
			connection.sendPacket(new PacketPlayOutEntityDestroy(npc.getId()));
		}
	}

	public void spreadAllNPCToEveryone()
	{
		for(Player p : Bukkit.getOnlinePlayers())
		{
			this.sendAllNPCPacketToPlayer(p);
		}
	}

	public void spreadNPCPacketToEveryone(EntityPlayer npc)
	{
		/*
		 * 모든유저에게 한개의 NPC 패킷 전송
		 */
		for(Player p : Bukkit.getOnlinePlayers())
		{
			PlayerConnection connection=((CraftPlayer)p).getHandle().playerConnection;
			connection.sendPacket(
					new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, npc));
			connection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));
			connection.sendPacket(new PacketPlayOutEntityHeadRotation(npc, (byte)(npc.yaw*256/360)));
			// 이거 하면 스킨도 없어짐...
//			connection.sendPacket(
//					new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, npc));
		}
	}

	public void sendAllNPCPacketToPlayer(Player p)
	{
		/*
		 * 1명 유저에게 모든 NPC 패킷 전송
		 */
		for(EntityPlayer npc : this.NPCs.values())
		{
			PlayerConnection connection=((CraftPlayer)p).getHandle().playerConnection;
			connection.sendPacket(
					new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, npc));
			connection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));
			connection.sendPacket(new PacketPlayOutEntityHeadRotation(npc, (byte)(npc.yaw*256/360)));
			// 이거 하면 스킨도 없어짐...
//			connection.sendPacket(
//					new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, npc));
		}
	}

	public Map<String, EntityPlayer> getNPCs()
	{
		return this.NPCs;
	}

	private EntityPlayer EntityPlayerDataToEntityPlayer(EntityPlayerData npcData)
	{
		/*
		 * 데이터 누수로 따로 NPC 리스트 만들어서 관리해야 할듯
		 */
		MinecraftServer server=((CraftServer)Bukkit.getServer()).getServer();
		WorldServer world=((CraftWorld)Bukkit.getWorld(npcData.world)).getHandle();
		// gameProfile의 주번째 인자는 name으로 16자가 넘어가면 안됨
		// 모든 유저의 GameProfile은 Mojang에 저장되있어서 가져와서 사용가능!(이름, uuid, 스킨 등등 의 정보가 포함되있음)
		GameProfile gameProfile=new GameProfile(UUID.randomUUID(), npcData.name);
		EntityPlayer npc=new EntityPlayer(server, world, gameProfile, new PlayerInteractManager(world));
		npc.setLocation(npcData.x, npcData.y, npcData.z, npcData.yaw, npcData.pitch);

		gameProfile.getProperties().put("textures", new Property("textures", npcData.texture, npcData.signature));

		return npc;
	}

//	private EntityPlayerData EntityPlayerToEntityPlayerData(EntityPlayer npc) {
//		Property property = (Property)npc.getProfile().getProperties().get("textures"); 
//		EntityPlayerData npcData = new EntityPlayerData("world", npc.getX(), npc.getY(), npc.getZ(), 
//				npc.yaw, npc.pitch, npc.getName(), property.getValue(), property.getSignature());
//		
//		return npcData;
//	}

	void loadAllNPCs()
	{
//		for(EntityPlayer npc : this.NPCs.values()) { 
//			this.spreadNPCPacket(npc);
//		}

		for(Player p : Bukkit.getOnlinePlayers())
		{
			this.sendAllNPCPacketToPlayer(p);
		}
	}

	private void makeNPCListFromEntityPlayerData(Map<String, EntityPlayerData> entityPlayerData)
	{
		/*
		 * installData할떄 EntityPlayerData정보를 바탕으로 실제NPC(EntityPlayer)를 만들어서 NPCs리스트에
		 * 넣어놓고 사용
		 */
		for(Entry<String, EntityPlayerData> entry : entityPlayerData.entrySet())
		{
			EntityPlayer npc=this.EntityPlayerDataToEntityPlayer(entry.getValue());
			this.NPCs.put(entry.getKey(), npc);
		}
	}

//	private List<EntityPlayerData> getEntityPlayerListFromNPCs() {
//		List<EntityPlayerData> data = new ArrayList<EntityPlayerData>();
//		for(EntityPlayer npc : this.NPCs.values()) {
//			EntityPlayerData d = this.EntityPlayerToEntityPlayerData(npc);
//			data.add(d);
//		}
//		
//		return data;
//	}

	@SuppressWarnings("unchecked")
	@Override
	public void installData(Object obj)
	{
		// TODO Auto-generated method stub
		this.entityPlayerDatas=(Map<String, EntityPlayerData>)obj;

//		BroadcastTool.debug("==================NPC data==================");
//		for(EntityPlayer ep : this.NPCs.values()) {
//			BroadcastTool.printConsoleMessage(ep.getName());
//		}

		// EntityPlayerData -> NPC(EntityPlayer)로 변경해서 NPCs리스트에 넣음
		makeNPCListFromEntityPlayerData(this.entityPlayerDatas);

		loadAllNPCs();
	}

	@Override
	public Object getData()
	{
		// TODO Auto-generated method stub
		return this.entityPlayerDatas;
	}

	@Override
	public String getDataMemberName()
	{
		return "npc";
	}
}
