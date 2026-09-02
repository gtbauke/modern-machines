package io.github.gtbauke.modernmachines.data;

import net.minecraft.WorldVersion;

public class VersionProbe {
    public static void probe() {
        var sb = new StringBuilder();
        for (var m : WorldVersion.class.getMethods()) {
            sb.append(m.getName()).append(":").append(m.getReturnType().getSimpleName()).append(", ");
        }
        throw new RuntimeException("WORLD_VERSION_METHODS: " + sb);
    }
}
