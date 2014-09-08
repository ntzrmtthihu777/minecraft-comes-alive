/*******************************************************************************
 * PacketOnClickTrade.java
 * Copyright (c) 2014 Radix-Shock Entertainment.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the MCA Minecraft Mod license.
 ******************************************************************************/

package mca.network.packets;

import io.netty.buffer.ByteBuf;
import mca.entity.EntityVillagerAdult;
import net.minecraft.entity.player.EntityPlayer;

import com.radixshock.radixcore.network.packets.AbstractPacket;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketOnClickTrade extends AbstractPacket implements IMessage, IMessageHandler<PacketOnClickTrade, IMessage>
{
	private int entityId;

	public PacketOnClickTrade()
	{
	}

	public PacketOnClickTrade(int entityId)
	{
		this.entityId = entityId;
	}

	@Override
	public void fromBytes(ByteBuf byteBuf)
	{
		entityId = byteBuf.readInt();
	}

	@Override
	public void toBytes(ByteBuf byteBuf)
	{
		byteBuf.writeInt(entityId);
	}

	@Override
	public IMessage onMessage(PacketOnClickTrade packet, MessageContext context)
	{
		final EntityPlayer player = getPlayer(context);
		final EntityVillagerAdult villager = (EntityVillagerAdult) player.worldObj.getEntityByID(packet.entityId);

		if (villager != null)
		{
			villager.setCustomer(player);
			player.displayGUIMerchant(villager, "Villager");
		}

		return null;
	}
}
