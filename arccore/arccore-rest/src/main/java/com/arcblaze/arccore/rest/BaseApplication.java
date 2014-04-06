package com.arcblaze.arccore.rest;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arcblaze.arccore.rest.factory.ConfigFactory;
import com.arcblaze.arccore.rest.factory.DaoFactoryFactory;
import com.arcblaze.arccore.rest.factory.HealthCheckRegistryFactory;
import com.arcblaze.arccore.rest.factory.MetricRegistryFactory;
import com.arcblaze.arccore.rest.factory.PasswordFactory;
import com.arcblaze.arccore.rest.factory.TimerFactory;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;

/**
 * The base REST application class.
 */
public abstract class BaseApplication extends ResourceConfig {
	private final static Logger log = LoggerFactory
			.getLogger(BaseApplication.class);

	/**
	 * Default constructor.
	 */
	public BaseApplication() {
		log.info("Loading core application resources.");

		// Note that we use BaseApplication.class instead of getClass() to make
		// sure we aren't using the package from a child class. We also use our
		// own register method instead of packages since packages has trouble
		// finding the resources when Tomcat is run as an embedded app.
		registerPackage(BaseApplication.class.getPackage().getName());

		register(ConfigFactory.getBinder());
		register(DaoFactoryFactory.getBinder());
		register(MetricRegistryFactory.getBinder());
		register(HealthCheckRegistryFactory.getBinder());
		register(TimerFactory.getBinder());
		register(PasswordFactory.getBinder());

		register(JacksonJaxbJsonProvider.class);
	}

	/**
	 * @param packages
	 *            the packages to register with resources
	 */
	public void registerPackage(final String... packages) {
		if (packages != null) {
			for (final String packageName : packages) {
				final Set<Class<?>> classes = getClassNames(packageName);
				for (final Class<?> clazz : classes) {
					log.info("  Registering: {}", clazz.getName());
					register(clazz);
				}
			}
		}
	}

	protected Set<Class<?>> getClassNames(final String packageName) {
		final Set<Class<?>> classes = new LinkedHashSet<>();
		try {
			final SortedSet<String> classNames = new TreeSet<>();
			final ClassPath classPath = ClassPath.from(getClass()
					.getClassLoader());
			for (final ClassInfo classInfo : classPath
					.getTopLevelClassesRecursive(packageName)) {
				classNames.add(classInfo.getName());
			}
			for (final String className : classNames)
				classes.add(Class.forName(className));

		} catch (final IOException classpathIssue) {
			log.error("Failed to retrieve resources from class path.",
					classpathIssue);
		} catch (final ClassNotFoundException notFound) {
			log.error("Failed to load class.", notFound);
		}
		return classes;
	}
}
