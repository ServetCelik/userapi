package com.twix.userapi;

import com.twix.userapi.business.UserServiceImp;
import com.twix.userapi.business.exceptions.UserNameAlreadyExistsException;
import com.twix.userapi.business.exceptions.UserNotExistException;
import com.twix.userapi.controller.CreateUserRequest;
import com.twix.userapi.repository.UserEntity;
import com.twix.userapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImpTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private RabbitTemplate rabbitTemplate;

	@Mock
	private DirectExchange exchange;

	@InjectMocks
	private UserServiceImp userService;

	private UserEntity user;
	private UserEntity followUser;

	@BeforeEach
	void setUp() {
		user = UserEntity.builder().id(1L).userName("testUser").password("password").build();
		followUser = UserEntity.builder().id(2L).userName("followUser").password("password").build();
	}

	@Test
	void createUser_success() {
		CreateUserRequest request = new CreateUserRequest("testUser", "password");
		when(userRepository.existsByUserName(request.getUserName())).thenReturn(false);
		when(userRepository.save(any(UserEntity.class))).thenReturn(user);
		when(exchange.getName()).thenReturn("defaultExchange"); // Mock exchange name

		Long userId = userService.createUser(request);

		assertNotNull(userId);
		assertEquals(user.getId(), userId);
		verify(userRepository).save(any(UserEntity.class));
		verify(rabbitTemplate).convertAndSend(anyString(), anyString(), anyString());
	}

	@Test
	void createUser_userNameAlreadyExists() {
		CreateUserRequest request = new CreateUserRequest("testUser", "password");
		when(userRepository.existsByUserName(request.getUserName())).thenReturn(true);

		assertThrows(UserNameAlreadyExistsException.class, () -> userService.createUser(request));
		verify(userRepository, never()).save(any(UserEntity.class));
		verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), anyString());
	}

	@Test
	void deleteUser_success() {
		when(userRepository.existsById(user.getId())).thenReturn(true);

		Long userId = userService.deleteUser(user.getId());

		assertNotNull(userId);
		assertEquals(user.getId(), userId);
		verify(userRepository).deleteById(user.getId());
	}

	@Test
	void deleteUser_userNotExist() {
		when(userRepository.existsById(user.getId())).thenReturn(false);

		assertThrows(UserNotExistException.class, () -> userService.deleteUser(user.getId()));
		verify(userRepository, never()).deleteById(user.getId());
	}

	@Test
	void updateUser_success() {
		when(userRepository.existsById(user.getId())).thenReturn(true);
		when(userRepository.save(any(UserEntity.class))).thenReturn(user);

		Long userId = userService.updateUser(user.getId(), user);

		assertNotNull(userId);
		assertEquals(user.getId(), userId);
		verify(userRepository).save(any(UserEntity.class));
	}

	@Test
	void updateUser_userNotExist() {
		when(userRepository.existsById(user.getId())).thenReturn(false);

		assertThrows(UserNotExistException.class, () -> userService.updateUser(user.getId(), user));
		verify(userRepository, never()).save(any(UserEntity.class));
	}

	@Test
	void getUserById_success() {
		when(userRepository.existsById(user.getId())).thenReturn(true);
		when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

		Optional<UserEntity> foundUser = userService.getUserById(user.getId());

		assertTrue(foundUser.isPresent());
		assertEquals(user.getId(), foundUser.get().getId());
	}

	@Test
	void getUserById_userNotExist() {
		when(userRepository.existsById(user.getId())).thenReturn(false);

		assertThrows(UserNotExistException.class, () -> userService.getUserById(user.getId()));
	}

	@Test
	void getUserByUserName_success() {
		when(userRepository.findByUserName(user.getUserName())).thenReturn(user);

		Optional<UserEntity> foundUser = userService.getUserByUserName(user.getUserName());

		assertTrue(foundUser.isPresent());
		assertEquals(user.getUserName(), foundUser.get().getUserName());
	}

	@Test
	void getAllUsers_success() {
		List<UserEntity> users = new ArrayList<>();
		users.add(user);
		users.add(followUser);
		when(userRepository.findAll()).thenReturn(users);

		List<UserEntity> allUsers = userService.getAllUsers();

		assertNotNull(allUsers);
		assertEquals(2, allUsers.size());
	}

	@Test
	void followUser_success() {
		when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
		when(userRepository.findById(followUser.getId())).thenReturn(Optional.of(followUser));

		userService.followUser(user.getId(), followUser.getId());

		assertTrue(user.getFollowings().contains(followUser));
		verify(userRepository).save(user);
	}

	@Test
	void followUser_userNotFound() {
		when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

		userService.followUser(user.getId(), followUser.getId());

		verify(userRepository, never()).save(any(UserEntity.class));
	}

	@Test
	void unfollowUser_success() {
		user.getFollowings().add(followUser);
		when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
		when(userRepository.findById(followUser.getId())).thenReturn(Optional.of(followUser));

		userService.unfollowUser(user.getId(), followUser.getId());

		assertFalse(user.getFollowings().contains(followUser));
		verify(userRepository).save(user);
	}

	@Test
	void unfollowUser_userNotFound() {
		when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

		userService.unfollowUser(user.getId(), followUser.getId());

		verify(userRepository, never()).save(any(UserEntity.class));
	}
}

