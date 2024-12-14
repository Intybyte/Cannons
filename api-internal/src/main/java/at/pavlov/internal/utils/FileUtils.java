package at.pavlov.internal.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {
    /**
     * changes the extension of the a string (e.g. classic.yml to
     * classic.schematic)
     *
     * @param originalName
     * @param newExtension
     * @return
     */
    public static String changeExtension(String originalName, String newExtension) {
        int lastDot = originalName.lastIndexOf(".");
        if (lastDot != -1) {
            return originalName.substring(0, lastDot) + newExtension;
        } else {
            return originalName + newExtension;
        }
    }

    /**
     * removes the extrions of a filename like classic.yml
     * @param str
     * @return
     */
    public static String removeExtension(String str)
    {
        return str.substring(0, str.lastIndexOf('.'));
    }

    /**
     * return true if the folder is empty
     * @param folderPath
     * @return
     */
    public static boolean isFolderEmpty(String folderPath) {
        File file = new File(folderPath);
        if (!file.isDirectory()) {
            return true;
        }

        if (file.list().length > 0)  {
            //folder is not empty
            return false;
        }
        return true;
    }

    /**
     * copies a file form the .jar to the disk
     * @param in
     * @param file
     */
    public static void copyFile(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while((len=in.read(buf))>0){
                out.write(buf,0,len);
            }
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
