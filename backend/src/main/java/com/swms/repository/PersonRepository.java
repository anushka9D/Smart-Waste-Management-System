package com.swms.repository;

import com.swms.model.Person;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRepository extends MongoRepository<Person, String> {
    
    // Find person by name
    Optional<Person> findByName(String name);
    
    // Find all persons by age
    List<Person> findByAge(int age);
    
    // Find persons by age greater than
    List<Person> findByAgeGreaterThan(int age);
    
    // Find persons by age between
    List<Person> findByAgeBetween(int minAge, int maxAge);
}