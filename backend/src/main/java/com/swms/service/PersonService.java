package com.swms.service;

import com.swms.dto.PersonRequest;
import com.swms.model.Person;
import com.swms.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PersonService {

    @Autowired
    private PersonRepository personRepository;

    public String savePerson(PersonRequest request) {
        Person person = new Person();
        person.setName(request.getName());
        person.setAge(request.getAge());
        person.setCreatedAt(LocalDateTime.now());
        person.setUpdatedAt(LocalDateTime.now());
        
        Person savedPerson = personRepository.save(person);
        
        return "Person created with ID: " + savedPerson.getId();
    }
    
    public List<Person> getAllPersons() {
        return personRepository.findAll();
    }
    
    public Optional<Person> getPersonById(String id) {
        return personRepository.findById(id);
    }
    
    public Optional<Person> getPersonByName(String name) {
        return personRepository.findByName(name);
    }
    
    public String updatePerson(String id, PersonRequest request) {
        Optional<Person> existingPerson = personRepository.findById(id);
        
        if (existingPerson.isPresent()) {
            Person person = existingPerson.get();
            person.setName(request.getName());
            person.setAge(request.getAge());
            person.setUpdatedAt(LocalDateTime.now());
            
            personRepository.save(person);
            return "Person updated successfully";
        }
        
        throw new RuntimeException("Person not found with id: " + id);
    }
    
    public String deletePerson(String id) {
        if (personRepository.existsById(id)) {
            personRepository.deleteById(id);
            return "Person deleted successfully";
        }
        
        throw new RuntimeException("Person not found with id: " + id);
    }
}