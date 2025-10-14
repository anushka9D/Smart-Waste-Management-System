package com.swms.service;

import com.swms.model.Citizen;
import com.swms.model.CityAuthority;
import com.swms.model.Driver;
import com.swms.model.User;
import com.swms.model.WasteCollectionStaff;
import com.swms.repository.CitizenRepository;
import com.swms.repository.CityAuthorityRepository;
import com.swms.repository.DriverRepository;
import com.swms.repository.WasteCollectionStaffRepository;
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
    
    @Autowired
    private CityAuthorityRepository cityAuthorityRepository;
    
    @Autowired
    private DriverRepository driverRepository;
    
    @Autowired
    private WasteCollectionStaffRepository wasteCollectionStaffRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Try to find citizen
        Citizen citizen = citizenRepository.findByEmail(email).orElse(null);
        if (citizen != null) {
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
        
        // Try to find city authority
        CityAuthority cityAuthority = cityAuthorityRepository.findByEmail(email).orElse(null);
        if (cityAuthority != null) {
            return new org.springframework.security.core.userdetails.User(
                    cityAuthority.getEmail(),
                    cityAuthority.getPassword(),
                    cityAuthority.isEnabled(),
                    true,
                    true,
                    true,
                    getAuthorities(cityAuthority)
            );
        }
        
        // Try to find driver
        Driver driver = driverRepository.findByEmail(email).orElse(null);
        if (driver != null) {
            return new org.springframework.security.core.userdetails.User(
                    driver.getEmail(),
                    driver.getPassword(),
                    driver.isEnabled(),
                    true,
                    true,
                    true,
                    getAuthorities(driver)
            );
        }
        
        // Try to find waste collection staff
        WasteCollectionStaff wasteCollectionStaff = wasteCollectionStaffRepository.findByEmail(email).orElse(null);
        if (wasteCollectionStaff != null) {
            return new org.springframework.security.core.userdetails.User(
                    wasteCollectionStaff.getEmail(),
                    wasteCollectionStaff.getPassword(),
                    wasteCollectionStaff.isEnabled(),
                    true,
                    true,
                    true,
                    getAuthorities(wasteCollectionStaff)
            );
        }
        
        throw new UsernameNotFoundException("User not found with email: " + email);
    }

    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        
        // Determine role based on the actual class type
        if (user instanceof Citizen) {
            authorities.add(new SimpleGrantedAuthority("ROLE_CITIZEN"));
        } else if (user instanceof CityAuthority) {
            authorities.add(new SimpleGrantedAuthority("ROLE_CITY_AUTHORITY"));
        } else if (user instanceof Driver) {
            authorities.add(new SimpleGrantedAuthority("ROLE_DRIVER"));
        } else if (user instanceof WasteCollectionStaff) {
            authorities.add(new SimpleGrantedAuthority("ROLE_WASTE_COLLECTION_STAFF"));
        }
        
        return authorities;
    }
}