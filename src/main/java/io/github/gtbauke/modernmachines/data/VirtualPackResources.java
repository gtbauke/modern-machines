package io.github.gtbauke.modernmachines.data;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.AbstractPackResources;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.util.InclusiveRange;

public class VirtualPackResources extends AbstractPackResources {
    private final PackType packType;
    private final Map<Identifier, byte[]> resources = new HashMap<>();
    private final Map<String, Set<Identifier>> resourcesByNamespace = new HashMap<>();
    private final byte[] packMcmeta;

    public VirtualPackResources(String id, String title, PackType packType, PackSource packSource) {
        super(new PackLocationInfo(id, Component.literal(title), packSource, Optional.empty()));
        this.packType = packType;

        var versionFormat = SharedConstants.getCurrentVersion().packVersion(packType).major();
        var mcmeta = """
                {
                  "pack": {
                    "description": "%s",
                    "pack_format": %d
                  }
                }
                """.formatted(title, versionFormat);
        this.packMcmeta = mcmeta.getBytes(StandardCharsets.UTF_8);
    }

    public void addResource(Identifier id, String jsonContent) {
        var bytes = jsonContent.getBytes(StandardCharsets.UTF_8);
        resources.put(id, bytes);
        resourcesByNamespace.computeIfAbsent(id.getNamespace(), k -> new HashSet<>()).add(id);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> @Nullable T getMetadataSection(@NonNull MetadataSectionType<T> type) {
        if (type == PackMetadataSection.SERVER_TYPE || type == PackMetadataSection.CLIENT_TYPE || type == PackMetadataSection.FALLBACK_TYPE) {
            var format = SharedConstants.getCurrentVersion().packVersion(packType);
            var range = new InclusiveRange<>(format, format);
            return (T) new PackMetadataSection(location().title(), range);
        }

        return null;
    }

    @Override
    public @Nullable IoSupplier<InputStream> getRootResource(String... segments) {
        if (segments.length == 1 && "pack.mcmeta".equals(segments[0])) {
            return () -> new ByteArrayInputStream(packMcmeta);
        }

        return null;
    }

    @Override
    public @Nullable IoSupplier<InputStream> getResource(@NonNull PackType packType, @NonNull Identifier location) {
        var bytes = resources.get(location);
        if (bytes != null) {
            return () -> new ByteArrayInputStream(bytes);
        }

        return null;
    }

    @Override
    public void listResources(@NonNull PackType packType, @NonNull String namespace, @NonNull String path, @NonNull ResourceOutput resourceOutput) {
        var ids = resourcesByNamespace.get(namespace);
        if (ids == null) {
            return;
        }

        if (path.isEmpty()) {
            for (var id : ids) {
                var bytes = resources.get(id);
                if (bytes != null) {
                    resourceOutput.accept(id, () -> new ByteArrayInputStream(bytes));
                }
            }

            return;
        }

        var prefix = path.endsWith("/") ? path : path + "/";
        for (var id : ids) {
            if (id.getPath().startsWith(prefix)) {
                var bytes = resources.get(id);
                if (bytes != null) {
                    resourceOutput.accept(id, () -> new ByteArrayInputStream(bytes));
                }
            }
        }
    }

    @Override
    public @NonNull Set<String> getNamespaces(@NonNull PackType packType) {
        return Collections.unmodifiableSet(resourcesByNamespace.keySet());
    }

    @Override
    public void close() {
        // In-memory resources are retained for multiple pack opens and server reloads
    }
}
