package server.exception;

public class WrongArgumentException extends Exception {

    public WrongArgumentException(String data) {
        super(data);
    }
}
