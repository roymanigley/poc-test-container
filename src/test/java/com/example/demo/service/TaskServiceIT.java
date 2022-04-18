package com.example.demo.service;

import com.example.demo.TestContainerApplication;
import com.example.demo.domain.Task;
import com.example.demo.repository.TaskRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@Testcontainers
@SpringBootTest(classes = { TestContainerApplication.class })
public class TaskServiceIT {

    @Autowired
    TaskService service;
    @Autowired
    TaskRepository repository;

    @Test
    void save() {
        // GIVEN
        Task task = createNewTask();
        // WHEN
        service.save(task);
        // THEN
        assertThat(task.getId()).isNotNull();
    }

    @Test
    void find() {
        // GIVEN
        Task task = findOrCreateOne();
        // WHEN
        Optional<Task> taskFound = service.find(task.getId());
        // THEN
        assertThat(taskFound).isPresent();
        assertThat(taskFound.get()).isEqualTo(task);
    }

    @Test
    void findAll() {
        // GIVEN
        int newRecords = 10;
        long existingRecords = repository.count();
        IntStream.range(0,newRecords).forEach(i -> repository.save(createNewTask()));
        // WHEN
        List<Task> tasksFound = service.findAll();
        // THEN
        assertThat(tasksFound).isNotEmpty();
        assertThat(tasksFound.size()).isEqualTo(existingRecords + newRecords);
    }

    @Test
    void delete() {
        // GIVEN
        Task task = findOrCreateOne();
        long existingRecords = repository.count();
        // WHEN
        service.delete(task.getId());
        // THEN
        assertThat(repository.count()).isEqualTo(existingRecords -1);
        assertThat(repository.findById(task.getId())).isNotPresent();
    }

    public static Task createNewTask() {
        return Task.builder()
                .title("LALALALALA")
                .description("BABABABABABABABABABABABABABABABABABABAABBAABAB")
                .dueDate(ZonedDateTime.now())
                .completed(false)
                .creationDate(ZonedDateTime.now())
                .build();
    }

    Task findOrCreateOne() {
        return service.findAll().stream()
                .findFirst()
                .orElseGet(() -> repository.save(createNewTask()));
    }

}