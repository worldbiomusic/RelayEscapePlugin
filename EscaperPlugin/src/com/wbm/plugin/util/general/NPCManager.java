package com.wbm.plugin.util.general;

import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URL;
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

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.wbm.plugin.util.config.DataMember;

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

	public NPCManager()
	{
		this.entityPlayerDatas=new HashMap<String, EntityPlayerData>();
		this.NPCs=new HashMap<String, EntityPlayer>();
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

		String[] name=getSkin(skin);
		if(name==null)
		{
			BroadcastTool.debug("not exist skin");
			return;
		}
		gameProfile.getProperties().put("textures", new Property("textures", name[0], name[1]));

		// 현재 접속중인 사람들에게 패킷 전송
		spreadNPCPacketToEveryone(npc);

//		// map에 넣을 데이터 NPCEntityPlayer로 변환해서 저장(EntityPlayer클래스는 serializable이 구현 안되서 저장이 안됨..)
		Location pLoc=loc;
		EntityPlayerData npcData=new EntityPlayerData(loc.getWorld().getName(), pLoc.getX(), pLoc.getY(), pLoc.getZ(),
				pLoc.getYaw(), pLoc.getPitch(), skin, name[0], name[1]);
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

//	public void loadNPC(Location loc, GameProfile profile) {
//		/*
//		 * loc = 위치
//		 * profile = 스킨
//		 */
//		MinecraftServer server=((CraftServer)Bukkit.getServer()).getServer();
//		WorldServer world=((CraftWorld)loc.getWorld()).getHandle();
//		// gameProfile의 주번째 인자는 name으로 16자가 넘어가면 안됨
//		// 모든 유저의 GameProfile은 Mojang에 저장되있어서 가져와서 사용가능!(이름, uuid, 스킨 등등 의 정보가 포함되있음)
//		GameProfile gameProfile = profile;
//		EntityPlayer npc=new EntityPlayer(server, world, gameProfile, new PlayerInteractManager(world));
//		npc.setLocation(loc.getX(), loc.getY(), loc.getZ(),
//				loc.getYaw(), loc.getPitch());
//
//		// 현재 접속중인 사람들에게 패킷 전송
//		addNPCPacket(npc);
//	}

	private String[] getSkin(String name)
	{
		try
		{
			URL url=new URL("https://api.mojang.com/users/profiles/minecraft/"+name);
			InputStreamReader reader=new InputStreamReader(url.openStream());
			String uuid=new JsonParser().parse(reader).getAsJsonObject().get("id").getAsString();

			URL url2=new URL("https://sessionserver.mojang.com/session/minecraft/profile/"+uuid+"?unsigned=false");
			InputStreamReader reader2=new InputStreamReader(url2.openStream());

			JsonObject property=new JsonParser().parse(reader2).getAsJsonObject().get("properties").getAsJsonArray()
					.get(0).getAsJsonObject();

			String texture=property.get("value").getAsString();
			String signature=property.get("signature").getAsString();

			return new String[]{texture, signature};
		}
		catch(Exception e)
		{
			/*
			 * 예외1: 플레이어가 없을때 예외2: 모장사이트에 너무 많은 request할때
			 */
			e.printStackTrace();

			// 오류나면 그냥 취소메세지 보내기

//			// 오류나면 그냥 현재 플레이어것으로 처리
//			EntityPlayer ep = ((CraftPlayer)p).getHandle();
//			GameProfile profile = ep.getProfile();
//			Property property = profile.getProperties().get("texture").iterator().next();
//			
//			String texture = property.getValue();
//			String signature = property.getSignature();
//			
//			return new String[] {texture, signature};

			return null;
		}
	}

	public void spreadAllPacketToEveryone()
	{
		for(Player p : Bukkit.getOnlinePlayers())
		{
			this.addAllPacketToPlayer(p);
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
		}
	}

	public void addAllPacketToPlayer(Player p)
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
			this.addAllPacketToPlayer(p);
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
