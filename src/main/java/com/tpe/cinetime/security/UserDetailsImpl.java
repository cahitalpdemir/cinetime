package com.tpe.cinetime.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tpe.cinetime.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;



@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsImpl implements UserDetails {

    private Long id;

    private String name;

    private String surname;

    private String username;

    @JsonIgnore
    private String password;

    private Boolean builtIn;

    private Collection<? extends GrantedAuthority> authorities;

    //User entity'den UserDetailsImpl üreten static factory method
    public static UserDetailsImpl build(User user){

        //Kullanicin tek rolünü SpringSecurity'nin anlayacagi GrantedAuthority'e ceviriyoruz
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        String role = user.getRole().getRoleName().name();
        grantedAuthorities.add(new SimpleGrantedAuthority(role));

        UserDetailsImpl userDetails = new UserDetailsImpl();
        userDetails.id = user.getId();
        userDetails.name = user.getName();
        userDetails.surname = user.getSurname();
        userDetails.username = user.getEmail();
        userDetails.password = user.getPassword();
        userDetails.builtIn = user.getBuiltIn();
        userDetails.authorities = grantedAuthorities;

       return userDetails;

    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){
        return authorities;
    }

    @Override
    public String getPassword(){
        return password;
    }

    @Override
    public String getUsername(){
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


}
