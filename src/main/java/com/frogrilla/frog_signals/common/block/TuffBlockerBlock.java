package com.frogrilla.frog_signals.common.block;

import com.frogrilla.frog_signals.signals.Signal;
import com.frogrilla.frog_signals.signals.SignalManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class TuffBlockerBlock extends Block implements ISignalInteractor {

    public static final BooleanProperty UP = BooleanProperty.of("up");
    public static final BooleanProperty DOWN = BooleanProperty.of("down");
    public static final BooleanProperty NORTH = BooleanProperty.of("north");
    public static final BooleanProperty EAST = BooleanProperty.of("east");
    public static final BooleanProperty SOUTH = BooleanProperty.of("south");
    public static final BooleanProperty WEST = BooleanProperty.of("west");

    public TuffBlockerBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState()
                .with(UP, false)
                .with(DOWN, false)
                .with(NORTH, false)
                .with(EAST, false)
                .with(SOUTH, false)
                .with(WEST, false)
        );
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(UP);
        builder.add(DOWN);
        builder.add(NORTH);
        builder.add(EAST);
        builder.add(SOUTH);
        builder.add(WEST);
    }

    public static BooleanProperty getPropertyFromDirection(Direction direction){
        switch (direction){
            case UP -> {
                return UP;
            }
            case DOWN -> {
                return DOWN;
            }
            case NORTH -> {
                return NORTH;
            }
            case EAST -> {
                return EAST;
            }
            case SOUTH -> {
                return SOUTH;
            }
            case WEST -> {
                return WEST;
            }
        }
        return UP;
    }

    @Override
    public void processSignal(Signal incoming, SignalManager manager, ServerWorld serverWorld) {
        BlockState state = serverWorld.getBlockState(incoming.getBlockPos());
        if(state.get(getPropertyFromDirection(incoming.getDirection().getOpposite()))){
            serverWorld.playSound((PlayerEntity) null, incoming.getBlockPos(), SoundEvents.ENTITY_MAGMA_CUBE_HURT_SMALL, SoundCategory.BLOCKS, 1, 1);
            incoming.step();
            if(incoming.getPower() == 0){
                manager.removeSignal(incoming);
            }
        }
        else{
            serverWorld.playSound((PlayerEntity) null, incoming.getBlockPos(), SoundEvents.BLOCK_POLISHED_TUFF_HIT, SoundCategory.BLOCKS, 1, 1);
            manager.removeSignal(incoming);
        }
    }

    @Override
    protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if(world.isClient()) return ActionResult.PASS;

        BooleanProperty property = getPropertyFromDirection(hit.getSide());
        if(state.get(property)){
            if(stack.isIn(ItemTags.AXES)){
                world.playSound((PlayerEntity) null, pos, SoundEvents.ITEM_AXE_SCRAPE, SoundCategory.BLOCKS, 1, 1);
                world.setBlockState(pos, state.with(property, false));
                return ActionResult.CONSUME;
            }
        }
        else if(stack.getItem() == Items.MAGMA_CREAM){
            world.playSound((PlayerEntity) null, pos, SoundEvents.BLOCK_SLIME_BLOCK_PLACE, SoundCategory.BLOCKS, 1, 1);
            world.setBlockState(pos, state.with(property, true));
            return ActionResult.CONSUME;
        }
        return ActionResult.PASS;
    }
}