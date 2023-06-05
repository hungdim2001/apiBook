package com.example.apiStory.service;


import com.example.apiStory.commons.RoleEnum;
import com.example.apiStory.dto.request.LoginRequest;
import com.example.apiStory.dto.request.RegisterRequest;
import com.example.apiStory.dto.response.LoginResponse;
import com.example.apiStory.dto.response.RegisterResponse;
import com.example.apiStory.dto.response.WhoAmIResponse;
import com.example.apiStory.entity.Code;
import com.example.apiStory.entity.Token;
import com.example.apiStory.entity.User;
import com.example.apiStory.entity.UserRole;
import com.example.apiStory.exceptions.DuplicateException;
import com.example.apiStory.exceptions.InvalidLoginException;
import com.example.apiStory.exceptions.InvalidRefreshToken;
import com.example.apiStory.exceptions.NotFoundException;
import com.example.apiStory.repository.CodeRepository;
import com.example.apiStory.repository.TokenRepository;
import com.example.apiStory.repository.UserRepository;
import com.example.apiStory.repository.UserRoleRepository;
import com.example.apiStory.security.jwt.JwtUtils;
import com.example.apiStory.security.jwt.TokenType;
import com.example.apiStory.util.BaseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Date;

@Service
public class AuthService {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserRoleRepository userRoleRepo;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private MailService mailService;
    @Autowired
    private CodeRepository codeRepository;

    public RegisterResponse register(RegisterRequest user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new DuplicateException(HttpStatus.CONFLICT, "email have already taken");
        } else if (userRepository.existsByusername(user.getUsername())) {
            throw new DuplicateException(HttpStatus.CONFLICT, "username have already taken");
        }

        User newUser = User.builder()
                .email(user.getEmail())
                .password(encoder.encode(user.getPassword()))
                .username(user.getUsername())
                .status(false)
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
        User userResponse = userRepository.save(newUser);
        String code = BaseUtils.getAlphaNumericString(6);
        Code newCode = Code.builder().code(code).
                expiredTime(new Date().getTime() + 300000).userId(userResponse.getId())
                .build();
        codeRepository.save(newCode);
        UserRole userRole = new UserRole();
        if ("admin".equals(user.getRole())) {
            userRole.setUserId(userResponse.getId());
            userRole.setRole(RoleEnum.ADMIN.getRole());
        } else if ("author".equals(user.getRole())) {
            userRole.setUserId(userResponse.getId());
            userRole.setRole(RoleEnum.AUTHOR.getRole());
        } else {
            userRole.setUserId(userResponse.getId());
            userRole.setRole(RoleEnum.USER.getRole());
        }
        UserRole userRoleSave = userRoleRepo.save(userRole);

        try {
            mailService.sendSimpleEmail(user.getEmail(),
                    "Verify Email",
                    code);
        } catch (Exception e) {
            throw new DuplicateException(HttpStatus.CONFLICT, "can't send email");

        }

        return RegisterResponse.builder()
                .email(userResponse.getEmail())
                .id(userResponse.getId())
                .username(userResponse.getUsername())
                .fullName(userResponse.getLastName() + " " + userResponse.getFirstName())
                .avatarUrl(userResponse.getAvatarUrl())
                .firstName(userResponse.getFirstName())
                .lastName(userResponse.getLastName())
                .status(userResponse.getStatus())
                .role(userRoleSave.getRole())
                .build();
    }

    public String verify(String code, HttpServletRequest request) {
        String jwt = BaseUtils.parseJwt(request);
        Long id = Long.valueOf(jwtUtils.getIdFromJwtToken(jwt, true));
        Code codeFound = codeRepository.findByCode(code).orElseThrow(
                () -> new NotFoundException(HttpStatus.NOT_FOUND, "Code is invalid")
        );
        Long currentTime = new Date().getTime();
        if (currentTime > codeFound.getExpiredTime()) {
            throw new InvalidRefreshToken(HttpStatus.BAD_REQUEST, "code is expired");
        }
        userRepository.updateStatusUser(id);
        codeRepository.removeCode(id);
        return null;
    }

    public LoginResponse signIn(@Valid @RequestBody LoginRequest user) {
        Authentication authentication = null;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getAccount(), user.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            throw new InvalidLoginException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }

        User userResponse = userRepository.findAccount(user.getAccount());
        TokenType accessToken = jwtUtils.generateJwtToken(userResponse.getId(), true);
        TokenType refreshToken = jwtUtils.generateJwtToken(userResponse.getId(), false);
        Token token = Token.builder().token(refreshToken.getToken()).build();
        tokenRepository.save(token);
        return LoginResponse
                .builder()
                .accessToken(accessToken.getToken())
                .expiresIn(accessToken.getExpiresIn() - (new Date()).getTime())
                .refreshToken(refreshToken.getToken())
                .refreshExpiresIn(refreshToken.getExpiresIn() - (new Date()).getTime())
                .build();

    }

    public String logout(String token) {
        if (!tokenRepository.existsByToken(token)) {
            throw new InvalidRefreshToken(HttpStatus.NOT_FOUND, "Not Found token");
        }
        tokenRepository.removeToken(token);
        return null;
    }

    public String findEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new NotFoundException(HttpStatus.NOT_FOUND, "Email không tồn tại")
        );
        TokenType accessToken = jwtUtils.generateJwtToken(user.getId(), true);

        String message = " Xin chào bạn,\n" +
                "\n" +
                "Để khôi phục lại mật khẩu, xin nhấp vào " + "http://52.234.251.248:8080/auth/reset-password/" +
                user.getId() + "/" +
                accessToken.getToken() +
                " để tạo mật khẩu mới." +
                "Link này sẽ hết hạn trong 5 phút";
        try {

            mailService.sendSimpleEmail(user.getEmail(),
                    "Reset Password",
                    message, true);
        } catch (Exception e) {
            throw new DuplicateException(HttpStatus.CONFLICT, "can't send email");
        }
        return null;
    }

    public String reSendCode(HttpServletRequest request) {
        String jwt = BaseUtils.parseJwt(request);
        Long id = Long.valueOf(jwtUtils.getIdFromJwtToken(jwt, true));
        codeRepository.removeCode(id);
        User user = userRepository.findById(id).orElseThrow(
                () -> new NotFoundException(HttpStatus.NOT_FOUND, "Not user with id")
        );
        String code = BaseUtils.getAlphaNumericString(6);
        Code newCode = Code.builder().code(code).
                expiredTime(new Date().getTime() + 300000).userId(user.getId())
                .build();
        codeRepository.save(newCode);

        try {
            mailService.sendSimpleEmail(user.getEmail(),
                    "Verify Email",
                    code);
        } catch (Exception e) {
            throw new DuplicateException(HttpStatus.CONFLICT, "can't send email");
        }
        return null;
    }

    public String resetPassword(String password, HttpServletRequest request) {
        String jwt = BaseUtils.parseJwt(request);
        Long id = Long.valueOf(jwtUtils.getIdFromJwtToken(jwt, true));
        User user = userRepository.findById(id).orElseThrow(
                () -> new NotFoundException(HttpStatus.NOT_FOUND, "Not user with id")
        );
        String newPassword = encoder.encode(password);
        userRepository.updatePassword(newPassword, user.getId());
        return null;
    }

    public WhoAmIResponse whoAmI(HttpServletRequest request) {
        String jwt = BaseUtils.parseJwt(request);
        Long id = Long.valueOf(jwtUtils.getIdFromJwtToken(jwt, true));
        User userResponse = userRepository.getById(id);
        UserRole userRole = userRoleRepository.findByUserId(userResponse.getId()).orElseThrow(
                () -> new NotFoundException(HttpStatus.NOT_FOUND,"user id not found" )
        );
        return WhoAmIResponse.builder()
                .id(userResponse.getId())
                .avatarUrl(userResponse.getAvatarUrl())
                .fullName(userResponse.getLastName() + " " + userResponse.getFirstName())
                .role(userRole.getRole())
                .username(userResponse.getUsername())
                .firstName(userResponse.getFirstName())
                .lastName(userResponse.getLastName())
                .status(userResponse.getStatus())
                .email(userResponse.getEmail())
                .build();
    }
}
/*
    public LoginResponse oauth2SignUp(Oauth2Request user) {
        Optional<User> userFound = userRepository.findByEmail(user.getEmail());
        System.out.println("provider: " + userFound.get().getProvider() != null);
        if (userFound.isPresent() && userFound.get().getProvider() != null) {
            TokenType accessToken = jwtUtils.generateJwtToken(userFound.get().getId(), true);
            TokenType refreshToken = jwtUtils.generateJwtToken(userFound.get().getId(), false);
            Token token = Token.builder().token(refreshToken.getToken()).build();
            tokenRepository.save(token);
            userRepository.updateOauth2User(user.getFirstName(), user.getLastName(), user.getAvatarUrl(), user.getEmail());
            return LoginResponse
                    .builder()
                    .accessToken(accessToken.getToken())
                    .expiresIn(accessToken.getExpiresIn() - (new Date()).getTime())
                    .refreshToken(refreshToken.getToken())
                    .refreshExpiresIn(refreshToken.getExpiresIn() - (new Date()).getTime())
                    .build();
        } else if (userFound.isPresent() && userFound.get().getProvider() == null) {
            throw new InvalidLoginException(HttpStatus.UNAUTHORIZED, "Email is exist");
        }
        User newUser = User.builder().email(user.getEmail())
                .isActive(true)
                .provider(user.getAuthProvider())
                .avatarUrl(user.getAvatarUrl())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
        Role userRole = roleRepository.findByName(ERole.ROLE_USER).orElseThrow(() -> new RuntimeException("Error: Role user is not found."));
        newUser.setRole(userRole);
        userRepository.save(newUser);
        User userResponse = userRepository.findAccount(user.getEmail());
        TokenType accessToken = jwtUtils.generateJwtToken(userResponse.getId(), true);
        TokenType refreshToken = jwtUtils.generateJwtToken(userResponse.getId(), false);
        Token token = Token.builder().token(refreshToken.getToken()).build();
        tokenRepository.save(token);
        return LoginResponse
                .builder()
                .accessToken(accessToken.getToken())
                .expiresIn(accessToken.getExpiresIn() - (new Date()).getTime())
                .refreshToken(refreshToken.getToken())
                .refreshExpiresIn(refreshToken.getExpiresIn() - (new Date()).getTime())
                .build();

    }



    public WhoAmIResponse whoAmI(HttpServletRequest request) {
        String jwt = BaseUtils.parseJwt(request);
        Long id = Long.valueOf(jwtUtils.getIdFromJwtToken(jwt, true));
        User userResponse = userRepository.getById(id);
        return WhoAmIResponse.builder()
                .id(userResponse.getId())
                .avatarUrl(userResponse.getAvatarUrl())
                .fullName(userResponse.getLastName() + " " + userResponse.getFirstName())
                .role(userResponse.getRole().getName().toString())
                .username(userResponse.getusername())
                .firstName(userResponse.getFirstName())
                .lastName(userResponse.getLastName())
                .isActive(userResponse.isActive())
                .email(userResponse.getEmail())
                .build();
    }
}
*/