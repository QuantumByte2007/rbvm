package ma.dxc.app.execption;




public class FileProcessedException extends RuntimeException {
    public FileProcessedException(){
        super("File already processed");
    }
}
