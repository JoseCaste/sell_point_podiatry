package com.podiatry.services;

import com.podiatry.model.Role;
import com.podiatry.model.User;
import com.podiatry.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServices implements UserDetailsService {

    @Autowired
    private BCryptPasswordEncoder encoder;
    @Autowired
    private UserRepository userRepository;

    public User save(User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        user.setRoles(Arrays.asList(new Role("ROLE_USER")));
        return userRepository.save(user);
    }

    public List<User> findAll(){
        return this.userRepository.findAll();
    }

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        final Optional<User>optionalUseruser= userRepository.findByUserName(userName);
        if(optionalUseruser.isPresent()){

            User user= optionalUseruser.get();
            return new org.springframework.security.core.userdetails.User(user.getUserName(),user.getPassword(),mapAutorities(user.getRoles()));
        }else
            throw  new UsernameNotFoundException("Crendenciales incorrectas");
    }
    private Collection<? extends GrantedAuthority> mapAutorities(Collection<Role> roles){
        return roles.stream().map(role-> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
    }
}
