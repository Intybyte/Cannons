package at.pavlov.cannons.schematic.formats;

import at.pavlov.cannons.Cannons;
import at.pavlov.cannons.schematic.block.WorldEditBlock;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.util.io.Closer;
import com.sk89q.worldedit.world.block.BlockState;
import me.vaan.schematiclib.base.block.IBlock;
import me.vaan.schematiclib.base.formats.FileExceptionUnknown;
import me.vaan.schematiclib.base.formats.SchematicLoader;
import me.vaan.schematiclib.base.schematic.Schematic;
import me.vaan.schematiclib.file.schematic.FileSchematic;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WorldEditFormat implements SchematicLoader {
    @Override
    public Schematic load(File file) throws Throwable {
        Clipboard cc = loadSchematic(file);
        if (cc == null) throw new FileExceptionUnknown("Schematic not found");

        Region region = cc.getRegion();

        List<IBlock> positions = new ArrayList<>();

        for (BlockVector3 pos : region) {
            BlockState state = cc.getBlock(pos);
            positions.add(new WorldEditBlock(state, pos));
        }

        return new FileSchematic(positions);
    }

    private Clipboard loadSchematic(File schematicFile) {
        ClipboardFormat format = ClipboardFormats.findByFile(schematicFile);

        if(format == null) {
            Cannons.getPlugin().logSevere("Error while loading schematic " + schematicFile.getPath() + " : Format not found");
            return null;
        }

        try (Closer closer = Closer.create()) {
            FileInputStream fis = closer.register(new FileInputStream(schematicFile));
            BufferedInputStream bis = closer.register(new BufferedInputStream(fis));
            ClipboardReader reader = closer.register(format.getReader(bis));

            return reader.read();
        } catch (IOException e) {
            Cannons.getPlugin().logSevere("Error while loading schematic " + schematicFile.getPath() + " : IO Error");
            return null;
        }
    }
}
