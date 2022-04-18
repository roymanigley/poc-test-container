package com.example.demo.web.rest;

import com.example.demo.TestContainerApplication;
import com.example.demo.domain.Task;
import com.example.demo.repository.TaskRepository;
import com.example.demo.service.TaskService;
import com.example.demo.service.TaskServiceIT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.servlet.ServletContext;
import java.net.URI;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@AutoConfigureMockMvc
@SpringBootTest(classes = {TestContainerApplication.class})
class TaskResourceIT {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    TaskRepository repository;

    @Test
    void findAll() throws Exception {
        // GIVEN
        int newRecords = 10;
        long existingRecords = repository.count();
        IntStream.range(0,newRecords).forEach(i -> repository.save(TaskServiceIT.createNewTask()));
        // WHEN
        mockMvc.perform(
            MockMvcRequestBuilders
                .get(URI.create("/api/tasks"))
                .accept(MediaType.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
        // THEN
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(existingRecords + newRecords));
    }

    @Test
    void create() throws Exception {
        // GIVEN
        Task newTask = TaskServiceIT.createNewTask();
        // WHEN
        mockMvc.perform(
            MockMvcRequestBuilders
                .post(URI.create("/api/tasks"))
                .content(
                    mapper.writeValueAsBytes(newTask)
                )
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // THEN
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNotEmpty());
    }

    @Test
    void update() throws Exception {
        // GIVEN
        Task savedTask = repository.save(TaskServiceIT.createNewTask());
        // WHEN
        mockMvc.perform(
                MockMvcRequestBuilders
                        .put(URI.create("/api/tasks"))
                        .content(
                                mapper.writeValueAsBytes(savedTask)
                        )
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // THEN
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(savedTask.getId().toString()));
    }

    @Test
    void delete() throws Exception {
        // GIVEN
        Task savedTask = repository.save(TaskServiceIT.createNewTask());
        long existingRecords = repository.count();
        // WHEN
        mockMvc.perform(
                MockMvcRequestBuilders
                        .delete(URI.create("/api/tasks/" + savedTask.getId()))
                        .accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // THEN
        .andExpect(MockMvcResultMatchers.status().isAccepted());
        assertThat(repository.count()).isEqualTo(existingRecords -1);
    }

    @Test
    void find() throws Exception {
        // GIVEN
        Task savedTask = repository.save(TaskServiceIT.createNewTask());
        // WHEN
        mockMvc.perform(
                MockMvcRequestBuilders
                        .get(URI.create("/api/tasks/" + savedTask.getId()))
                        .accept(MediaType.APPLICATION_JSON))
        .andDo(MockMvcResultHandlers.print())
        // THEN
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.title").value(savedTask.getTitle()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(savedTask.getDescription()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.completed").value(savedTask.isCompleted()))
        .andExpect(MockMvcResultMatchers.jsonPath("$.dueDate").value(formatAsDateTime(savedTask.getDueDate())))
        .andExpect(MockMvcResultMatchers.jsonPath("$.creationDate").value(formatAsDateTime(savedTask.getCreationDate())));
    }

    @NotNull
    private String formatAsDateTime(ZonedDateTime dateTime) {
        return DateTimeFormatter.ofPattern(Task.DATE_TIME_FORMAT).format(dateTime);
    }
}