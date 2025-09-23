package at.pavlov.cannons.schematic.world;

import at.pavlov.cannons.schematic.block.BlockImpl;
import at.pavlov.cannons.schematic.namespace.MinecraftNamespaceHandler;
import lombok.Getter;
import me.vaan.schematiclib.base.block.IBlock;
import me.vaan.schematiclib.base.namespace.NamespaceHandler;
import me.vaan.schematiclib.base.namespace.NamespaceRegistry;
import me.vaan.schematiclib.base.world.SchematicWorldProcessor;
import org.bukkit.block.Block;

import java.util.UUID;

public class SchematicWorldProcessorImpl implements SchematicWorldProcessor {
    @Getter
    private static final SchematicWorldProcessorImpl processor = new SchematicWorldProcessorImpl();
    private final NamespaceRegistry registry;
    private final MinecraftNamespaceHandler mcHandler = new MinecraftNamespaceHandler();

    private SchematicWorldProcessorImpl() {
        this.registry = new NamespaceRegistry("minecraft", mcHandler);
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

    public Block getRaw(IBlock block, UUID world) {
        return ((BlockImpl) mcHandler.get(block.x(), block.y(), block.z(), world)).getBlock();
    }
}

