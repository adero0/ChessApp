package org.example.vuejstest.controllers;

import org.example.vuejstest.models.Todo;
import org.example.vuejstest.models.User;
import org.example.vuejstest.repository.TodoRepository;
import org.example.vuejstest.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/todos")
public class TodoController {
    private static final Logger logger = LoggerFactory.getLogger(TodoController.class);
    private final TodoRepository todoRepository;
    private final UserRepository userRepository;

    public TodoController(TodoRepository todoRepository, UserRepository userRepository) {
        this.todoRepository = todoRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<Todo> getTodos() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new NoSuchElementException("User not found"));
        return todoRepository.findByUserId(user.getId());
    }

    @PostMapping
    public Todo createTodo(@RequestBody Todo todo) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new NoSuchElementException("User not found"));
        todo.setUser(user);
        return todoRepository.save(todo);
    }

    @DeleteMapping("/{id}")
    public void deleteTodo(@PathVariable Long id) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new NoSuchElementException("User not found"));

            Todo todo = todoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Todo not found"));
            if (!todo.getUser().getId().equals(user.getId())) {
                throw new RuntimeException("You can only delete your own todos!");
            }
            logger.info("Deleting todo with ID: {}", id);
            todoRepository.deleteById(id);
        } catch (Exception e) {
            logger.error("{} error with deleting todo!!", e.getMessage());
        }
        }
}