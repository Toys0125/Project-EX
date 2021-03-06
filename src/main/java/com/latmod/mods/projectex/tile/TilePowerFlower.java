package com.latmod.mods.projectex.tile;

import com.latmod.mods.projectex.block.EnumTier;
import com.latmod.mods.projectex.integration.PersonalEMC;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;

import java.util.UUID;

/**
 * @author LatvianModder
 */
public class TilePowerFlower extends TileEntity implements ITickable
{
	public UUID owner = new UUID(0L, 0L);
	public String name = "";
	public long storedEMC = 0L;

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		owner = nbt.getUniqueId("owner");
		name = nbt.getString("name");
		double storedEMC1 = nbt.getDouble("emc");
		storedEMC = storedEMC1 > Long.MAX_VALUE ? Long.MAX_VALUE : (long) storedEMC1;
		super.readFromNBT(nbt);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt)
	{
		nbt.setUniqueId("owner", owner);
		nbt.setString("name", name);

		if (storedEMC > 0L)
		{
			nbt.setLong("emc", storedEMC);
		}

		return super.writeToNBT(nbt);
	}

	@Override
	public void onLoad()
	{
		if (world.isRemote)
		{
			world.tickableTileEntities.remove(this);
		}

		validate();
	}

	@Override
	public void update()
	{
		if (world.isRemote || world.getTotalWorldTime() % 20L != TileRelay.mod(hashCode(), 20))
		{
			return;
		}

		storedEMC += EnumTier.byMeta(getBlockMetadata()).properties.powerFlowerOutput();

		EntityPlayerMP player = world.getMinecraftServer().getPlayerList().getPlayerByUUID(owner);

		if (player != null)
		{
			PersonalEMC.add(PersonalEMC.get(player), storedEMC);
			storedEMC = 0;
		}
		else
		{
			world.markChunkDirty(pos, this);
		}
	}
}