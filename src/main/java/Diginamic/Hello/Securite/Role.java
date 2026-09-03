package Diginamic.Hello.Securite;

import org.springframework.security.core.GrantedAuthority;

public class Role implements GrantedAuthority {

    private String nom; // ex: "ROLE_USER", "ROLE_ADMIN"

    public Role() {}

    public Role(String nom) {
        this.nom = nom;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    @Override
    public String getAuthority() {
        return nom;
    }
}
