package com.swms.service;

import com.swms.model.Citizen;
import com.swms.repository.CitizenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private CitizenRepository citizenRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Citizen citizen = citizenRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Citizen not found with email: " + email));

        return new org.springframework.security.core.userdetails.User(
                citizen.getEmail(),
                citizen.getPassword(),
                citizen.isEnabled(),
                true,
                true,
                true,
                getAuthorities(citizen)
        );
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Citizen citizen) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + citizen.getUserType()));
        return authorities;
    }
}