package ru.kurs.userapp.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import ru.kurs.userapp.entity.UserEntity;
import ru.kurs.userapp.mapper.UserMapper;
import ru.kurs.userapp.repository.UserRepository;
import ru.kurs.userapp.rest.dto.UserDTO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final JdbcTemplate jdbcTemplate;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(JdbcTemplate jdbcTemplate, UserRepository userRepository, UserMapper userMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        if (userDTO == null || !StringUtils.hasText(userDTO.getName()) || !StringUtils.hasText(userDTO.getPassword())) {
            throw new IllegalArgumentException("User data cannot be null and name and password are required");
        }

        UserEntity userEntity = userMapper.toEntity(userDTO);
        userEntity.setId(UUID.randomUUID());
        userEntity.setPasswordHash(userMapper.hashPassword(userDTO.getPassword()));
        userEntity.setDeleted(false);
        userEntity.setCreatedAt(LocalDateTime.now());

        UserEntity savedEntity = userRepository.save(userEntity);
        return userMapper.toDTO(savedEntity);
    }

    @Override
    public UserDTO getUserById(UUID id) {
        if (id == null) {
            return null;
        }

        return userRepository.findById(id)
                .filter(user -> !Boolean.TRUE.equals(user.getDeleted()))
                .map(userMapper::toDTO)
                .orElse(null);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .filter(user -> !Boolean.TRUE.equals(user.getDeleted()))
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO updateUser(UUID id, UserDTO userDTO) {
        if (id == null || userDTO == null) {
            return null;
        }

        return userRepository.findById(id)
                .filter(user -> !Boolean.TRUE.equals(user.getDeleted()))
                .map(user -> {
                    if (StringUtils.hasText(userDTO.getName())) {
                        user.setLogin(userDTO.getName());
                    }
                    UserEntity savedUser = userRepository.save(user);
                    return userMapper.toDTO(savedUser);
                })
                .orElse(null);
    }

    @Override
    public void delete(UUID id) {
        if (id == null) {
            return;
        }
        userRepository.setDeleted(id);
    }

    @Override
    public UserDTO getUserByLoginAndPassword(String login, String password) {
        var hashPassword = userMapper.hashPassword(password);
        var users = jdbcTemplate.query("SELECT id, login FROM users u WHERE u.login=? AND u.password_hash=?", new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setString(1, login);
                ps.setString(2, hashPassword);
            }
        }, new RowMapper<UserDTO>() {
            @Override
            public UserDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
                UserDTO userDTO = new UserDTO();
                userDTO.setId(UUID.fromString(rs.getString(2))); //cпойлер: здесь специально допущена ошибка
                userDTO.setName(rs.getString("login"));
                return userDTO;
            }
        });
        return !CollectionUtils.isEmpty(users) ? users.get(0) : null;
    }
}