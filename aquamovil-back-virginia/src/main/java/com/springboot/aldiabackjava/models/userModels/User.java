package com.springboot.aldiabackjava.models.userModels;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.springboot.aldiabackjava.models.userModels.permisos.Permiso;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users", uniqueConstraints = {@UniqueConstraint(columnNames = {"email"})})
public class User implements UserDetails {
    @Id
    @Column(name = "id_user", nullable = false)
    private Long idUser;

    @Column(nullable = true)
    private String username;

    @JsonIgnore
    @Column(nullable = true)
    private String password;

    private String email;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name ="fk_id_cargo", referencedColumnName = "id_cargo")
    private Cargo cargo;

    @Column(name="name")
    private String name;

    @Column(name="profile_picture")
    private String profilePicture;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(cargo.getNombreCargo()));
        return authorities;
    }

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Permiso> permisos;

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
