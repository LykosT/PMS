package gr.lykost.pms.shared;

import java.util.Map;

/**
 * Standard error body returned by the REST API.
 */
public record ApiErrorDTO(
        String code,
        String message,
        Map<String, String> fieldErrors
) {
    public static ApiErrorDTO of(String code, String message) {
        return new ApiErrorDTO(code, message, null);
    }
}
