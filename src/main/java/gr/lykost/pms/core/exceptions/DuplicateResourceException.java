package gr.lykost.pms.core.exceptions;

public class DuplicateResourceException extends ResourceGenericException {
    private static final String DEFAULT_CODE = "Duplicate Resource";

    public DuplicateResourceException(String code, String message) {
        super(code + DEFAULT_CODE, message);
    }
}
