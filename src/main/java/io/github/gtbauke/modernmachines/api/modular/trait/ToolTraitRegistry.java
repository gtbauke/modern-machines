package io.github.gtbauke.modernmachines.api.modular.trait;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jspecify.annotations.Nullable;

import net.minecraft.resources.Identifier;

public class ToolTraitRegistry {
    private static final Map<Identifier, ToolTrait> TRAITS = new ConcurrentHashMap<>();

    public static void register(ToolTrait trait) {
        TRAITS.put(trait.getId(), trait);
    }

    public static @Nullable ToolTrait get(Identifier id) {
        return TRAITS.get(id);
    }

    public static boolean contains(Identifier id) {
        return TRAITS.containsKey(id);
    }

    public static Collection<ToolTrait> getAll() {
        return Collections.unmodifiableCollection(TRAITS.values());
    }
}
