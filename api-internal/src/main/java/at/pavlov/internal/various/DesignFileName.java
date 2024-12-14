package at.pavlov.internal.various;

import at.pavlov.internal.CannonLogger;
import at.pavlov.internal.utils.FileUtils;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Logger;

@AllArgsConstructor
@Data public class DesignFileName {
    private String ymlString;
    private String schematicString;

    /**
     * returns a list with valid cannon designs (.yml + .schematic)
     *
     * @return
     */
    public static ArrayList<DesignFileName> getDesignFiles(String path) {
        ArrayList<DesignFileName> designList = new ArrayList<>();
        Logger logger = CannonLogger.getLogger();

        try {
            // check plugin/cannons/designs for .yml and .schematic files
            String ymlFile;
            File folder = new File(path);

            File[] listOfFiles = folder.listFiles();
            if (listOfFiles == null) {
                logger.severe("Design folder empty");
                return designList;
            }


            for (File listOfFile : listOfFiles) {
                if (!listOfFile.isFile()) {
                    continue;
                }

                ymlFile = listOfFile.getName();
                if (!ymlFile.endsWith(".yml") && !ymlFile.endsWith(".yaml")) {
                    continue;
                }

                String schematicFile = FileUtils.changeExtension(ymlFile, ".schematic");
                String schemFile = FileUtils.changeExtension(ymlFile, ".schem");
                if (new File(path + schematicFile).isFile()) {
                    // there is a shematic file and a .yml file
                    designList.add(new DesignFileName(ymlFile, schematicFile));
                } else if (new File(path + schemFile).isFile()) {
                    // there is a shematic file and a .yml file
                    designList.add(new DesignFileName(ymlFile, schemFile));
                } else {
                    logger.severe(schematicFile + " is missing");
                }
            }
        } catch (Exception e) {
            logger.severe("Error while checking yml and schematic " + e);
        }
        return designList;
    }
}
