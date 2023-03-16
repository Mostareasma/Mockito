package com.example.rest;

import com.example.rest.exception.InvalidRequestException;
import com.example.rest.exception.UserNotFoundException;
import com.example.rest.model.User;
import com.example.rest.repository.UsersRepository;
import com.example.rest.service.UsersService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class UsersControllerTest {

    @Autowired
    UsersRepository usersRepository;
    @MockBean
    UsersService userService;
    @Autowired
    private MockMvc mockMvc;

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void givenNonexistentUser_whenSave_thenStatus200() throws Exception {

        User user = new User(1, "test", "test@email.com", "pass");

        when(userService.save(user)).thenReturn(user);


        mockMvc.perform(post("/users")
                        .content(asJsonString(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(user));

        verify(userService, times(1)).save(user);
    }

    @Test
    public void givenExistentUser_whenSave_thenThrowsInvalidRequestException() throws Exception {

        User user = new User(1, "user1", "user1@email.com", "pass");

        when(userService.save(user)).thenThrow(new InvalidRequestException("User with email " + user.getEmail() + " already exists"));

        mockMvc.perform(post("/users")
                        .content(asJsonString(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(Matchers.equalTo("User with email " + user.getEmail() + " already exists")));

        verify(userService, times(1)).save(user);
    }

    @Test
    public void whenGetAll_thenStatus200() throws Exception {
        List<User> users = List.of(
                new User(1, "user1", "user1@email.com", "pass"),
                new User(2, "user2", "user2@email.com", "pass")
        );
        when(userService.getAll()).thenReturn(users);

        ObjectMapper objectMapper = new ObjectMapper();
        String usersJson = objectMapper.writeValueAsString(users);

        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(usersJson));

        verify(userService, times(1)).getAll();
    }

    @Test
    public void givenExistentUserId_whenGetByIt_thenStatus200() throws Exception {

        User user = new User(1, "user1", "user1@email.com", "pass");

        when(userService.getById(11L)).thenReturn(user);

        mockMvc.perform(get("/users/{id}", 11)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(user));

        verify(userService, times(1)).getById(11L);
    }

    @Test
    public void givenNonexistentUserId_whenGetByIt_thenThrowsUserNotFoundException() throws Exception {

        when(userService.getById(11L)).thenThrow(new UserNotFoundException("User doesn't exist"));

        mockMvc.perform(get("/users/{id}", 11)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(Matchers.equalTo("User doesn't exist")));

        verify(userService, times(1)).getById(11L);
    }

    @Test
    public void givenExistentUser_whenDelete_thenStatus200() throws Exception {

        when(userService.delete(14L)).thenReturn(true);

        mockMvc.perform(delete("/users/{id}", 14))
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(true)));

        verify(userService, times(1)).delete(14L);
    }

    @Test
    public void givenExistentUser_whenDelete_thenThrowsUserNotFoundException() throws Exception {
        when(userService.delete(14L)).thenThrow(new UserNotFoundException("User doesn't exist"));

        mockMvc.perform(delete("/users/{id}", 14))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(Matchers.equalTo("User doesn't exist")));

        verify(userService, times(1)).delete(14L);
    }
}
