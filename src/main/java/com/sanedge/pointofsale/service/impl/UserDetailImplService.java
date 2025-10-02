package com.sanedge.pointofsale.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.sanedge.pointofsale.models.User;
import com.sanedge.pointofsale.repository.user.UserQueryRepository;
import com.sanedge.pointofsale.security.UserDetailsImpl;

import jakarta.transaction.Transactional;

@Service
public class UserDetailImplService implements UserDetailsService {
    @Autowired
    UserQueryRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

        return UserDetailsImpl.build(user);
    }
}