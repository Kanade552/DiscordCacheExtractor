public class FileType {
    private final String name;
    private final int[] magicBytes;

    public String getName() {
        return name;
    }

    public int[] getMagicBytes() {
        return magicBytes;
    }

    public FileType(String name, int[] magicBytes) {
        this.name = name;
        this.magicBytes = magicBytes;
    }
}