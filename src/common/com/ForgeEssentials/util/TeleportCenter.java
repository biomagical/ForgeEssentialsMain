package com.ForgeEssentials.util;

import java.util.ArrayList;
import java.util.EnumSet;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.ServerConfigurationManager;

import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.util.AreaSelector.WarpPoint;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.IScheduledTickHandler;
import cpw.mods.fml.common.TickType;

public class TeleportCenter implements IScheduledTickHandler
{
	private static ArrayList<TPdata> que = new ArrayList();
	private static ArrayList<TPdata> removeQue = new ArrayList();
	
	public static int tpWarmup;
	public static int tpCooldown;
	
	public static void addToTpQue(WarpPoint point, EntityPlayer player)
	{
		if(PlayerInfo.getPlayerInfo(player).TPcooldown != 0)
		{
			player.sendChatToPlayer("Cooldown still active. Still got " + PlayerInfo.getPlayerInfo(player).TPcooldown + "sec to go.");
		}
		else
		{
			PlayerInfo.getPlayerInfo(player).TPcooldown = tpCooldown;
			TPdata data = new TPdata(point, player);
			if(tpWarmup == 0)
			{
				data.doTP();
			}
			else
			{
				player.sendChatToPlayer("Stand still for " + tpWarmup + "sec.");
				que.add(data);
			}
		}
	}
	
	public static void abort(TPdata tpData) 
	{
		removeQue.add(tpData);
		tpData.getPlayer().sendChatToPlayer("TP aborted");
	}
	
	public static void TPdone(TPdata tpData) 
	{
		removeQue.add(tpData);
		tpData.getPlayer().sendChatToPlayer("*Poof*");
	}
	
	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) 
	{
		for(TPdata data : que)
		{
			data.count();
		}
		que.removeAll(removeQue);
		removeQue.clear();
		for(Object player : FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().playerEntityList)
		{
			PlayerInfo.getPlayerInfo((EntityPlayer) player).TPcooldownTick();
		}
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) 
	{
		//Not needed here
	}

	@Override
	public EnumSet<TickType> ticks() 
	{
		return EnumSet.of(TickType.SERVER);
	}

	@Override
	public String getLabel() 
	{
		return "TeleportCenter";
	}

	@Override
	public int nextTickSpacing() 
	{
		return 20;
	}
}
