package Diginamic.Hello.Securite;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class Utilisateur implements UserDetails {

    private String username;
    private String password; // stocké encodé (BCrypt)
    private List<Role> roles;

    public Utilisateur() {}

    public Utilisateur(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.roles = List.of(role);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
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
