package at.pavlov.cannons.schematic.world;

import at.pavlov.cannons.schematic.namespace.MinecraftNamespaceHandler;
import lombok.Getter;
import me.vaan.schematiclib.base.namespace.NamespaceHandler;
import me.vaan.schematiclib.base.namespace.NamespaceRegistry;
import me.vaan.schematiclib.base.world.SchematicWorldProcessor;

public class SchematicWorldProcessorImpl implements SchematicWorldProcessor {
    @Getter
    private static final SchematicWorldProcessorImpl processor = new SchematicWorldProcessorImpl();
    private final NamespaceRegistry registry;

    private SchematicWorldProcessorImpl() {
        this.registry = new NamespaceRegistry("minecraft", new MinecraftNamespaceHandler());
    }

    @Override
    public NamespaceRegistry registry() {
        return registry;
    }

    public void registerReflectionNamespace(String namespace, String classToFind, NamespaceHandler handler) {
        try {
            Class.forName(classToFind);
        } catch (Exception e) {
            return;
        }

        registry.registerNamespaceHandler(namespace, handler);
    }
}

