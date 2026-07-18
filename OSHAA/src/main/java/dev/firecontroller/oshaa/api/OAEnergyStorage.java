package dev.firecontroller.oshaa.api;

import net.neoforged.neoforge.energy.EnergyStorage;

public class OAEnergyStorage extends EnergyStorage {
    protected int maxConsume;

    public OAEnergyStorage(int capacity) {
        super(capacity, capacity, capacity, 0);
        this.maxConsume = capacity;
    }

    public OAEnergyStorage(int capacity, int maxTransfer)  {
        super(capacity, maxTransfer, maxTransfer, 0);
        this.maxConsume = maxTransfer;
    }

    public OAEnergyStorage(int capacity, int maxReceive, int maxExtract) {
        super(capacity, maxReceive, maxExtract, 0);
        this.maxConsume = maxExtract;
    }

    public OAEnergyStorage(int capacity, int maxReceive, int maxExtract, int maxConsume) {
        super(capacity, maxReceive, maxExtract, 0);
        this.maxConsume = maxConsume;
    }

    public OAEnergyStorage(int capacity, int maxReceive, int maxExtract, int maxConsume, int energy) {
        super(capacity, maxReceive, maxExtract, energy);
        this.maxConsume = maxConsume;
    }

    public void setEnergyStored(int energy) {
        this.energy = energy;
    }

    public int consumeEnergy(int toConsume, boolean simulate) {
        if (toConsume <= 0) return 0;
        int energyConsumed = Math.min(this.energy, Math.min(this.maxConsume, toConsume));
        if (!simulate) this.energy -= energyConsumed;
        return energyConsumed;
    }

    public boolean canConsume() {
        return this.maxConsume > 0;
    }

}
