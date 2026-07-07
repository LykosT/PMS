package gr.lykost.pms.controller;

import gr.lykost.pms.authentication.CustomUserDetails;
import gr.lykost.pms.dto.LoginRequestDTO;
import gr.lykost.pms.dto.readonlydto.AuthUserDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;

    private final SecurityContextHolderStrategy securityContextHolderStrategy =
            SecurityContextHolder.getContextHolderStrategy();

    @PostMapping("/login")
    public AuthUserDTO login(@Valid @RequestBody LoginRequestDTO loginRequest,
                             HttpServletRequest request, HttpServletResponse response) {

        Authentication authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(
                        loginRequest.username(), loginRequest.password()));

        // Protect against session fixation, then persist the authentication in the session.
        HttpSession session = request.getSession(false);
        if (session != null) {
            request.changeSessionId();
        }

        SecurityContext context = securityContextHolderStrategy.createEmptyContext();
        context.setAuthentication(authentication);
        securityContextHolderStrategy.setContext(context);
        securityContextRepository.saveContext(context, request, response);

        return toAuthUser((CustomUserDetails) authentication.getPrincipal());
    }

    @GetMapping("/me")
    public AuthUserDTO currentUser(@AuthenticationPrincipal CustomUserDetails principal) {
        return toAuthUser(principal);
    }

    private AuthUserDTO toAuthUser(CustomUserDetails principal) {
        return new AuthUserDTO(
                principal.getUsername(),
                principal.fullName(),
                principal.systemRole()
        );
    }
}
