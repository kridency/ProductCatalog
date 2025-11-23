package org.example.productcatalog.audit;

import org.example.productcatalog.entity.Invocation;
import org.example.productcatalog.repository.InvocationRepository;
import org.example.productcatalog.repository.UserRepository;

public class Auditor {
    private static Auditor INSTANCE;
    private final UserRepository userRepository;
    private final InvocationRepository invocationRepository;

    private Auditor() {
        userRepository = UserRepository.getInstance();
        invocationRepository = InvocationRepository.getInstance();
    }

    public static Auditor getInstance() {
        if(INSTANCE == null) INSTANCE = new Auditor();
        return INSTANCE;
    }

    public void audit(String endpoint, String email) {
        invocationRepository.add(new Invocation(endpoint, userRepository.getByEmail(email).orElse(null)));
    }
}
