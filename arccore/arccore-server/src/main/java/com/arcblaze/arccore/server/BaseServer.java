package com.arcblaze.arccore.server;

import static com.arcblaze.arccore.server.ServerProperty.SERVER_CERTIFICATE_KEY_ALIAS;
import static com.arcblaze.arccore.server.ServerProperty.SERVER_CONFIG_FILE;
import static com.arcblaze.arccore.server.ServerProperty.SERVER_DEVELOPMENT_MODE;
import static com.arcblaze.arccore.server.ServerProperty.SERVER_INSECURE_MODE;
import static com.arcblaze.arccore.server.ServerProperty.SERVER_KEYSTORE_FILE;
import static com.arcblaze.arccore.server.ServerProperty.SERVER_KEYSTORE_PASS;
import static com.arcblaze.arccore.server.ServerProperty.SERVER_PORT_INSECURE;
import static com.arcblaze.arccore.server.ServerProperty.SERVER_PORT_SECURE;
import static com.arcblaze.arccore.server.ServerProperty.SERVER_WEBAPP_DIR;
import static org.glassfish.jersey.servlet.ServletProperties.JAXRS_APPLICATION_CLASS;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Service;
import org.apache.catalina.Wrapper;
import org.apache.catalina.authenticator.FormAuthenticator;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.deploy.FilterDef;
import org.apache.catalina.deploy.FilterMap;
import org.apache.catalina.deploy.LoginConfig;
import org.apache.catalina.deploy.SecurityCollection;
import org.apache.catalina.deploy.SecurityConstraint;
import org.apache.catalina.servlets.DefaultServlet;
import org.apache.catalina.startup.Tomcat;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.lang.StringUtils;
import org.apache.coyote.http11.Http11NioProtocol;
import org.apache.jasper.servlet.JspServlet;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arcblaze.arccore.common.config.Config;
import com.arcblaze.arccore.common.model.Role;
import com.arcblaze.arccore.db.DaoFactory;
import com.arcblaze.arccore.rest.BaseApplication;
import com.arcblaze.arccore.rest.factory.ConfigFactory;
import com.arcblaze.arccore.rest.factory.DaoFactoryFactory;
import com.arcblaze.arccore.server.security.SecurityRealm;
import com.arcblaze.arccore.server.tasks.BackgroundTask;
import com.arcblaze.arccore.server.tasks.MemoryUsageLoggingTask;
import com.arcblaze.arccore.server.tasks.SystemHealthCheckTask;
import com.arcblaze.arccore.server.util.ResponseCodeStatusFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.servlet.InstrumentedFilter;
import com.codahale.metrics.servlets.HealthCheckServlet;
import com.codahale.metrics.servlets.MetricsServlet;
import com.codahale.metrics.servlets.PingServlet;
import com.codahale.metrics.servlets.ThreadDumpServlet;

/**
 * The base class for application hosting servers.
 */
public abstract class BaseServer {
    private final static Logger log = LoggerFactory.getLogger(BaseServer.class);

    static {
        configureTomcatLogging();
    }

    private final Config config;
    private final Tomcat tomcat;

    private final MetricRegistry metricRegistry = new MetricRegistry();
    private final HealthCheckRegistry healthCheckRegistry = new HealthCheckRegistry();

    /**
     * Default constructor initializes the embedded Tomcat server.
     * 
     * @throws ConfigurationException
     *             if there is a problem loading the system configuration properties
     */
    public BaseServer() throws ConfigurationException {
        this.config = new Config(SERVER_CONFIG_FILE.getDefaultValue());

        final DaoFactory daoFactory = getDaoFactory(this.config);

        String baseDir = ".";
        if (this.config.getBoolean(SERVER_DEVELOPMENT_MODE))
            baseDir = "target";

        this.tomcat = new Tomcat();
        this.tomcat.setBaseDir(baseDir);
        this.tomcat.getHost().setAppBase(".");

        final Service service = this.tomcat.getService();
        final Connector insecureConnector = getInsecureConnector(this.config);
        service.addConnector(insecureConnector);
        this.tomcat.setConnector(insecureConnector);
        if (!this.config.getBoolean(SERVER_INSECURE_MODE)) {
            service.addConnector(getSecureConnector(this.config));
            insecureConnector.setRedirectPort(this.config.getInt(SERVER_PORT_INSECURE));
        }

        final SecurityRealm realm = new SecurityRealm("security", daoFactory);
        this.tomcat.getEngine().setRealm(realm);

        final String webappDir = this.config.getString(SERVER_WEBAPP_DIR);
        final Context context = this.tomcat.addContext("", webappDir);
        context.addWelcomeFile("/index.jsp");

        addServletWrappers(this.config, context);
        addMetricFilter(this.config, context);

        final Set<Role> roles = getSystemRoles(this.config);
        if (roles != null) {
            for (final Role role : getSystemRoles(this.config))
                context.addSecurityRole(role.getName());
        }
        for (final SecurityConstraint constraint : getSecurityConstraints(this.config))
            context.addConstraint(constraint);

        final LoginConfig loginConfig = new LoginConfig();
        loginConfig.setAuthMethod("FORM");
        loginConfig.setRealmName(realm.getName());
        loginConfig.setLoginPage("/WEB-INF/login.jsp");
        loginConfig.setErrorPage("/WEB-INF/error.jsp");
        context.setLoginConfig(loginConfig);
        context.getPipeline().addValve(new FormAuthenticator());

        context.getServletContext().setAttribute(ConfigFactory.CONFIG_FACTORY_CONFIG, this.config);
        context.getServletContext().setAttribute(DaoFactoryFactory.DAO_FACTORY_CONFIG, daoFactory);
        context.getServletContext().setAttribute(InstrumentedFilter.REGISTRY_ATTRIBUTE, this.metricRegistry);
        context.getServletContext().setAttribute(MetricsServlet.METRICS_REGISTRY, this.metricRegistry);
        context.getServletContext().setAttribute(HealthCheckServlet.HEALTH_CHECK_REGISTRY, this.healthCheckRegistry);

        context.addMimeMapping("css", "text/css");
        context.addMimeMapping("js", "application/javascript");

        launchBackgroundTasks(this.config, this.metricRegistry, this.healthCheckRegistry);
    }

    /**
     * Start the embedded tomcat server.
     */
    public void start() {
        try {
            this.tomcat.start();
            this.tomcat.getServer().await();
        } catch (final LifecycleException lifecycleException) {
            log.error("Tomcat error.", lifecycleException);
        }
    }

    /**
     * @return the system configuration properties
     */
    protected Config getConfiguration() {
        return this.config;
    }

    /**
     * @param config
     *            the system configuration properties
     * 
     * @return the system {@link DaoFactory} instance used to hit the database
     */
    public abstract DaoFactory getDaoFactory(final Config config);

    /**
     * Configure logging within Tomcat.
     */
    protected static void configureTomcatLogging() {
        final File prodFile = new File("conf/logging.properties");
        final File devFile = new File("src/main/resources/logging.properties");
        if (prodFile.exists())
            System.setProperty("java.util.logging.config.file", prodFile.getAbsolutePath());
        else if (devFile.exists())
            System.setProperty("java.util.logging.config.file", devFile.getAbsolutePath());
    }

    /**
     * @param config
     *            the system configuration properties
     * 
     * @return a {@link Connector} for insecure HTTP connections with web clients
     */
    protected Connector getInsecureConnector(final Config config) {
        final Connector httpConnector = new Connector(Http11NioProtocol.class.getName());
        httpConnector.setPort(config.getInt(SERVER_PORT_INSECURE));
        httpConnector.setSecure(false);
        httpConnector.setScheme("http");
        addCompressionAttributes(config, httpConnector);
        return httpConnector;
    }

    /**
     * @param config
     *            the system configuration properties
     * 
     * @return a {@link Connector} for secure HTTPS connections with web clients
     */
    protected Connector getSecureConnector(final Config config) {
        final Connector httpsConnector = new Connector(Http11NioProtocol.class.getName());
        httpsConnector.setPort(config.getInt(SERVER_PORT_SECURE));
        httpsConnector.setSecure(true);
        httpsConnector.setScheme("https");
        httpsConnector.setAttribute("clientAuth", "false");
        httpsConnector.setAttribute("sslProtocol", "TLS");
        httpsConnector.setAttribute("SSLEnabled", true);
        httpsConnector.setAttribute("keyAlias", config.getString(SERVER_CERTIFICATE_KEY_ALIAS));
        httpsConnector.setAttribute("keystorePass", config.getString(SERVER_KEYSTORE_PASS));
        httpsConnector.setAttribute("keystoreFile", config.getString(SERVER_KEYSTORE_FILE));
        addCompressionAttributes(config, httpsConnector);
        return httpsConnector;
    }

    /**
     * @param config
     *            the system configuration properties
     * @param connector
     *            the {@link Connector} on which the compression attributes will be applied
     */
    protected void addCompressionAttributes(final Config config, final Connector connector) {
        connector.setAttribute("compression", "on");
        connector.setAttribute("compressionMinSize", "2048");
        connector.setAttribute("noCompressionUserAgents", "gozilla, traviata");
        connector.setAttribute("compressableMimeType", StringUtils.join(Arrays.asList("text/html", "text/plain",
                "text/css", "text/javascript", "application/json", "application/xml"), ","));
        connector.setAttribute("useSendfile", "false");
    }

    /**
     * @param config
     *            the system configuration properties
     * @param context
     *            the context to use when creating the servlet wrappers
     */
    protected void addServletWrappers(final Config config, final Context context) {
        final Wrapper defaultServlet = getDefaultServletWrapper(this.config, context);
        context.addChild(defaultServlet);
        context.addServletMapping("/", defaultServlet.getName());

        final Wrapper jspServlet = getJspServletWrapper(this.config, context);
        context.addChild(jspServlet);
        context.addServletMapping("*.jsp", jspServlet.getName());

        final Wrapper metricsServlet = getMetricsServletWrapper(this.config, context);
        context.addChild(metricsServlet);
        context.addServletMapping("/rest/admin/metrics", metricsServlet.getName());

        final Wrapper healthServlet = getHealthServletWrapper(this.config, context);
        context.addChild(healthServlet);
        context.addServletMapping("/rest/admin/health", healthServlet.getName());

        final Wrapper threadDumpServlet = getThreadDumpServletWrapper(this.config, context);
        context.addChild(threadDumpServlet);
        context.addServletMapping("/rest/admin/threads", threadDumpServlet.getName());

        final Wrapper pingServlet = getPingServletWrapper(this.config, context);
        context.addChild(pingServlet);
        context.addServletMapping("/ping", pingServlet.getName());

        final Wrapper jerseyServlet = getJerseyServletWrapper(this.config, context);
        context.addChild(jerseyServlet);
        context.addServletMapping("/rest/*", jerseyServlet.getName());
    }

    /**
     * @param config
     *            the system configuration properties
     * @param context
     *            the context to use when creating the servlet wrapper
     * 
     * @return the default servlet wrapper to be included in the app
     */
    protected Wrapper getDefaultServletWrapper(final Config config, final Context context) {
        final Wrapper defaultServlet = context.createWrapper();
        defaultServlet.setName("default");
        defaultServlet.setServletClass(DefaultServlet.class.getName());
        defaultServlet.addInitParameter("debug", "0");
        defaultServlet.addInitParameter("listings", "false");
        defaultServlet.addInitParameter("sendfileSize", "-1");
        defaultServlet.setLoadOnStartup(1);
        return defaultServlet;
    }

    /**
     * @param config
     *            the system configuration properties
     * @param context
     *            the context to use when creating the servlet wrapper
     * 
     * @return the JSP servlet wrapper to be included in the app
     */
    protected Wrapper getJspServletWrapper(final Config config, final Context context) {
        final Wrapper defaultServlet = context.createWrapper();
        defaultServlet.setName("jsp");
        defaultServlet.setServletClass(JspServlet.class.getName());
        defaultServlet.addInitParameter("classdebuginfo", "false");
        defaultServlet.addInitParameter("development", String.valueOf(config.getBoolean(SERVER_DEVELOPMENT_MODE)));
        defaultServlet.addInitParameter("fork", "false");
        defaultServlet.setLoadOnStartup(3);
        return defaultServlet;
    }

    /**
     * @param config
     *            the system configuration properties
     * 
     * @return a {@link Class} representing the jersey {@link ResourceConfig} used to manage the web application
     *         resources
     */
    public abstract Class<? extends BaseApplication> getApplicationClass(final Config config);

    /**
     * @param config
     *            the system configuration properties
     * @param context
     *            the context to use when creating the servlet wrapper
     * 
     * @return the jersey servlet wrapper to be included in the app
     */
    protected Wrapper getJerseyServletWrapper(final Config config, final Context context) {
        final Wrapper jerseyServlet = context.createWrapper();
        jerseyServlet.setName("jersey");
        jerseyServlet.setServletClass(ServletContainer.class.getName());
        jerseyServlet.addInitParameter(JAXRS_APPLICATION_CLASS, getApplicationClass(config).getName());
        jerseyServlet.setLoadOnStartup(1);
        return jerseyServlet;
    }

    /**
     * @param config
     *            the system configuration properties
     * @param context
     *            the context to use when creating the servlet wrapper
     * 
     * @return the metrics servlet wrapper to be included in the app
     */
    protected Wrapper getMetricsServletWrapper(final Config config, final Context context) {
        final Wrapper metricsServlet = context.createWrapper();
        metricsServlet.setName("metrics");
        metricsServlet.setServletClass(MetricsServlet.class.getName());
        metricsServlet.setLoadOnStartup(2);
        return metricsServlet;
    }

    /**
     * @param config
     *            the system configuration properties
     * @param context
     *            the context to use when creating the servlet wrapper
     * 
     * @return the health check servlet wrapper to be included in the app
     */
    protected Wrapper getHealthServletWrapper(final Config config, final Context context) {
        final Wrapper metricsServlet = context.createWrapper();
        metricsServlet.setName("health");
        metricsServlet.setServletClass(HealthCheckServlet.class.getName());
        metricsServlet.setLoadOnStartup(2);
        return metricsServlet;
    }

    /**
     * @param config
     *            the system configuration properties
     * @param context
     *            the context to use when creating the servlet wrapper
     * 
     * @return the ping servlet wrapper to be included in the app
     */
    protected Wrapper getPingServletWrapper(final Config config, final Context context) {
        final Wrapper metricsServlet = context.createWrapper();
        metricsServlet.setName("ping");
        metricsServlet.setServletClass(PingServlet.class.getName());
        metricsServlet.setLoadOnStartup(2);
        return metricsServlet;
    }

    /**
     * @param config
     *            the system configuration properties
     * @param context
     *            the context to use when creating the servlet wrapper
     * 
     * @return the thread dump servlet wrapper to be included in the app
     */
    protected Wrapper getThreadDumpServletWrapper(final Config config, final Context context) {
        final Wrapper metricsServlet = context.createWrapper();
        metricsServlet.setName("threads");
        metricsServlet.setServletClass(ThreadDumpServlet.class.getName());
        metricsServlet.setLoadOnStartup(2);
        return metricsServlet;
    }

    /**
     * @param config
     *            the system configuration properties
     * @param context
     *            the context to which the filter will be added
     */
    protected void addMetricFilter(final Config config, final Context context) {
        final FilterDef metricsFilter = new FilterDef();
        metricsFilter.setFilterName("metricsFilter");
        metricsFilter.setFilterClass(ResponseCodeStatusFilter.class.getName());
        context.addFilterDef(metricsFilter);
        final FilterMap metricsFilterMap = new FilterMap();
        metricsFilterMap.setFilterName(metricsFilter.getFilterName());
        metricsFilterMap.addURLPattern("/*");
        context.addFilterMap(metricsFilterMap);
    }

    /**
     * @param config
     *            the system configuration properties
     * 
     * @return a {@link Map} specifying the roles associated with each REST end-point URI
     */
    public abstract Map<String, List<Role>> getEndpointRoleMap(final Config config);

    /**
     * @param config
     *            the system configuration properties
     * 
     * @return the {@link SecurityConstraint} objects to be applied to the web application
     */
    protected List<SecurityConstraint> getSecurityConstraints(final Config config) {
        final List<SecurityConstraint> constraints = new ArrayList<>();

        final Map<String, List<Role>> map = getEndpointRoleMap(config);
        if (map == null)
            return constraints;

        String userConstraint = "NONE";
        if (!config.getBoolean(SERVER_DEVELOPMENT_MODE))
            userConstraint = "CONFIDENTIAL";

        for (final Map.Entry<String, List<Role>> entry : map.entrySet()) {
            final SecurityCollection collection = new SecurityCollection();
            collection.setName(entry.getKey());
            collection.addPattern(entry.getKey());

            final SecurityConstraint constraint = new SecurityConstraint();
            for (final Role role : entry.getValue())
                constraint.addAuthRole(role.getName());
            constraint.setDisplayName(entry.getKey());
            constraint.addCollection(collection);
            constraint.setUserConstraint(userConstraint);
            constraints.add(constraint);
        }

        return constraints;
    }

    /**
     * @param config
     *            the system configuration properties
     * 
     * @return the set of all the roles supported in this system
     */
    public abstract Set<Role> getSystemRoles(final Config config);

    /**
     * @param config
     *            the system configuration properties
     * @param metricRegistry
     *            the registry of metrics used to track system performance information
     * @param healthCheckRegistry
     *            the registry of system health and status information
     * 
     * @return a {@link Collection} of the background tasks to be scheduled within this system
     */
    public abstract Collection<BackgroundTask> getBackgroundTasks(final Config config,
            final MetricRegistry metricRegistry, final HealthCheckRegistry healthCheckRegistry);

    /**
     * Launches background tasks that do work for this system.
     * 
     * @param config
     *            the system configuration properties
     * @param metricRegistry
     *            the registry of metrics used to track system performance information
     * @param healthCheckRegistry
     *            the registry of system health and status information
     */
    protected void launchBackgroundTasks(final Config config, final MetricRegistry metricRegistry,
            final HealthCheckRegistry healthCheckRegistry) {
        final ScheduledExecutorService backgroundTaskRunner = Executors.newScheduledThreadPool(3);

        final BackgroundTask systemHealth = new SystemHealthCheckTask(this.config, this.metricRegistry,
                this.healthCheckRegistry);
        final BackgroundTask memoryUsage = new MemoryUsageLoggingTask(this.config, this.metricRegistry,
                this.healthCheckRegistry);

        final Collection<BackgroundTask> childTasks = getBackgroundTasks(config, metricRegistry, healthCheckRegistry);

        final List<BackgroundTask> tasks = new LinkedList<>();
        if (childTasks != null)
            tasks.addAll(childTasks);
        tasks.add(systemHealth);
        tasks.add(memoryUsage);

        for (final BackgroundTask task : tasks)
            task.schedule(backgroundTaskRunner);
    }
}
