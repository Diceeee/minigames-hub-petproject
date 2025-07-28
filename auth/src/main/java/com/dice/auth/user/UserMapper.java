package com.dice.auth.user;

import com.dice.auth.user.api.dto.UserResponse;
import com.dice.auth.user.domain.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        unmappedSourcePolicy = ReportingPolicy.ERROR
)
public interface UserMapper {

    @Mapping(target = "authority", ignore = true)
    User mapUserEntity(UserEntity entity);

    @BeanMapping(ignoreUnmappedSourceProperties = {"accountNonExpired", "accountNonLocked", "credentialsNonExpired", "enabled", "registered"})
    UserEntity mapUserToEntity(User user);

    @BeanMapping(ignoreUnmappedSourceProperties = {"accountNonExpired", "accountNonLocked", "credentialsNonExpired", "enabled", "registered", "password", "createdAt", "googleId", "facebookId"})
    UserResponse mapToResponse(User user);

    default Collection<? extends GrantedAuthority> mapAuthoritiesFromListOfStrings(List<String> authorities) {
        return authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    default List<String> mapAuthoritiesToListOfStrings(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
    }

    default Set<String> mapAuthoritiesToSetOfStrings(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
    }
}
