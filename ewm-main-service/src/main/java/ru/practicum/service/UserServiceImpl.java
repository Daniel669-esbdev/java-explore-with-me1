package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.NewUserRequest;
import ru.practicum.dto.UserDto;
import ru.practicum.model.User;
import ru.practicum.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto addUser(NewUserRequest newUserRequest) {
        log.info("Adding new user: name={}, email={}", newUserRequest.getName(), newUserRequest.getEmail());
        User user = User.builder()
                .name(newUserRequest.getName())
                .email(newUserRequest.getEmail())
                .build();
        User savedUser = userRepository.save(user);
        log.info("User saved with id={}", savedUser.getId());
        return toUserDto(savedUser);
    }

    @Override
    public List<UserDto> getUsers(List<Long> ids, int from, int size) {
        log.info("Fetching users: ids={}, from={}, size={}", ids, from, size);
        PageRequest page = PageRequest.of(from / size, size);
        List<User> users;
        if (ids == null || ids.isEmpty()) {
            users = userRepository.findAll(page).getContent();
        } else {
            users = userRepository.findAllByIdIn(ids, page);
        }
        log.info("Found {} users", users.size());
        return users.stream().map(this::toUserDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        log.info("Deleting user with id={}", userId);
        userRepository.deleteById(userId);
        log.info("User with id={} deleted", userId);
    }

    private UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}