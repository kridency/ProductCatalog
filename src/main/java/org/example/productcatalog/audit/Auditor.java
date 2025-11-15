package org.example.productcatalog.audit;

public class Auditor {
    private static Auditor INSTANCE;

    private Auditor() {}

    public static Auditor getInstance() {
        if(INSTANCE == null) INSTANCE = new Auditor();
        return INSTANCE;
    }

    public void audit(String message) {
        System.out.println("Auditor: " + message);
    }
}
