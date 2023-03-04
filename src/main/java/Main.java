import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class Main {
    static List<FileType> fileTypes = new ArrayList<>();

    public static boolean checkMagicBytes(byte[] bytes, int[] magicBytes) {
        if (bytes.length != magicBytes.length) {
            return false;
        }
        for (int i = 0; i < magicBytes.length; i++) {
            if (Byte.toUnsignedInt(bytes[i]) != magicBytes[i]) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) throws IOException {
        FileType[] fileTypes = new FileType[6];

        // Taken from https://en.wikipedia.org/wiki/List_of_file_signatures
        fileTypes[0] = new FileType("png", new int[]{137, 80, 78, 71, 13, 10, 26, 10});
        fileTypes[1] = new FileType("gif", new int[]{71, 73, 70, 56, 57, 97});
        fileTypes[2] = new FileType("gif", new int[]{71, 73, 70, 56, 55, 97});
        fileTypes[3] = new FileType("jpg", new int[]{255, 216});
        fileTypes[4] = new FileType("webp", new int[]{82, 73, 70, 70});
        fileTypes[5] = new FileType("webm", new int[]{26, 69, 223, 163});

        System.out.println("Starting discord cache converter");

        short processedFiles = 0;
        short convertedFiles = 0;

        Path cachePath = Path.of("C:\\Users\\" + System.getProperty("user.name") + "\\AppData\\Roaming\\discord\\Cache");

        // Folder for converted files
        File convertedFolder = new File(cachePath + "\\converted");
        convertedFolder.mkdirs();

        // Get all files from the cache folder
        File[] listOfFiles = new File(cachePath.toUri()).listFiles();

        assert listOfFiles != null;

        // Go through all the cached files
        for (File file : listOfFiles) {
            if (file.isFile() && file.getName().startsWith("f_")) {
                try (FileInputStream in = new FileInputStream(file)) {
                    String ext = "";

                    for (FileType fileType : fileTypes) {
                        if(checkMagicBytes(in.readNBytes(fileType.getMagicBytes().length), fileType.getMagicBytes())) {
                            ext = fileType.getName();
                            break;
                        }
                    }

                    if (!ext.equals("")) {
                        Files.copy(file.toPath(), Path.of(cachePath + "\\converted\\" + file.getName() + ext), StandardCopyOption.REPLACE_EXISTING);
                        convertedFiles++;
                    }
                    processedFiles++;

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println("Converting completed. Converted " + convertedFiles + " files out of " + processedFiles + " files.");
    }
}