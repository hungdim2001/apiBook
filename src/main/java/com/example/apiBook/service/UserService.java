package com.example.apiBook.service;

import com.example.apiBook.repository.UserRepository;
import com.example.apiBook.security.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    private PasswordEncoder encoder;
/*
    public String updateProfile(ProfileRequest profileRequest, HttpServletRequest httpServletRequest) {
        String jwt = BaseUtils.parseJwt(httpServletRequest);
        String folder = "avatar";
        Long id = Long.valueOf(jwtUtils.getIdFromJwtToken(jwt, true));
        User user = userRepository.findById(id).orElseThrow(() ->
                new NotFoundException(HttpStatus.NOT_FOUND, "Not  User with " + id)
        );
        if (profileRequest.getImage() == null) {
            userRepository.updateProfileUser(user.getId(), profileRequest.getFirstName(), profileRequest.getLastName());
            return null;
        }

        if (user.getAvatarUrl()!=null) {
            String fileName = user.getAvatarUrl().substring(user.getAvatarUrl().lastIndexOf('/'));
            fileName = folder + fileName;
            uploadFileService.deleteFile(fileName);
            String image = uploadFileService.uploadFile(profileRequest.getImage(), profileRequest.getImage().getOriginalFilename(), folder);
            userRepository.updateProfileUser(user.getId(), profileRequest.getFirstName(), profileRequest.getLastName(), image);
            return null;
        }

        String image = uploadFileService.uploadFile(profileRequest.getImage(), profileRequest.getImage().getOriginalFilename(), folder);
        userRepository.updateProfileUser(user.getId(), profileRequest.getFirstName(), profileRequest.getLastName(), image);
        return null;
    }

    public String updatePassword(PasswordUpdate passwordUpdate, HttpServletRequest httpServletRequest) {
        String jwt = BaseUtils.parseJwt(httpServletRequest);
        Long id = Long.valueOf(jwtUtils.getIdFromJwtToken(jwt, true));
        User user = userRepository.findById(id).orElseThrow(() ->
                new NotFoundException(HttpStatus.NOT_FOUND, "Not  User with " + id)
        );
        boolean isValidPassword = encoder.matches(passwordUpdate.getOldPassword(), user.getPassword());
        if (!isValidPassword) {
            throw new InvalidLoginException(HttpStatus.UNAUTHORIZED, "Mật khẩu vừa nhập không đúng ");
        }
        String newPassword = encoder.encode(passwordUpdate.getNewPassword());
        userRepository.updatePassword(user.getId(), newPassword);
        return null;
    }*/


}
