package com.markus1002.endteleporters.block;

import com.markus1002.endteleporters.Config;
import com.markus1002.endteleporters.EndTeleporters;
import com.markus1002.endteleporters.util.ModSoundEvents;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.registries.ForgeRegistries;

public class EnderFrameTileEntity extends TileEntity implements ITickableTileEntity
{
	private int activationTimer = 0;

	public EnderFrameTileEntity()
	{
		super(EndTeleporters.ENDER_FRAME_TILE);
	}

	public void tick()
	{
		BlockState blockstate = this.world.getBlockState(this.pos);
		if (activationTimer == 1)
		{
			if (!blockstate.get(EnderFrameBlock.ACTIVE) && shouldBeActive())
			{
				setActive(blockstate, true);
			}
			activationTimer--;
		}
		else if (activationTimer <= 0)
		{
			if (!blockstate.get(EnderFrameBlock.ACTIVE) && shouldBeActive())
			{
				activationTimer = 10;
			}
			else if (blockstate.get(EnderFrameBlock.ACTIVE) && !shouldBeActive())
			{
				setActive(blockstate, false);
			}
		}
		else
		{
			activationTimer--;
		}
	}

	private void setActive(BlockState blockstate, boolean active)
	{
		if (blockstate.get(EnderFrameBlock.ACTIVE))
		{
			this.playSound(ModSoundEvents.BLOCK_ENDER_FRAME_DEACTIVATE);
		}
		else
		{
			this.playSound(ModSoundEvents.BLOCK_ENDER_FRAME_ACTIVATE);
		}
		this.world.setBlockState(this.pos, blockstate.with(EnderFrameBlock.ACTIVE, active), 2);
	}
	
	private boolean shouldBeActive()
	{
		if (this.world.getBlockState(this.pos).get(EnderFrameBlock.ENDER_FRAME_EYE) == EnderFrameEye.NONE)
		{
			return false;
		}

		for(int x = -1; x <= 1; ++x)
		{
			for(int z = -1; z <= 1; ++z)
			{
				BlockPos blockpos = this.pos.add(x, -2, z);
				if (this.world.getBlockState(blockpos).getCollisionShape(this.world, blockpos).project(Direction.UP).isEmpty())
				{
					return false;
				}
			}
		}

		for(int x = -1; x <= 1; ++x)
		{
			for(int y = -1; y <= 1; ++y)
			{
				for(int z = -1; z <= 1; ++z)
				{
					BlockPos blockpos = this.pos.add(x, y, z);
					if (!(blockpos.getX() == this.pos.getX() && blockpos.getY() == this.pos.getY() && blockpos.getZ() == this.pos.getZ()) && !this.world.isAirBlock(blockpos))
					{
						return false;
					}
				}
			}
		}

		for(int x = -6; x <= 6; ++x)
		{
			for(int y = -6; y <= 6; ++y)
			{
				for(int z = -6; z <= 6; ++z)
				{
					BlockPos blockpos = this.pos.add(x, y, z);
					if (!(blockpos.getX() == this.pos.getX() && blockpos.getY() == this.pos.getY() && blockpos.getZ() == this.pos.getZ()) && this.world.getBlockState(blockpos).getBlock() == EndTeleporters.ENDER_FRAME)
					{
						return false;
					}
				}
			}
		}

		int purpurblocks = 0;
		for(int x = -2; x <= 2; ++x)
		{
			for(int y = -2; y <= 2; ++y)
			{
				for(int z = -2; z <= 2; ++z)
				{
					BlockState blockstate = this.world.getBlockState(this.pos.add(x, y, z));
					Block block = blockstate.getBlock();
					String name = ForgeRegistries.BLOCKS.getKey(block).toString();
					if (Config.TELEPORTER_BLOCKS.get().contains(name))
					{
						purpurblocks++;
					}
				}
			}
		}

		return purpurblocks >= Config.MIN_TELEPORTER_BLOCKS.get();
	}

	public void read(CompoundNBT compound)
	{
		super.read(compound);
		this.activationTimer = compound.getInt("ActivationTimer");
	}

	public CompoundNBT write(CompoundNBT compound)
	{
		super.write(compound);
		compound.putInt("ActivationTimer", this.activationTimer);
		return compound;
	}

	public void playSound(SoundEvent p_205738_1_)
	{
		this.world.playSound((PlayerEntity)null, this.pos, p_205738_1_, SoundCategory.BLOCKS, 1.0F, 1.0F);
	}
}