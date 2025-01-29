package com.frogrilla.echoes.common.signal;

import com.frogrilla.echoes.signal.SignalManager;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;

public abstract class AbstractSignal {
    public static HashMap<String, Class<? extends AbstractSignal>> SIGNAL_TYPES = new HashMap<>();

    public BlockPos blockPos;
    public Direction direction;
    public int frequency;
    public AbstractSignal(BlockPos blockPos, Direction direction, int frequency) {
        this.blockPos = blockPos;
        this.direction = direction;
        this.frequency = frequency;
    }

    public AbstractSignal(NbtCompound compound){
        this.blockPos = BlockPos.fromLong(compound.getLong("pos"));
        this.direction = Direction.byId(compound.getInt("direction"));
        this.frequency = compound.getInt("frequency");
    }

    public NbtCompound asCompound(){
        NbtCompound compound = new NbtCompound();
        compound.putString("type", this.getClass().getName());
        compound.putLong("pos", blockPos.asLong());
        compound.putInt("direction", direction.getId());
        compound.putInt("frequency", frequency);
        return compound;
    }

    public String toString() {
        return String.format("%s, %s, %s", blockPos.toShortString(), frequency, direction);
    }

    /**
     * Called before the signal manager checks if the signal will be ticked
     */
    public void preTick() {};

    /**
     * Called after preTick. Do modifications to the signal in preTick and not here.
     * @return Whether this signal should go through the tick routine of it's manager
     */
    public boolean shouldTick()
    {
        return true;
    }

    /**
     * Does nothing by default, but other signal types may use it to reset their properties.
     * Use as an alternative to creating a new instance.
     */
    public void refreshProperties() {};

    /**
     * Method for how the signal should move by default.
     * Signal interactors should use this method instead of calculating positions themselves.
     */
    public abstract void step();

    /**
     * Called by the signal manager if shouldTick returns true
     * and if the signal isn't handled by a signal interactor.
     */
    public abstract void regularTick(SignalManager manager);

    /**
     * Do effects such as particles and sounds at the given position.
     * @param world
     * @param pos
     */
    public abstract void effects(ServerWorld world, Vec3d pos);

}
