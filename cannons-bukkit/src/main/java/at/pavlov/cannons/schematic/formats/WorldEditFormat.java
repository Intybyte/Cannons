package at.pavlov.cannons.schematic.formats;

import at.pavlov.cannons.Cannons;
import at.pavlov.cannons.schematic.block.WorldEditBlock;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.session.ClipboardHolder;
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

        ClipboardHolder clipboardHolder = new ClipboardHolder(cc);
        clipboardHolder.setTransform(new AffineTransform().translate(cc.getMinimumPoint().multiply(-1)));
        Clipboard transformedCC = clipboardHolder.getClipboard();

        cc.setOrigin(BlockVector3.ZERO);

        List<IBlock> positions = new ArrayList<>();

        int width = transformedCC.getDimensions().getBlockX();
        int height = transformedCC.getDimensions().getBlockY();
        int length = transformedCC.getDimensions().getBlockZ();

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                for (int z = 0; z < length; ++z) {
                    BlockVector3 pt = BlockVector3.at(x, y, z);
                    BlockState blockState = cc.getBlock(pt.add(cc.getMinimumPoint()));

                    positions.add(new WorldEditBlock(blockState, pt));
                }
            }
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
