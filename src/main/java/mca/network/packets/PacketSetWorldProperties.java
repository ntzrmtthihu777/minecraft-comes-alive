/*******************************************************************************
 * PacketSetWorldProperties.java
 * Copyright (c) 2014 Radix-Shock Entertainment.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the MCA Minecraft Mod license.
 ******************************************************************************/

package mca.network.packets;

import io.netty.buffer.ByteBuf;

import java.lang.reflect.Field;

import mca.core.MCA;
import net.minecraft.entity.player.EntityPlayer;

import com.radixshock.radixcore.core.RadixCore;
import com.radixshock.radixcore.file.WorldPropertiesManager;
import com.radixshock.radixcore.network.ByteBufIO;
import com.radixshock.radixcore.network.packets.AbstractPacket;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketSetWorldProperties extends AbstractPacket implements IMessage, IMessageHandler<PacketSetWorldProperties, IMessage>
{
	private WorldPropertiesManager manager;

	public PacketSetWorldProperties()
	{
	}

	public PacketSetWorldProperties(WorldPropertiesManager manager)
	{
		this.manager = manager;
	}

	@Override
	public void fromBytes(ByteBuf byteBuf)
	{
		manager = (WorldPropertiesManager) ByteBufIO.readObject(byteBuf);
	}

	@Override
	public void toBytes(ByteBuf byteBuf)
	{
		ByteBufIO.writeObject(byteBuf, manager);
	}

	@Override
	public IMessage onMessage(PacketSetWorldProperties packet, MessageContext context)
	{
		final EntityPlayer player = getPlayer(context);
		final WorldPropertiesManager recvManager = packet.manager;
		final WorldPropertiesManager myManager = MCA.getInstance().playerWorldManagerMap.get(player.getCommandSenderName());

		recvManager.mod = MCA.getInstance();

		try
		{
			if (myManager != null)
			{
				myManager.mod = MCA.getInstance();
				if (player.worldObj.isRemote) //Received from the server.
				{
					for (final Field field : myManager.worldPropertiesInstance.getClass().getDeclaredFields())
					{
						final Object serverValue = field.get(recvManager.worldPropertiesInstance);
						final Object clientValue = field.get(myManager.worldPropertiesInstance);

						if (!clientValue.equals(serverValue))
						{
							field.set(myManager.worldPropertiesInstance, serverValue);
						}
					}
				}

				else
				//Received from client.
				{
					for (final Field field : myManager.worldPropertiesInstance.getClass().getDeclaredFields())
					{
						final Object clientValue = field.get(recvManager.worldPropertiesInstance);
						final Object serverValue = field.get(myManager.worldPropertiesInstance);

						if (!serverValue.equals(clientValue) && !field.getName().equals("playerID"))
						{
							field.set(myManager.worldPropertiesInstance, clientValue);
							RadixCore.getInstance().getLogger().log("Updated field: " + field.getName() + " : " + clientValue);
						}
					}

					myManager.saveWorldProperties();
				}
			}

			else
			{
				MCA.getInstance().playerWorldManagerMap.put(player.getCommandSenderName(), recvManager);
			}
		}

		catch (final Throwable e)
		{
			e.printStackTrace();
		}

		return null;
	}
}
