package com.voxelwind.api.server.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * A translated and replaced message used for localization in MCPE.
 */
@Nonnull
public class TranslatedMessage {
    private final String name;
    private final List<String> replacements;

    public TranslatedMessage(String name, List<String> replacements) {
        this.name = Preconditions.checkNotNull(name, "name");
        this.replacements = ImmutableList.copyOf(Preconditions.checkNotNull(replacements, "replacements"));
    }

    public String getName() {
        return name;
    }

    public List<String> getReplacements() {
        return replacements;
    }
}
