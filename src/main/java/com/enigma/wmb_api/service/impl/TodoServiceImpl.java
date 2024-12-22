package com.enigma.wmb_api.service.impl;

import com.enigma.wmb_api.dto.request.TodoRequest;
import com.enigma.wmb_api.dto.response.TodoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
//@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class TodoServiceImpl {
    @Autowired
    @Qualifier("jsonPlaceHolderClient")
    private RestClient jsonPlaceholderClient;

    public TodoResponse getTodosById(String id) {
        log.info("getTodosById with id: " + id);
        try {
            String endpointUrl = "todos/" + id;
            return jsonPlaceholderClient
                    .get()
                    .uri(endpointUrl)
                    .retrieve()
                    .body(TodoResponse.class);
        } catch (RestClientException e) {
            log.error("error when retrieving todo by id" + e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Todo not found");
        }
    }

    public List<TodoResponse> getAllTodos(Integer limit) {
        log.info("getAllTodos with limit: " + limit);
        try {
            return jsonPlaceholderClient
                    .get()
                    .uri(uriBuilder -> {
                return uriBuilder.path("todos").queryParam("_limit", limit).build();
                    })
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<TodoResponse>>() {});
        } catch (Exception e) {
        log.error("error when retrieving todos" + e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Todos not found");
        }
    }

    public TodoResponse createTodo(TodoRequest todoRequest) {
        log.info("create a new todo: " + todoRequest);
        try {
            return jsonPlaceholderClient
                    .post()
                    .uri("/todos")
                    .body(todoRequest) // request body
                    .retrieve()
                    .body(TodoResponse.class); // response body
        } catch (Exception e) {
            log.error("error when creating todo" + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error when creating todo, might be due to payload");
        }
    }

    public TodoResponse updateTodo(String id, TodoRequest todoRequest) {
        log.info("create a new todo: " + todoRequest);
        try {
            return jsonPlaceholderClient
                    .put()
                    .uri("/todos" + id)
                    .body(todoRequest) // request body
                    .retrieve()
                    .body(TodoResponse.class); // response body
        } catch (Exception e) {
            log.error("error when updating todo" + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error when creating todo, might be due to payload");
        }
    }

    public void deleteTodo(String id) {
        log.info("deleteTodo with id: " + id);
        try {
            String endpointUrl = "todos/" + id;
            jsonPlaceholderClient
                    .get()
                    .uri(endpointUrl)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            log.error("error when delete todo by id" + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Got error from todo server");
        }
    }
}
