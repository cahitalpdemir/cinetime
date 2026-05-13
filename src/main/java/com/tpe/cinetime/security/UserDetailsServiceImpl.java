package com.tpe.cinetime.security;

import com.tpe.cinetime.entity.User;
import com.tpe.cinetime.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    /*
      -Spring Security login sırasında bu methodu cagirir.
      -Biz username parametresi olarak email kullaniyoruz.
     */

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                //Optional boşsa direkt exception fırlat
                .orElseThrow(() -> new UsernameNotFoundException(
                   "User not found with email: " + email
                ));

        //Tüm dönüşüm işlemini build() ile yapiliyor
        return UserDetailsImpl.build(user);
    }


}
