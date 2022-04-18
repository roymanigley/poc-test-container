package com.example.demo.service.impl;

import com.example.demo.domain.Task;
import com.example.demo.repository.TaskRepository;
import com.example.demo.service.TaskService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository repository;

    public TaskServiceImpl(TaskRepository repository) {
        this.repository = repository;
    }

    @Override
    public Task save(Task t) {
        return repository.save(t);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Task> find(UUID id) {
        return repository.findById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Task> findAll() {
        return repository.findAll();
    }

    @Override
    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
