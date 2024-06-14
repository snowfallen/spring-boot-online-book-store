package book.store.security;

import book.store.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private static final String CANT_FIND_USER_BY_EMAIL = "Can't find the user by email: ";
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findUserByEmail(username).orElseThrow(
                () -> new UsernameNotFoundException(CANT_FIND_USER_BY_EMAIL + username)
        );
    }
}
