package com.example.apiStory.security.service;

import com.example.apiStory.entity.User;
import com.example.apiStory.entity.UserRole;
import com.example.apiStory.exceptions.NotFoundException;
import com.example.apiStory.repository.UserRepository;
import com.example.apiStory.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserRoleRepository userRoleRepository;
    @Override
    public UserDetails loadUserByUsername(String account) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameOrEmail(account).orElseThrow(
                () -> new NotFoundException(HttpStatus.NOT_FOUND,"username or password is incorrect" )
        );
        UserRole role = userRoleRepository.findByUserId(user.getId()).orElseThrow(
                () -> new NotFoundException(HttpStatus.NOT_FOUND,"role not found" )
        );
        return UserDetailsImpl.build(user,role.getRole());
    }
    public UserDetails loadUserById(Long id) throws UsernameNotFoundException {
        User user = userRepository.findById(id).orElseThrow(
                () -> new NotFoundException(HttpStatus.NOT_FOUND,"User Not Found with id: " + id)
        );
        UserRole role = userRoleRepository.findByUserId(user.getId()).orElseThrow(
                () -> new NotFoundException(HttpStatus.NOT_FOUND,"role not found" )
        );
        return UserDetailsImpl.build(user, role.getRole());
    }
}
