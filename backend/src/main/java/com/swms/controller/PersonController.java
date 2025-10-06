package com.swms.controller;

import com.swms.dto.PersonRequest;
import com.swms.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/persons")
public class PersonController extends BaseController {

    private final PersonService personService;

    @Autowired
    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @PostMapping
    public ResponseEntity<?> addPerson(@RequestBody PersonRequest person) {
        try {
            String result = personService.savePerson(person);
            return success("Person added successfullyyy", result);
        } catch (Exception e) {
            return error("Failed to add person: " + e.getMessage());
        }
    }
}