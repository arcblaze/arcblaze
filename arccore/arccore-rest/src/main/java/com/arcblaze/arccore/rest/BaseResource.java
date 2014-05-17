package com.arcblaze.arccore.rest;

import javax.mail.MessagingException;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arcblaze.arccore.common.config.Config;
import com.arcblaze.arccore.common.model.User;
import com.arcblaze.arccore.db.DatabaseException;
import com.arcblaze.arccore.mail.MailSenderThread;
import com.arcblaze.arccore.mail.sender.SystemErrorMailSender;
import com.codahale.metrics.health.HealthCheck;

/**
 * The base class for all resources.
 */
public class BaseResource extends HealthCheck {
    private final static Logger log = LoggerFactory.getLogger(BaseResource.class);

    /** A system error resulting in failed health checks. */
    private Throwable failure = null;

    /**
     * @param message
     *            the message to include in the exception
     * 
     * @return a {@link NotFoundException} with a suitable status code and error message
     */
    protected NotFoundException notFound(final String message) {
        log.error(message);
        return new NotFoundException(Response.status(Status.NOT_FOUND).entity(message).build());
    }

    /**
     * @param message
     *            the message to include in the exception
     * 
     * @return a {@link BadRequestException} with a suitable status code and error message
     */
    protected BadRequestException badRequest(final String message) {
        log.error(message);
        return new BadRequestException(Response.status(Status.BAD_REQUEST).entity(message).build());
    }

    /**
     * @param config
     *            the system configuration information
     * @param user
     *            the user account that performed the unauthorized action
     * @param message
     *            the message to include in the exception
     * 
     * @return a {@link ForbiddenException} with a suitable status code and error message
     */
    protected ForbiddenException forbidden(final Config config, final User user, final String message) {
        // This will cause the health check to fail.
        this.failure = new Exception("User " + user + " attempted to perform an unauthorized action: " + message);

        log.error("User attempted to perform an unauthorized action: " + user);
        log.error(message);
        final ForbiddenException forbidden = new ForbiddenException(Response.status(Status.FORBIDDEN).entity(message)
                .build());

        new MailSenderThread(new SystemErrorMailSender(config, user, forbidden)).start();

        return forbidden;
    }

    /**
     * @param config
     *            the system configuration properties
     * @param user
     *            the user involved in the activity
     * @param exception
     *            the database exception
     * 
     * @return an {@link InternalServerErrorException} with a suitable status code and error message
     */
    protected InternalServerErrorException dbError(final Config config, final User user,
            final DatabaseException exception) {
        // This will cause the health check to fail.
        this.failure = exception;

        log.error("Database error", exception);

        new MailSenderThread(new SystemErrorMailSender(config, user, exception)).start();

        return new InternalServerErrorException(Response.status(Status.INTERNAL_SERVER_ERROR)
                .entity(exception.getMessage()).build());
    }

    /**
     * @param config
     *            the system configuration properties
     * @param user
     *            the user involved in the activity
     * @param exception
     *            the exception that occurred on the server
     * 
     * @return an {@link InternalServerErrorException} with a suitable status code and error message
     */
    protected InternalServerErrorException serverError(final Config config, final User user, final Exception exception) {
        // This will cause the health check to fail.
        this.failure = exception;

        log.error("Server error", exception);

        new MailSenderThread(new SystemErrorMailSender(config, user, exception)).start();

        return new InternalServerErrorException(Response.status(Status.INTERNAL_SERVER_ERROR)
                .entity(exception.getMessage()).build());
    }

    /**
     * @param exception
     *            the database exception
     * 
     * @return a {@link InternalServerErrorException} with a suitable status code and error message
     */
    protected InternalServerErrorException mailError(final Config config, final User user,
            final MessagingException exception) {
        // This will cause the health check to fail.
        this.failure = exception;

        log.error("Mail error", exception);

        new MailSenderThread(new SystemErrorMailSender(config, user, exception)).start();

        String message = exception.getMessage();
        if (message == null)
            message = "Failed to send email.";
        return new InternalServerErrorException(Response.status(Status.INTERNAL_SERVER_ERROR).entity(message).build());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Result check() throws Exception {
        if (this.failure != null)
            return Result.unhealthy(this.failure);
        return Result.healthy();
    }
}
