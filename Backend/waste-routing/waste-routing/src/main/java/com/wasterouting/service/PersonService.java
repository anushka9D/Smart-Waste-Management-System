// src/main/java/com/wasterouting/service/PersonService.java
package com.wasterouting.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.wasterouting.dto.PersonRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class PersonService {

    private final Firestore firestore;

    @Autowired
    public PersonService(Firestore firestore) {
        this.firestore = firestore;
    }

    public String savePerson(PersonRequest person) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection("persons").document();

        Map<String, Object> data = new HashMap<>();
        data.put("name", person.getName());
        data.put("age", person.getAge());

        ApiFuture<WriteResult> result = docRef.set(data);
        return "Person created with ID: " + docRef.getId() + " at: " + result.get().getUpdateTime();
    }
}