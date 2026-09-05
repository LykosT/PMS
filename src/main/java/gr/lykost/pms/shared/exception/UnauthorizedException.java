package gr.lykost.pms.shared.exception;

public class UnauthorizedException extends ResourceGenericException {
    private static final String DEFAULT_CODE = "Unauthorized Access";

    public UnauthorizedException(String code, String message) {
        super(code + DEFAULT_CODE, message);
    }
}
