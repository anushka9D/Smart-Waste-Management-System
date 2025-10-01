package com.wasterouting.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class FirebaseService {

    private final Firestore db;

    public FirebaseService() {
        this.db = FirestoreClient.getFirestore();
    }

    public String testConnection() throws ExecutionException, InterruptedException {
        // Create a test document
        DocumentReference docRef = db.collection("testConnection").document("testDoc");
        
        // Create a simple test object
        Map<String, Object> testData = new HashMap<>();
        testData.put("timestamp", System.currentTimeMillis());
        testData.put("status", "connection_test");
        
        // Write the test data to Firestore
        ApiFuture<WriteResult> result = docRef.set(testData);
        
        // Wait for the write to complete and return the result
        return "Data successfully written at: " + result.get().getUpdateTime();
    }

    public String readTestData() throws ExecutionException, InterruptedException {
        DocumentReference docRef = db.collection("testConnection").document("testDoc");
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();
        
        if (document.exists()) {
            return "Test data: " + document.getData();
        } else {
            return "No test data found";
        }
    }
}
