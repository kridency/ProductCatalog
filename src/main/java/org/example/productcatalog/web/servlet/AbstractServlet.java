package org.example.productcatalog.web.servlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletResponse;

public abstract class AbstractServlet<T> extends HttpServlet {
    //private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();

    protected abstract void create(HttpServletResponse response, T obj);
    protected abstract void list(HttpServletResponse response, T obj);
    protected abstract void update(HttpServletResponse response, T obj);
    protected abstract void delete(HttpServletResponse response, T obj);

    /*protected void validate(HttpServletResponse response, T obj) throws ConstraintViolationException {
        Validator validator = factory.getValidator();

        if (obj != null) {
            final Set<ConstraintViolation<T>> violations = validator.validate(obj);
            if (!violations.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                throw new ConstraintViolationException(violations);
            }
        }
    }*/
}
