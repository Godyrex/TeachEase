package org.example.teacheaseapplication.services;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.example.teacheaseapplication.dto.requests.LoginRequest;
import org.example.teacheaseapplication.dto.requests.SignupRequest;
import org.example.teacheaseapplication.dto.responses.UserResponse;
import org.example.teacheaseapplication.models.CodeType;
import org.example.teacheaseapplication.models.CodeVerification;
import org.example.teacheaseapplication.models.User;
import org.example.teacheaseapplication.repositories.UserRepository;
import org.example.teacheaseapplication.security.jwt.JWTUtils;
import org.example.teacheaseapplication.utils.CookieUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.Instant;
import java.util.NoSuchElementException;

@Service
@Slf4j
public class AuthServiceImpl implements IAuthService {
    public static final String USER_NOT_FOUND = "User not found : ";
    private final UserRepository userRepository;
    private final IRefreshTokenService iRefreshTokenService;
    private final JWTUtils jwtUtils;
    private final PasswordEncoder encoder;
    private final CookieUtil cookieUtil;
    private final AuthenticationManager authenticationManager;
    private final IMailService mailService;
    private final ICodeVerificationService codeVerificationService;
    @Value("${Security.app.jwtExpirationMs}")
    private long jwtExpirationMs;
    @Value("${Security.app.refreshExpirationMs}")
    private long refreshExpirationMs;
    @Value("${Security.app.refreshRememberMeExpirationMs}")
    private long refreshRememberMeExpirationMs;

    public AuthServiceImpl(UserRepository userRepository, IRefreshTokenService iRefreshTokenService, JWTUtils jwtUtils, PasswordEncoder encoder, CookieUtil cookieUtil, AuthenticationManager authenticationManager, IMailService mailService, ICodeVerificationService codeVerificationService) {
        this.userRepository = userRepository;
        this.iRefreshTokenService = iRefreshTokenService;
        this.jwtUtils = jwtUtils;
        this.encoder = encoder;
        this.cookieUtil = cookieUtil;
        this.authenticationManager = authenticationManager;
        this.mailService = mailService;
        this.codeVerificationService = codeVerificationService;
    }

    @Override
    public ResponseEntity<HttpStatus> logout(String email, HttpServletRequest request, HttpServletResponse response) {
        log.info("Logging out user");
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND + email));
            iRefreshTokenService.deleteUserTokens(user);
            log.info("User found");
            new SecurityContextLogoutHandler().logout(request, response, null);
            response.addHeader(HttpHeaders.SET_COOKIE, cookieUtil.createAccessTokenCookie("accessToken", 0L).toString());
            log.info("Logout: Access Token removed");
            response.addHeader(HttpHeaders.SET_COOKIE, cookieUtil.createRefreshTokenCookie("refreshToken", 0L).toString());
            log.info("Logout :Refresh Token removed");
            log.info("User logged out");
            return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<HttpStatus> logout(HttpServletRequest request, HttpServletResponse response) {
        new SecurityContextLogoutHandler().logout(request, response, null);
        response.addHeader(HttpHeaders.SET_COOKIE, cookieUtil.createAccessTokenCookie("accessToken", 0L).toString());
        log.info("Logout: Access Token removed");
        response.addHeader(HttpHeaders.SET_COOKIE, cookieUtil.createRefreshTokenCookie("refreshToken", 0L).toString());
        log.info("Logout :Refresh Token removed");
        log.info("User logged out");
        return ResponseEntity.ok().build();
    }

    boolean isUserAuthenticated(){
        if (SecurityContextHolder.getContext().getAuthentication() != null &&
                SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && authentication.isAuthenticated()) {
                if (authentication.getPrincipal() instanceof UserDetails userDetails) {
                    String username = userDetails.getUsername();
                    log.info("Authenticated user's username: " + username);
                    return true;
                }
                return false;
            }
            log.info("User not authenticated");
            return false;
        }
        return false;
    }
    @Override
    public ResponseEntity<UserResponse> authenticateUser(LoginRequest loginRequest, @NonNull HttpServletResponse response) {
        log.info("Authenticating user");
        if(isUserAuthenticated()){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail().toLowerCase(), loginRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            User userDetails = (User) authentication.getPrincipal();
            setHeaders(response,userDetails,loginRequest.isRememberMe());
            userRepository.save(userDetails);

            log.info("User authenticated successfully");
            return ResponseEntity.ok(UserResponse.builder()
                    .email(userDetails.getEmail())
                    .name(userDetails.getName())
                    .lastname(userDetails.getLastname())
                    .image(userDetails.getImage())
                    .verified(userDetails.getVerified())
                    .ban(userDetails.getBan())
                    .role(userDetails.getRole() != null ? userDetails.getRole().name() : null)
                    .build());
        } catch (DisabledException e) {
            log.error("User not verified");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (LockedException e) {
            log.error("User account locked");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (AuthenticationException e) {
            log.error("Invalid email or password");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    void setHeaders(HttpServletResponse response,User userDetails,boolean rememberMe){
        iRefreshTokenService.deleteUserTokens(userDetails);
        response.addHeader(HttpHeaders.SET_COOKIE, cookieUtil.createAccessTokenCookie(jwtUtils.generateJwtToken(userDetails.getEmail()), jwtExpirationMs).toString());
        response.addHeader(HttpHeaders.SET_COOKIE, cookieUtil.createRefreshTokenCookie(
                iRefreshTokenService.createRefreshToken(userDetails.getEmail(), rememberMe ? refreshRememberMeExpirationMs : refreshExpirationMs).getToken()
                , rememberMe ? refreshRememberMeExpirationMs : refreshExpirationMs).toString());
    }

    @Override
    public ResponseEntity<HttpStatus> saveUser(SignupRequest signupRequest) {
        log.info(signupRequest.toString());
        if(userRepository.existsByEmail(signupRequest.getEmail())){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        User user = User.builder()
                .email(signupRequest.getEmail())
                .password(encoder.encode(signupRequest.getPassword()))
                .name(signupRequest.getName())
                .lastname(signupRequest.getLastname())
                .verified(true)
                .ban(false)
                .build();
        userRepository.save(user);
        mailService.sendAccountCreatedEmail(user);
      //  sendVerificationCode(user.getEmail());
        return ResponseEntity.ok().build();
    }


    @Override
    public ResponseEntity<HttpStatus> sendVerificationCode(String email) {
        if(userRepository.existsByEmail(email)){
            CodeVerification codeVerification = codeVerificationService.saveCode(
                    CodeType.EMAIL_VERIFICATION,
                    email,
                    codeVerificationService.generateCode(),
                    Instant.now().plusSeconds(3600*24)
            );
            mailService.sendConfirmationEmail(userRepository.findByEmail(email).orElseThrow(()-> new NoSuchElementException("User not found")),codeVerification);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @Override
    public ResponseEntity<HttpStatus> sendPasswordResetCode(String email) {
        if(userRepository.existsByEmail(email)){
            CodeVerification codeVerification = codeVerificationService.saveCode(
                    CodeType.PASSWORD_RESET,
                    email,
                    codeVerificationService.generateCode(),
                    Instant.now().plusSeconds(3600)
            );
            mailService.sendPasswordResetEmail(userRepository.findByEmail(email).orElseThrow(()-> new NoSuchElementException("User not found")),codeVerification);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @Override
    public ResponseEntity<HttpStatus> verifyEmail(String code) {
        log.info("Verifying email");
        log.info("Code: "+code);
        CodeVerification codeVerification = codeVerificationService.verifyCode(code);
        if(codeVerification.getEmail()!= null){
            log.info("Email verified");
            User user = userRepository.findByEmail(codeVerification.getEmail()).orElseThrow(()-> new NoSuchElementException("User not found"));
            user.setVerified(true);
            userRepository.save(user);
            codeVerificationService.deleteCode(codeVerification.getEmail(),CodeType.EMAIL_VERIFICATION);
            return ResponseEntity.ok().build();
        }
        log.info("Invalid verification code");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @Override
    public ResponseEntity<UserResponse> checkAuth(Principal principal) {
        log.info("Checking user authentication");
        if(isUserAuthenticated()){
            User user = userRepository.findByEmail(principal.getName()).orElseThrow(()-> new NoSuchElementException("User not found"));

            log.info("User authenticated successfully");
            return ResponseEntity.ok(UserResponse.builder()
                    .email(user.getEmail())
                    .name(user.getName())
                    .lastname(user.getLastname())
                    .image(user.getImage())
                    .verified(user.getVerified())
                    .ban(user.getBan())
                    .role(user.getRole() != null ? user.getRole().name() : null)
                    .build());
        }
        log.info("User not authenticated");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}
