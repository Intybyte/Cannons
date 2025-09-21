package at.pavlov.cannons.schematic.world;

import at.pavlov.cannons.schematic.namespace.MinecraftNamespaceHandler;
import me.vaan.schematiclib.base.namespace.NamespaceRegistry;
import me.vaan.schematiclib.base.world.SchematicWorldProcessor;

public class SchematicWorldProcessorImpl implements SchematicWorldProcessor {
    private final NamespaceRegistry registry;

    public SchematicWorldProcessorImpl() {
        this.registry = new NamespaceRegistry("minecraft", new MinecraftNamespaceHandler());
    }

    @Override
    public NamespaceRegistry registry() {
        return registry;
    }
}

