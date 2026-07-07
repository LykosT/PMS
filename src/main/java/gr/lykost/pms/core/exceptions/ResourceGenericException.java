package gr.lykost.pms.core.exceptions;

public class ResourceGenericException extends RuntimeException {
    private final String code;

    public ResourceGenericException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
