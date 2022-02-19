package net.Duels.utility;

import java.io.File;

public class FileUtils {

    public static void checkDirectory(File file) {
        try {
            if (!file.exists()) {
                file.mkdirs();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
