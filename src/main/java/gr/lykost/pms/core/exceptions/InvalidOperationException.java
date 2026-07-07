package gr.lykost.pms.core.exceptions;

public class InvalidOperationException extends ResourceGenericException {
    private static final String DEFAULT_CODE = "Invalid Argument Operation";

    public InvalidOperationException(String code, String message) {
        super(code + DEFAULT_CODE, message);
    }
}
