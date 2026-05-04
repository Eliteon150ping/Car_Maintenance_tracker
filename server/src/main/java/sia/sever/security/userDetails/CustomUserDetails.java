package sia.sever.security.userDetails;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import sia.sever.entity.User;
import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    // Fields
    private final User user;

    // Constructor
    public CustomUserDetails(User user){
        this.user = user;
    }

    // Getters
    // No authorities yet but has to be implemented regardless so return an empty list to be safe
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){
        return List.of();
    }

    @Override
    public String getUsername(){
        return user.getEmail();
    }

    @Override
    public String getPassword(){
        return user.getPassword();
    }

    public Long getUserId(){
        return user.getId();
    }

    // These have to be implemented to avoid potential issues later so just return true for all of them until
    // needed for anything specific
    @Override
    public boolean isAccountNonExpired(){return true;}
    @Override
    public boolean isAccountNonLocked(){return true;}
    @Override
    public boolean isCredentialsNonExpired(){return true;}
    @Override
    public boolean isEnabled(){return true;}
}
