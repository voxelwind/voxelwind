package com.voxelwind.api.game.item.data;

import com.voxelwind.api.util.DyeColor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class DyedParameterTest {
    public DyedParameterTest(DyeColor color) {
        this.color = color;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        List<Object[]> data = new ArrayList<>();
        for (DyeColor color : DyeColor.values()) {
            data.add(new Object[] { color });
        }
        return data;
    }

    private final DyeColor color;

    @Test
    public void of() throws Exception {
        assertEquals(color, Dyed.of(color).getColor());
        assertEquals(color, Dyed.of((short) color.ordinal()).getColor());
    }

    @Test
    public void toMetadata() throws Exception {
        assertEquals(color.ordinal(), Dyed.of(color).toMetadata());
    }

}