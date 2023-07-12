package com.example.qrattendance.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import javax.persistence.PersistenceException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import com.example.qrattendance.model.AttendanceUser;
import com.example.qrattendance.repository.UserRepository;
import com.example.qrattendance.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private UserService userService;
    
    private ObjectMapper objectMapper = new ObjectMapper();
    
    @Test
    public void testCreateAttendanceUser() throws Exception {
     
        AttendanceUser attendanceUser = new AttendanceUser();
        attendanceUser.setUsername("username");
        attendanceUser.setPassword("password");
        
        String attendanceUserJson = objectMapper.writeValueAsString(attendanceUser);

        doReturn(attendanceUser).when(userService).createUser(any());
        
        mockMvc.perform(post("/users/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(attendanceUserJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("username"))
                .andExpect(jsonPath("$.currentTokenSession").isEmpty());
    }

    @Test
    public void testCreateAttendanceUserPersistanceError() throws Exception {
        doThrow(new PersistenceException()).when(userService).createUser(any());
        AttendanceUser attendanceUser = new AttendanceUser();
        attendanceUser.setUsername("username");
        attendanceUser.setPassword("password");

        String attendanceUserJson = objectMapper.writeValueAsString(attendanceUser);

        mockMvc.perform(post("/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(attendanceUserJson))
                .andExpect(status().isInternalServerError());
                
    }

    @Test
    public void testLoginAttendanceUser() throws Exception {

        AttendanceUser attendanceUser = new AttendanceUser();
        attendanceUser.setUsername("username");
        attendanceUser.setPassword("password");

        String attendanceUserJson = objectMapper.writeValueAsString(attendanceUser);

        doReturn(attendanceUser).when(userService).createUser(any());
        
        
        mockMvc.perform(post("/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(attendanceUserJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("username"))
                .andExpect(jsonPath("$.currentTokenSession").isEmpty());

        
        doReturn("tokenid").when(userService).login(any(), any(), any());
        
        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(attendanceUserJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    public void testLoginAttendanceUserFailure() throws Exception {

        AttendanceUser attendanceUser = new AttendanceUser();
        attendanceUser.setUsername("username");
        attendanceUser.setPassword("password");

        String attendanceUserJson = objectMapper.writeValueAsString(attendanceUser);

        doReturn(null).when(userService).login(any(), any(), any());
                
        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(attendanceUserJson))
                .andExpect(status().isBadRequest());
    }
}