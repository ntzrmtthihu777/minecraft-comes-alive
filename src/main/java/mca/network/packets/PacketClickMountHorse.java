/*******************************************************************************
 * PacketClickMountHorse.java
 * Copyright (c) 2014 Radix-Shock Entertainment.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the MCA Minecraft Mod license.
 ******************************************************************************/

package mca.network.packets;

import io.netty.buffer.ByteBuf;
import mca.core.MCA;
import mca.entity.AbstractEntity;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;

import com.radixshock.radixcore.network.packets.AbstractPacket;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketClickMountHorse extends AbstractPacket implements IMessage, IMessageHandler<PacketClickMountHorse, IMessage>
{
	private int interactingEntityId;
	private int horseEntityId;

	public PacketClickMountHorse()
	{
	}

	public PacketClickMountHorse(int interactingEntityId, int horseEntityId)
	{
		this.interactingEntityId = interactingEntityId;
		this.horseEntityId = horseEntityId;
	}

	@Override
	public void fromBytes(ByteBuf byteBuf)
	{
		interactingEntityId = byteBuf.readInt();
		horseEntityId = byteBuf.readInt();
	}

	@Override
	public void toBytes(ByteBuf byteBuf)
	{
		byteBuf.writeInt(interactingEntityId);
		byteBuf.writeInt(horseEntityId);
	}

	@Override
	public IMessage onMessage(PacketClickMountHorse packet, MessageContext context)
	{
		try
		{
			final EntityPlayer player = getPlayer(context);
			final AbstractEntity entity = (AbstractEntity) player.worldObj.getEntityByID(packet.interactingEntityId);
			final EntityHorse horse = (EntityHorse) player.worldObj.getEntityByID(packet.horseEntityId);

			if (entity != null)
			{
				if (horse != null && horse.riddenByEntity != null && horse.riddenByEntity.getEntityId() == entity.getEntityId())
				{
					entity.dismountEntity(horse);
					entity.ridingEntity = null;
					horse.riddenByEntity = null;
					horse.setHorseSaddled(true);
				}

				else
				{
					if (horse.isTame() && horse.isAdultHorse() && horse.riddenByEntity == null && horse.isHorseSaddled())
					{
						entity.mountEntity(horse);
						horse.setHorseSaddled(false);
					}

					else
					{
						if (!entity.worldObj.isRemote)
						{
							entity.say(MCA.getInstance().getLanguageLoader().getString("notify.horse.invalid", player, entity, false));
						}
					}
				}

				if (!entity.worldObj.isRemote)
				{
					MCA.packetHandler.sendPacketToAllPlayers(new PacketClickMountHorse(packet.interactingEntityId, packet.horseEntityId));
				}
			}
		}

		catch (final NullPointerException e)
		{
			e.printStackTrace();
		}

		return null;
	}
}
