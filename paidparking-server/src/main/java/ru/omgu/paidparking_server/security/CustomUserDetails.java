package ru.omgu.paidparking_server.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.omgu.paidparking_server.entity.UserEntity;

import java.util.Collection;
import java.util.stream.Collectors;


public class CustomUserDetails implements UserDetails {
    private final UserEntity user;

    public CustomUserDetails(UserEntity user) {
        this.user = user;
    }

    @Override
    public String getUsername() {
        return user.getPhoneNumber(); // Номер телефона используется как уникальный идентификатор
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getRole().name()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Аккаунт всегда активен
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Аккаунт не заблокирован
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Срок действия пароля не истек
    }

    @Override
    public boolean isEnabled() {
        return true; // Аккаунт включен
    }

    public Long getId() {
        return user.getId();
    }
}
