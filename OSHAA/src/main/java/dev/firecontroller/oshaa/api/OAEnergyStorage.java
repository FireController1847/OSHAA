package dev.firecontroller.oshaa.api;

import net.neoforged.neoforge.energy.EnergyStorage;

public class OAEnergyStorage extends EnergyStorage {
    protected int maxConsume;
    private final Runnable onChanged;

    public OAEnergyStorage(int capacity) {
        super(capacity, capacity, capacity, 0);
        this.maxConsume = capacity;
        this.onChanged = null;
    }

    public OAEnergyStorage(int capacity, Runnable onChanged) {
        super(capacity, capacity, capacity, 0);
        this.maxConsume = capacity;
        this.onChanged = onChanged;
    }

    public OAEnergyStorage(int capacity, int maxTransfer)  {
        super(capacity, maxTransfer, maxTransfer, 0);
        this.maxConsume = maxTransfer;
        this.onChanged = null;
    }

    public OAEnergyStorage(int capacity, int maxTransfer, Runnable onChanged)  {
        super(capacity, maxTransfer, maxTransfer, 0);
        this.maxConsume = maxTransfer;
        this.onChanged = onChanged;
    }

    public OAEnergyStorage(int capacity, int maxReceive, int maxExtract) {
        super(capacity, maxReceive, maxExtract, 0);
        this.maxConsume = maxExtract;
        this.onChanged = null;
    }

    public OAEnergyStorage(int capacity, int maxReceive, int maxExtract, Runnable onChanged) {
        super(capacity, maxReceive, maxExtract, 0);
        this.maxConsume = maxExtract;
        this.onChanged = onChanged;
    }

    public OAEnergyStorage(int capacity, int maxReceive, int maxExtract, int maxConsume) {
        super(capacity, maxReceive, maxExtract, 0);
        this.maxConsume = maxConsume;
        this.onChanged = null;
    }

    public OAEnergyStorage(int capacity, int maxReceive, int maxExtract, int maxConsume, Runnable onChanged) {
        super(capacity, maxReceive, maxExtract, 0);
        this.maxConsume = maxConsume;
        this.onChanged = onChanged;
    }

    public OAEnergyStorage(int capacity, int maxReceive, int maxExtract, int maxConsume, int energy) {
        super(capacity, maxReceive, maxExtract, energy);
        this.maxConsume = maxConsume;
        this.onChanged = null;
    }

    public OAEnergyStorage(int capacity, int maxReceive, int maxExtract, int maxConsume, int energy, Runnable onChanged) {
        super(capacity, maxReceive, maxExtract, energy);
        this.maxConsume = maxConsume;
        this.onChanged = onChanged;
    }

    @Override
    public int receiveEnergy(int toReceive, boolean simulate) {
        int received = super.receiveEnergy(toReceive, simulate);
        if (onChanged != null && !simulate && received > 0) {
            onChanged.run();
        }
        return received;
    }

    @Override
    public int extractEnergy(int toExtract, boolean simulate) {
        int extracted = super.extractEnergy(toExtract, simulate);
        if (onChanged != null && !simulate && extracted > 0) {
            onChanged.run();
        }
        return extracted;
    }

    public void setCapacity(int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException("Capacity cannot be negative.");
        }
        if (this.capacity == capacity) return;

        this.capacity = capacity;
        this.energy = Math.min(this.energy, capacity);

        if (this.onChanged != null) {
            this.onChanged.run();
        }
    }

    public void setEnergyStored(int energy) {
        this.energy = energy;
        if (onChanged != null) onChanged.run();
    }

    public int consumeEnergy(int toConsume, boolean simulate) {
        if (toConsume <= 0) return 0;
        int energyConsumed = Math.min(this.energy, Math.min(this.maxConsume, toConsume));
        if (!simulate) this.energy -= energyConsumed;
        if (onChanged != null && !simulate && energyConsumed > 0) onChanged.run();
        return energyConsumed;
    }

    public boolean canConsume() {
        return this.maxConsume > 0;
    }

}
