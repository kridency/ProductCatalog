package org.example.productcatalog.audit;

import org.example.productcatalog.terminal.AbstractTerminal;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Instant;

public class AuditingInvocationHandler implements InvocationHandler {
    private final Object auditingObject;
    private final Auditor auditor;

    public AuditingInvocationHandler(final Object auditingObject, final Auditor auditor) {
        this.auditingObject = auditingObject;
        this.auditor = auditor;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        auditor.audit(Instant.now() + " User: " + AbstractTerminal.getPrincipal().getEmail()
                + "; Invoking method: " + method.getName()
                + "; From service: " + method.getDeclaringClass().getTypeName());

        try {
            return method.invoke(auditingObject, args);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }
}
