package com.arcblaze.arctime.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arcblaze.arccore.rest.BaseApplication;
import com.arcblaze.arctime.rest.factory.AssignmentFactory;
import com.arcblaze.arctime.rest.factory.DaoFactoryFactory;
import com.arcblaze.arctime.rest.factory.HolidayFactory;
import com.arcblaze.arctime.rest.factory.TaskFactory;
import com.arcblaze.arctime.rest.factory.UserFactory;

/**
 * Configures the REST end-points for this system.
 */
public class ArcTimeApplication extends BaseApplication {
    private final static Logger log = LoggerFactory.getLogger(ArcTimeApplication.class);

    /**
     * Default constructor.
     */
    public ArcTimeApplication() {
        log.info("Loading ArcTime application resources.");
        // Note that we use ArcTimeApplication.class instead of getClass() to
        // make sure we aren't using the package from a child class. We also use
        // our own register method instead of packages since packages has
        // trouble finding the resources when Tomcat is run as an embedded app.
        registerPackage(ArcTimeApplication.class.getPackage().getName());

        register(AssignmentFactory.getBinder());
        register(DaoFactoryFactory.getBinder());
        register(HolidayFactory.getBinder());
        register(TaskFactory.getBinder());
        register(UserFactory.getBinder());
    }
}
