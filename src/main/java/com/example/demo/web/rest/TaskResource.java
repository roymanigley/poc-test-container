package com.example.demo.web.rest;

import com.example.demo.domain.Task;
import com.example.demo.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;

import static java.util.Objects.isNull;

@RestController
@RequestMapping("/api/tasks")
public class TaskResource {

    private final TaskService service;

    public TaskResource(TaskService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Task>> findAll() {
        return ResponseEntity
                .ok(service.findAll());
    }

    @PostMapping
    public ResponseEntity<Task> create(@RequestBody @Valid Task task) {
        task.setId(null);
        service.save(task);
        return ResponseEntity
                .created(URI.create("/api/tasks/" + task.getId()))
                .body(task);
    }

    @PutMapping
    public ResponseEntity<Task> update(@RequestBody @Valid  Task task) {
        if (isNull(task.getId())) {
            return ResponseEntity.badRequest().build();
        }
        service.save(task);
        return ResponseEntity
                .ok(task);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable("id") UUID id) {
        service.delete(id);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> find(@PathVariable("id") UUID id) {
        return service.find(id)
                .map(ResponseEntity::ok)
                .orElseGet(ResponseEntity.notFound()::build);
    }
}
