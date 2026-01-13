package gr.lykost.pms.core.exceptions;

public class ResourceNotFoundException extends ResourceGenericException {
    private static final String DEFAULT_CODE = "Resource Not Found";

    public ResourceNotFoundException(String code, String message) {
        super(code + DEFAULT_CODE , message);
    }
}
