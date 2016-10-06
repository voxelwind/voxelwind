package com.voxelwind.api.game.item.data.wood;

import com.google.common.base.Preconditions;
import com.voxelwind.api.game.Metadata;
import com.voxelwind.api.game.util.data.TreeSpecies;

import java.util.Objects;

/**
 * Represents an item that is woody in nature.
 */
public class Wood implements Metadata {
    public static Wood of(TreeSpecies species) {
        Preconditions.checkNotNull(species, "species");
        return new Wood(species);
    }

    private final TreeSpecies species;

    Wood(TreeSpecies species) {
        this.species = species;
    }

    public TreeSpecies getSpecies() {
        return species;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Wood wood = (Wood) o;
        return species == wood.species;
    }

    @Override
    public int hashCode() {
        return Objects.hash(species);
    }

    @Override
    public String toString() {
        return "Wood{" +
                "species=" + species +
                '}';
    }
}
