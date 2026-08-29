package ma.dxc.app.execption;

public class FileNotUploaded extends RuntimeException {
    public FileNotUploaded(String message) {
        super(message);
    }
}
