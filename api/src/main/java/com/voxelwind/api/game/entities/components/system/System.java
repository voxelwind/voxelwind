package com.voxelwind.api.game.entities.components.system;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.voxelwind.api.game.entities.Entity;
import com.voxelwind.api.game.entities.components.Component;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

/**
 * A System provides logic to drive an {@link com.voxelwind.api.game.entities.Entity} based on its {@link com.voxelwind.api.game.entities.components.Component}s.
 */
public final class System {
    private final Set<Class<? extends Component>> expectedComponents;
    private final SystemRunner runner;

    private System(Set<Class<? extends Component>> expectedComponents, SystemRunner runner) {
        this.expectedComponents = expectedComponents;
        this.runner = runner;
    }

    public final Set<Class<? extends Component>> getExpectedComponents() {
        return expectedComponents;
    }

    public final SystemRunner getRunner() {
        return runner;
    }

    public final boolean isSystemCompatible(Entity entity) {
        for (Class<? extends Component> component : expectedComponents) {
            if (!entity.providedComponents().contains(component)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        System system = (System) o;
        return Objects.equals(expectedComponents, system.expectedComponents) &&
                Objects.equals(runner, system.runner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(expectedComponents, runner);
    }

    @Override
    public String toString() {
        return "System{" +
                "expectedComponents=" + expectedComponents +
                ", runner=" + runner +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final ImmutableSet.Builder<Class<? extends Component>> expectedComponents = ImmutableSet.builder();
        private SystemRunner runner;

        private Builder() {

        }

        public final Builder expectComponent(Class<? extends Component> component) {
            expectedComponents.add(component);
            return this;
        }

        @SafeVarargs
        public final Builder expectComponents(Class<? extends Component>... components) {
            expectedComponents.addAll(Arrays.asList(components));
            return this;
        }

        public final Builder runner(SystemRunner runner) {
            this.runner = runner;
            return this;
        }

        public final System build() {
            Preconditions.checkArgument(runner != null, "no runner set");
            return new System(expectedComponents.build(), runner);
        }
    }
}
