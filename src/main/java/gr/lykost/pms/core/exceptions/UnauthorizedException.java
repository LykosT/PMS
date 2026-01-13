package gr.lykost.pms.core.exceptions;

public class UnauthorizedException extends ResourceGenericException {
    private static final String DEFAULT_CODE = "Unauthorized Access";

    public UnauthorizedException(String code, String message) {
        super(code + DEFAULT_CODE, message);
    }
}
