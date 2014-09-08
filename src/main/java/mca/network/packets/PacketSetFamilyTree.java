/*******************************************************************************
 * PacketSetFamilyTree.java
 * Copyright (c) 2014 Radix-Shock Entertainment.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the MCA Minecraft Mod license.
 ******************************************************************************/

package mca.network.packets;

import io.netty.buffer.ByteBuf;
import mca.core.MCA;
import mca.core.util.object.FamilyTree;
import mca.entity.AbstractEntity;
import net.minecraft.entity.player.EntityPlayer;

import com.radixshock.radixcore.network.ByteBufIO;
import com.radixshock.radixcore.network.packets.AbstractPacket;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;

public class PacketSetFamilyTree extends AbstractPacket implements IMessage, IMessageHandler<PacketSetFamilyTree, IMessage>
{
	private int entityId;
	private FamilyTree familyTree;

	public PacketSetFamilyTree()
	{
	}

	public PacketSetFamilyTree(int entityId, FamilyTree familyTree)
	{
		this.entityId = entityId;
		this.familyTree = familyTree;
	}

	@Override
	public void fromBytes(ByteBuf byteBuf)
	{
		entityId = byteBuf.readInt();
		familyTree = (FamilyTree) ByteBufIO.readObject(byteBuf);
	}

	@Override
	public void toBytes(ByteBuf byteBuf)
	{
		byteBuf.writeInt(entityId);
		ByteBufIO.writeObject(byteBuf, familyTree);
	}

	@Override
	public IMessage onMessage(PacketSetFamilyTree packet, MessageContext context)
	{
		final EntityPlayer player = getPlayer(context);
		final AbstractEntity entity = (AbstractEntity) player.worldObj.getEntityByID(packet.entityId);

		if (entity != null)
		{
			packet.familyTree.owner = entity;
			entity.familyTree = packet.familyTree;

			if (context.side == Side.SERVER)
			{
				MCA.packetHandler.sendPacketToAllPlayers(packet);
			}
		}

		return null;
	}
}
