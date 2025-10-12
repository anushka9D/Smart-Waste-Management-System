package com.swms.service;

import com.swms.dto.PersonRequest;
import com.swms.model.Person;
import com.swms.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PersonService {

    @Autowired
    private PersonRepository personRepository;

    public String savePerson(PersonRequest request) {
        Person person = new Person();
        person.setName(request.getName());
        person.setAge(request.getAge());
        person.setCreatedAt(LocalDateTime.now());
        
        Person savedPerson = personRepository.save(person);
        
        return "Person created with ID: " + savedPerson.getId();
    }
}