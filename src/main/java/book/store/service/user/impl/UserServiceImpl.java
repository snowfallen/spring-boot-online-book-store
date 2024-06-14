package book.store.service.user.impl;

import book.store.dto.user.UserRegistrationRequestDto;
import book.store.dto.user.UserResponseDto;
import book.store.exception.RegistrationException;
import book.store.mapper.UserMapper;
import book.store.model.Role;
import book.store.model.User;
import book.store.repository.role.RoleRepository;
import book.store.repository.user.UserRepository;
import book.store.service.user.UserService;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
class UserServiceImpl implements UserService {
    private static final String USER_ALREADY_EXIST = "User already exist!";
    private static final String CANT_FIND_ROLE_BY_NAME = "Can't find role by name";
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new RegistrationException(USER_ALREADY_EXIST);
        }

        Role userRole = roleRepository.findRoleByName(Role.RoleName.ROLE_USER)
                .orElseThrow(() -> new RegistrationException(CANT_FIND_ROLE_BY_NAME));
        Set<Role> defaultUserRoleSet = new HashSet<>();
        defaultUserRoleSet.add(userRole);

        User user = userMapper.toModel(requestDto);
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        user.setRoles(defaultUserRoleSet);
        return userMapper.toDto(userRepository.save(user));
    }
}
