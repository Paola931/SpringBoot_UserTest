package com.example.userTest;

import com.example.userTest.Controllers.UserController;
import com.example.userTest.Entities.UserEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles(value = "test")
class UserTestApplicationTests {
    @Autowired
    private UserController userController;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void controlerExistTest() {
        assertThat(userController).isNotNull();
    }

    private UserEntity createUser() throws Exception {
        UserEntity user = new UserEntity();
        user.setName("Mario");
        user.setSurname("Rossi");
        user.setEmail("mario@gmail.com");
        return createUser(user);
    }

    private UserEntity createUser(UserEntity user) throws Exception {
        MvcResult result = createUserRequest(user);
        UserEntity userMap = objectMapper.readValue(result.getResponse().getContentAsString(), UserEntity.class);
        assertThat(userMap.getId()).isNotNull();
        assertThat(userMap).isNotNull();
        return userMap;
    }

    private MvcResult createUserRequest(UserEntity user) throws Exception {
        if (user == null) return null;
        String userJSON = objectMapper.writeValueAsString(user);
        return this.mockMvc.perform(MockMvcRequestBuilders
                        .post("/users/createUser")
                        .content(userJSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
    }

    private UserEntity getFromID(Long id) throws Exception {
        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders
                        .get("/users/getUser/" + id))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        try {
            String userJSON = result.getResponse().getContentAsString();
            UserEntity user = objectMapper.readValue(userJSON, UserEntity.class);
            assertThat(user.getId()).isNotNull();
            assertThat(user).isNotNull();
            return user;
        } catch (Exception e) {
            return null;
        }
    }

    @Test
    void createUserTest() throws Exception {
        UserEntity result = createUser();
    }

    @Test
    void readSingleUser() throws Exception {
        UserEntity user = createUser();
        assertThat(user.getId()).isNotNull();
        UserEntity userget = getFromID(user.getId());
        assertThat(userget.getId()).isEqualTo(user.getId());
    }

    @Test
    void updateUser() throws Exception {
        UserEntity user = createUser();
        assertThat(user.getId()).isNotNull();
        String newName = "Giancarlo";
        user.setName(newName);
        String userJSON = objectMapper.writeValueAsString(user);
        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders
                        .put("/users/updateUser/" + user.getId())
                        .content(userJSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        UserEntity userMap = objectMapper.readValue(result.getResponse().getContentAsString(), UserEntity.class);
        assertThat(userMap.getId()).isEqualTo(user.getId());
        assertThat(userMap.getName()).isEqualTo(newName);
        UserEntity userID = getFromID(user.getId());
        assertThat(userID.getId()).isEqualTo(user.getId());
        assertThat(userID.getName()).isEqualTo(newName);
    }

    @Test
    void deleteUser() throws Exception {
        UserEntity user = createUser();
        assertThat(user.getId()).isNotNull();
        System.out.println("user id is:" + user.getId());
        this.mockMvc.perform(MockMvcRequestBuilders
                        .delete("/users/deleteUser/" + user.getId()))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        System.out.println("user id is:" + user.getId());
        UserEntity userID = getFromID(user.getId());
        assertThat(userID).isNull();
    }
}
