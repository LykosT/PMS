package gr.lykost.pms.shared.exception;

public class AppServerException extends ResourceGenericException {

    public AppServerException(String code, String message) {
        super(code, message);
    }
}
