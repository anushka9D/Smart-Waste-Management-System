package com.swms.controller;

import com.swms.dto.ApiResponse;
import com.swms.dto.PersonRequest;
import com.swms.model.Person;
import com.swms.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/persons")
public class PersonController extends BaseController {

    private final PersonService personService;

    @Autowired
    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<String>> addPerson(@RequestBody PersonRequest person) {
        try {
            String result = personService.savePerson(person);
            return success("Person added successfully", result);
        } catch (Exception e) {
            return error("Failed to add person: " + e.getMessage());
        }
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<Person>>> getAllPersons() {
        try {
            List<Person> persons = personService.getAllPersons();
            return success("Persons retrieved successfully", persons);
        } catch (Exception e) {
            return error("Failed to retrieve persons: " + e.getMessage());
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Person>> getPersonById(@PathVariable String id) {
        try {
            Optional<Person> person = personService.getPersonById(id);
            if (person.isPresent()) {
                return success("Person found", person.get());
            }
            return notFound("Person not found with id: " + id);
        } catch (Exception e) {
            return error("Failed to retrieve person: " + e.getMessage());
        }
    }
    
    @GetMapping("/name/{name}")
    public ResponseEntity<ApiResponse<Person>> getPersonByName(@PathVariable String name) {
        try {
            Optional<Person> person = personService.getPersonByName(name);
            if (person.isPresent()) {
                return success("Person found", person.get());
            }
            return notFound("Person not found with name: " + name);
        } catch (Exception e) {
            return error("Failed to retrieve person: " + e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> updatePerson(
            @PathVariable String id, 
            @RequestBody PersonRequest person) {
        try {
            String result = personService.updatePerson(id, person);
            return success(result, null);
        } catch (Exception e) {
            return error("Failed to update person: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deletePerson(@PathVariable String id) {
        try {
            String result = personService.deletePerson(id);
            return success(result, null);
        } catch (Exception e) {
            return error("Failed to delete person: " + e.getMessage());
        }
    }
}