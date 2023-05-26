package com.example.apiStory.controller;

import com.example.apiStory.dto.request.*;
import com.example.apiStory.helper.ResponseObj;
import com.example.apiStory.service.AuthService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RequestMapping("/api/auth")
@RestController
@CrossOrigin
public class AuthController {
    @Autowired
    AuthService authService;

    @PostMapping("/register")
    @ApiOperation(value = "Đăng ký")
    public ResponseEntity register(@Valid @RequestBody RegisterRequest user) {
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(HttpStatus.OK.value(), true, "save user successfully", authService.regitser(user)));
    }

    @ApiOperation(value = "verify code")
    @PostMapping("/verify")
    public ResponseEntity verify(@RequestBody VerifyRequest verifyRequest, HttpServletRequest httpServletRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(HttpStatus.OK.value(), true, "verify successfully", authService.verify(verifyRequest.getCode(), httpServletRequest)));
    }

    @ApiOperation(value = "Đăng nhập")
    @PostMapping("/login")
    public ResponseEntity signIn(@Valid @RequestBody LoginRequest user) {
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(HttpStatus.OK.value(), true, "login successfully", authService.signIn(user)));

    }
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER', 'ROLE_AUTHOR')")
    @ApiOperation(value = "đăng xuất")
    @PostMapping("/logout")
    public ResponseEntity logout(@RequestBody LogoutRequest logoutRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(HttpStatus.OK.value(), true, "log out successfully", authService.logout(logoutRequest.getToken())));
    }

    @ApiOperation(value = "gửi lại mã code")
    @GetMapping("/resendCode")
    public ResponseEntity resendCode(HttpServletRequest httpServletRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(HttpStatus.OK.value(), true, "resend code successfully", authService.reSendCode(httpServletRequest)));
    }

    @PostMapping("/findEmail")
    public ResponseEntity findEmail(@RequestBody FindEmailRequest findEmailRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(HttpStatus.OK.value(), true, "Please check your email for password reset code", authService.findEmail(findEmailRequest.getEmail())));
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER', 'ROLE_AUTHOR')")
    @ApiOperation(value = "Đặt lại mật khẩu")
    @PostMapping("/resetPassword")
    public ResponseEntity resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest, HttpServletRequest httpServletRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(HttpStatus.OK.value(), true, "reset password successfully", authService.resetPassword(resetPasswordRequest.getPassword(), httpServletRequest)));

    }


    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
    @GetMapping("/whoami")
    public ResponseEntity whoAmI(HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseObj(HttpStatus.OK.value(), true, "get infor successfully",authService.whoAmI(request)));
    }


}
