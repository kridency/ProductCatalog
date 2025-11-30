package org.example.productcatalog.web.servlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletResponse;
import org.example.productcatalog.exception.ApplicationException;
import org.example.productcatalog.service.CrudService;

import java.io.PrintWriter;
import java.util.Optional;

import static org.example.productcatalog.preset.ProductCatalogInit.*;

public abstract class AbstractServlet<T> extends HttpServlet {
    protected CrudService<T, String> service;
    //private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();

    protected void create(HttpServletResponse response, T object) {
        try (PrintWriter writer = response.getWriter()) {
            writer.println(Optional.ofNullable(service.create(object)).map(x -> {
                response.setStatus(HttpServletResponse.SC_CREATED);
                return CREATED;
            }).orElseGet(() -> {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return PRODUCT_NOT_CREATED;
            }));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            throw new ApplicationException(e.getMessage());
        }
    }

    protected void list(HttpServletResponse response, T object) {
        try (PrintWriter writer = response.getWriter()) {
            writer.println(objectMapper.writeValueAsString(service.findFiltered(object).stream().toList()));
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            if (!e.getMessage().equals(RETURN)) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                throw new ApplicationException(e.getMessage());
            }
        }
    }

    protected void update(HttpServletResponse response, T object) {
        try (PrintWriter writer = response.getWriter()) {
            writer.println(Optional.ofNullable(service.update(object)).map(x -> {
                response.setStatus(HttpServletResponse.SC_CREATED);
                return UPDATED;
            }).orElseGet(() -> {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return PRODUCT_NOT_UPDATED;
            }));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new ApplicationException(e.getMessage());
        }
    }

    protected void delete(HttpServletResponse response, T object) {
        try (PrintWriter writer = response.getWriter()) {
            service.remove(object);
            response.setStatus(HttpServletResponse.SC_OK);
            writer.println(DELETED);
        } catch (Exception e) {
            if (!e.getMessage().equals(RETURN)) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                throw new ApplicationException(e.getMessage());
            }
        }
    }

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
