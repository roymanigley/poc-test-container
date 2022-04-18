package com.example.demo.service;

import com.example.demo.domain.Task;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskService {

    Task save(Task t);
    Optional<Task> find(UUID id);
    List<Task> findAll();
    void delete(UUID id);
}
