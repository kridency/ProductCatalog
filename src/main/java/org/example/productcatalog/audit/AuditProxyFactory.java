package org.example.productcatalog.audit;

import java.lang.reflect.Proxy;

@SuppressWarnings("unchecked")
public class AuditProxyFactory {
    public static <T> T createAuditedProxy(T target, Class<? super T> type, Auditor auditor) {
        return (T) Proxy.newProxyInstance(
                type.getClassLoader(),
                new Class<?>[]{type},
                new AuditingInvocationHandler(target, auditor)
        );
    }
}
