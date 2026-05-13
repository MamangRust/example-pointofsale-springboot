package com.sanedge.pointofsale.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.sanedge.pointofsale.models.Role;
import com.sanedge.pointofsale.models.User;
import com.sanedge.pointofsale.repository.role.RoleQueryRepository;
import com.sanedge.pointofsale.repository.user.UserQueryRepository;
import com.sanedge.pointofsale.security.UserDetailsImpl;

import jakarta.transaction.Transactional;

@Service
public class UserDetailImplService implements UserDetailsService {
    @Autowired
    UserQueryRepository userRepository;

    @Autowired
    RoleQueryRepository roleRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

        List<Role> roles = roleRepository.findUserRoles(user.getUserId());
        return UserDetailsImpl.build(user, roles);
    }
}