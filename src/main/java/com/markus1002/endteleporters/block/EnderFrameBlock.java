package com.markus1002.endteleporters.block;

import java.util.List;
import java.util.Random;

import com.markus1002.endteleporters.Config;
import com.markus1002.endteleporters.EndTeleporters;
import com.markus1002.endteleporters.util.ModSoundEvents;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

public class EnderFrameBlock extends ContainerBlock
{
	public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
	public static final EnumProperty<EnderFrameEye> ENDER_FRAME_EYE = EnumProperty.create("eye", EnderFrameEye.class);

	protected static final VoxelShape SHAPE = Block.makeCuboidShape(3.0D, 3.0D, 3.0D, 13.0D, 13.0D, 13.0D);
	protected static final AxisAlignedBB TELEPORT_AABB = new AxisAlignedBB(-1.5D, -1.5D, -1.5D, 2.5D, 2.5D, 2.5D);

	public EnderFrameBlock(Properties properties)
	{
		super(properties);
		this.setDefaultState(this.stateContainer.getBaseState().with(ENDER_FRAME_EYE, EnderFrameEye.NONE).with(ACTIVE, Boolean.valueOf(false)));
	}

	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
	{
		return SHAPE;
	}

	public BlockRenderLayer getRenderLayer()
	{
		return BlockRenderLayer.CUTOUT;
	}

	public int getLightValue(BlockState state)
	{
		return state.get(ENDER_FRAME_EYE) != EnderFrameEye.NONE && state.get(ACTIVE) ? super.getLightValue(state) : 0;
	}

	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit)
	{
		ItemStack itemstack = player.getHeldItem(handIn);
		if (state.get(ENDER_FRAME_EYE) != EnderFrameEye.NONE)
		{
			if (itemstack.getItem() == Items.ENDER_PEARL)
			{
				if (state.get(ACTIVE))
				{
					boolean flag = false;
					int range = getRange(worldIn, pos);

					findteleporter:
						for(int k = 1; k <= range; ++k)
						{
							for(int i = -3; i <= 3; ++i)
							{
								for(int j = -3; j <= 3; ++j)
								{
									BlockPos blockpos = state.get(ENDER_FRAME_EYE) == EnderFrameEye.UP ? pos.add(i, k, j) : state.get(ENDER_FRAME_EYE) == EnderFrameEye.DOWN ? pos.add(i, -k, j) : state.get(ENDER_FRAME_EYE) == EnderFrameEye.NORTH ? pos.add(i, j, -k) : state.get(ENDER_FRAME_EYE) == EnderFrameEye.SOUTH ? pos.add(i, j, k) : state.get(ENDER_FRAME_EYE) == EnderFrameEye.WEST ? pos.add(-k, j, i) : pos.add(k, j, i);
									BlockState blockstate = worldIn.getBlockState(blockpos);

									if (blockstate.getBlock() == EndTeleporters.ENDER_FRAME && blockstate.get(ACTIVE))
									{
										teleportEntitiesTo(worldIn, pos, blockpos, player, itemstack);
										flag = true;
										break findteleporter;
									}
								}
							}
						}

					if (!flag && !worldIn.isRemote)
					{
						sendStatusMessage(player, new TranslationTextComponent("block.endteleporters.ender_frame.no_teleporter", new Object[] {range}));
						worldIn.playSound((PlayerEntity)null, pos, ModSoundEvents.BLOCK_ENDER_FRAME_TELEPORT_FAIL, SoundCategory.BLOCKS, 1.0F, 1.0F);
					}
					return true;
				}
				else
				{
					if (!worldIn.isRemote)
					{
						sendStatusMessage(player, new TranslationTextComponent("block.endteleporters.ender_frame.not_active"));
						worldIn.playSound((PlayerEntity)null, pos, ModSoundEvents.BLOCK_ENDER_FRAME_TELEPORT_FAIL, SoundCategory.BLOCKS, 1.0F, 1.0F);
					}
					return true;
				}
			}
			else if (player.isAllowEdit())
			{
				worldIn.setBlockState(pos, state.with(ENDER_FRAME_EYE, EnderFrameEye.NONE), 2);
				worldIn.playSound((PlayerEntity)null, pos, SoundEvents.BLOCK_END_PORTAL_FRAME_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
				if (!worldIn.isRemote && !player.abilities.isCreativeMode)
				{
					double d0 = (double)(worldIn.rand.nextFloat() * 0.7F) + (double)0.15F;
					double d1 = (double)(worldIn.rand.nextFloat() * 0.7F) + (double)0.060000002F + 0.3D;
					double d2 = (double)(worldIn.rand.nextFloat() * 0.7F) + (double)0.15F;
					ItemEntity itementity = new ItemEntity(worldIn, (double)pos.getX() + d0, (double)pos.getY() + d1, (double)pos.getZ() + d2, new ItemStack(Items.ENDER_EYE));
					itementity.setDefaultPickupDelay();
					worldIn.addEntity(itementity);
				}
				return true;
			}
		}
		else if (itemstack.getItem() == Items.ENDER_EYE && player.isAllowEdit())
		{
			if (!worldIn.isRemote)
			{
				worldIn.setBlockState(pos, state.with(ENDER_FRAME_EYE, getEyeState(hit.getFace())));
				worldIn.playSound((PlayerEntity)null, pos, SoundEvents.BLOCK_END_PORTAL_FRAME_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
				if (!player.abilities.isCreativeMode)
				{
					itemstack.shrink(1);
				}
			}
			return true;
		}
		else if (itemstack.getItem() == Items.ENDER_PEARL)
		{
			if (!worldIn.isRemote)
			{
				sendStatusMessage(player, new TranslationTextComponent("block.endteleporters.ender_frame.no_eye"));
				worldIn.playSound((PlayerEntity)null, pos, ModSoundEvents.BLOCK_ENDER_FRAME_TELEPORT_FAIL, SoundCategory.BLOCKS, 1.0F, 1.0F);
			}
			return true;
		}
		return false;
	}

	private void sendStatusMessage(LivingEntity livingentity, TranslationTextComponent translationTextComponent)
	{
		if (livingentity != null && livingentity instanceof PlayerEntity)
		{
			((PlayerEntity)livingentity).sendStatusMessage(translationTextComponent, true);
		}
	}

	private EnderFrameEye getEyeState(Direction direction)
	{
		return direction == Direction.UP ? EnderFrameEye.UP : direction == Direction.DOWN ? EnderFrameEye.DOWN : direction == Direction.NORTH ? EnderFrameEye.NORTH : direction == Direction.SOUTH ? EnderFrameEye.SOUTH : direction == Direction.WEST ? EnderFrameEye.WEST : EnderFrameEye.EAST;
	}

	private int getRange(World worldIn, BlockPos pos)
	{
		int range = 0;

		for(int x = -2; x <= 2; ++x)
		{
			for(int y = -2; y <= 2; ++y)
			{
				for(int z = -2; z <= 2; ++z)
				{
					if (range >= Config.MAX_TELEPORTER_RANGE.get())
					{
						break;
					}
					BlockState blockstate = worldIn.getBlockState(pos.add(x, y, z));
					Block block = blockstate.getBlock();
					String name = ForgeRegistries.BLOCKS.getKey(block).toString();
					if (Config.TELEPORTER_BLOCKS.get().contains(name))
					{
						range += Config.RANGE_PER_BLOCK.get();
					}
				}
			}
		}

		return range;
	}

	private void teleportEntitiesTo(World worldIn, BlockPos pos, BlockPos targetpos, PlayerEntity player, ItemStack itemstack)
	{
		AxisAlignedBB axisalignedbb = TELEPORT_AABB.offset(pos);
		List<? extends Entity> list = worldIn.getEntitiesWithinAABBExcludingEntity((Entity)null, axisalignedbb);

		if (!list.isEmpty())
		{
			boolean flag = false;
			for(Entity entity : list)
			{
				String name = ForgeRegistries.ENTITIES.getKey(entity.getType()).toString();
				if (!Config.TELEPORTABLE_ENTITIES_BLACKLIST.get().contains(name))
				{
					if (entity.getBoundingBox().minX >= axisalignedbb.minX && entity.getBoundingBox().minY >= axisalignedbb.minY && entity.getBoundingBox().minZ >= axisalignedbb.minZ &&
							entity.getBoundingBox().maxX <= axisalignedbb.maxX && entity.getBoundingBox().maxY <= axisalignedbb.maxY && entity.getBoundingBox().maxZ <= axisalignedbb.maxZ)
					{
						double d0 = targetpos.getX() - pos.getX();
						double d1 = targetpos.getY() - pos.getY();
						double d2 = targetpos.getZ() - pos.getZ();
						worldIn.playSound((PlayerEntity)null, entity.prevPosX, entity.prevPosY, entity.prevPosZ, ModSoundEvents.BLOCK_ENDER_FRAME_TELEPORT, entity.getSoundCategory(), 1.0F, 1.0F);
						entity.setPositionAndUpdate(entity.posX + d0, entity.posY + d1, entity.posZ + d2);
						worldIn.playSound((PlayerEntity)null, entity.posX, entity.posY,entity.posZ, ModSoundEvents.BLOCK_ENDER_FRAME_TELEPORT, entity.getSoundCategory(), 1.0F, 1.0F);

						flag = true;
					}
				}
			}
		
			if (flag && !player.abilities.isCreativeMode)
			{
				itemstack.shrink(1);
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand)
	{
		if (stateIn.get(ENDER_FRAME_EYE) != EnderFrameEye.NONE && stateIn.get(ACTIVE))
		{
			for(int i = 0; i < 3; ++i)
			{
				int j = rand.nextInt(2) * 2 - 1;
				int k = rand.nextInt(2) * 2 - 1;
				double d0 = (double)pos.getX() + 0.5D + 0.25D * (double)j;
				double d1 = (double)((float)pos.getY() + rand.nextFloat());
				double d2 = (double)pos.getZ() + 0.5D + 0.25D * (double)k;
				double d3 = (double)(rand.nextFloat() * (float)j);
				double d4 = ((double)rand.nextFloat() - 0.5D) * 0.125D;
				double d5 = (double)(rand.nextFloat() * (float)k);
				worldIn.addParticle(ParticleTypes.PORTAL, d0, d1, d2, d3, d4, d5);
			}
		}
	}

	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
	{
		builder.add(ACTIVE, ENDER_FRAME_EYE);
	}

	public PushReaction getPushReaction(BlockState state)
	{
		return PushReaction.IGNORE;
	}

	public TileEntity createNewTileEntity(IBlockReader worldIn)
	{
		return new EnderFrameTileEntity();
	}

	public BlockRenderType getRenderType(BlockState state)
	{
		return BlockRenderType.MODEL;
	}
}