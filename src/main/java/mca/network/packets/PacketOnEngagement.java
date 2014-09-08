/*******************************************************************************
 * PacketOnEngagement.java
 * Copyright (c) 2014 Radix-Shock Entertainment.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the MCA Minecraft Mod license.
 ******************************************************************************/

package mca.network.packets;

import io.netty.buffer.ByteBuf;

import java.util.List;

import mca.core.util.object.PlayerMemory;
import mca.entity.EntityVillagerAdult;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import com.radixshock.radixcore.logic.LogicHelper;
import com.radixshock.radixcore.network.packets.AbstractPacket;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketOnEngagement extends AbstractPacket implements IMessage, IMessageHandler<PacketOnEngagement, IMessage>
{
	private int interactingEntityId;

	public PacketOnEngagement()
	{
	}

	public PacketOnEngagement(int interactingEntityId)
	{
		this.interactingEntityId = interactingEntityId;
	}

	@Override
	public void fromBytes(ByteBuf byteBuf)
	{
		interactingEntityId = byteBuf.readInt();
	}

	@Override
	public void toBytes(ByteBuf byteBuf)
	{
		byteBuf.writeInt(interactingEntityId);
	}

	@Override
	public IMessage onMessage(PacketOnEngagement packet, MessageContext context)
	{
		final EntityPlayer player = getPlayer(context);
		final List<Entity> entitiesAroundMe = LogicHelper.getAllEntitiesWithinDistanceOfEntity(player.worldObj.getEntityByID(packet.interactingEntityId), 64);

		for (final Entity entity : entitiesAroundMe)
		{
			if (entity instanceof EntityVillagerAdult)
			{
				final EntityVillagerAdult entityVillager = (EntityVillagerAdult) entity;

				if (entityVillager.playerMemoryMap.containsKey(player.getCommandSenderName()))
				{
					final PlayerMemory memory = entityVillager.playerMemoryMap.get(player.getCommandSenderName());
					memory.hasGift = true;
					entityVillager.playerMemoryMap.put(player.getCommandSenderName(), memory);
				}
			}
		}

		return null;
	}
}
