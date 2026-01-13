package gr.lykost.pms.core.exceptions;

public class AppServerException extends ResourceGenericException {

    public AppServerException(String code, String message) {
        super(code, message);
    }
}
