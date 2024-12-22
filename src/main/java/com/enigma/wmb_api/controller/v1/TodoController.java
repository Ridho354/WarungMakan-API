package com.enigma.wmb_api.controller.v1;

import com.enigma.wmb_api.dto.request.TodoRequest;
import com.enigma.wmb_api.dto.response.TodoResponse;
import com.enigma.wmb_api.service.impl.PostServiceImpl;
import com.enigma.wmb_api.service.impl.TodoServiceImpl;
import com.enigma.wmb_api.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("api/v1/todos")
@RestController
@RequiredArgsConstructor
public class TodoController {
    private final TodoServiceImpl todoService;

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getTodoById(@PathVariable String id) {
        TodoResponse todoResponse = todoService.getTodosById(id);
        return ResponseUtil.buildResponse(HttpStatus.OK, "Success get todo by ID:" + id, todoResponse);
    }

    @GetMapping()
    public ResponseEntity<?> getAllTodos(@RequestParam(value = "_limit", defaultValue = "10") Integer limit) {
        List<TodoResponse> todoListResponse = todoService.getAllTodos(limit);
        return ResponseUtil.buildResponse(HttpStatus.OK, "Success get all todos with limit" + limit, todoListResponse);
    }

    @PostMapping()
    public ResponseEntity<?> createTodo(@RequestBody TodoRequest todoRequest) {
        TodoResponse todoResponse = todoService.createTodo(todoRequest);
        return ResponseUtil.buildResponse(HttpStatus.OK, "Success creating todo", todoResponse);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<?> updateTodo(@PathVariable String id, @RequestBody TodoRequest todoRequest) {
        TodoResponse todoResponse = todoService.updateTodo(id, todoRequest);
        return ResponseUtil.buildResponse(HttpStatus.OK, "Success updating todo", todoResponse);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteTodo(@PathVariable String id) {
        todoService.deleteTodo(id);
        return ResponseUtil.buildResponse(HttpStatus.OK, "Success deleting todo", null);
    }
}
