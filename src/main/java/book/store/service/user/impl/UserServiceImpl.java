package book.store.service.user.impl;

import book.store.dto.user.UserRegistrationRequestDto;
import book.store.dto.user.UserResponseDto;
import book.store.exception.RegistrationException;
import book.store.mapper.UserMapper;
import book.store.model.User;
import book.store.repository.user.UserRepository;
import book.store.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
class UserServiceImpl implements UserService {
    private static final String USER_ALREADY_EXIST = "User already exist!";
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new RegistrationException(USER_ALREADY_EXIST);
        }
        User user = userMapper.toModel(requestDto);
        return userMapper.toDto(userRepository.save(user));
    }
}
