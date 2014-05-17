package com.arcblaze.arctime.rest.factory;

import javax.ws.rs.FormParam;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import com.arcblaze.arccore.rest.factory.BaseFactory;
import com.arcblaze.arctime.common.model.Task;

/**
 * Provides access to {@link Task} objects within the REST resource classes, based on parameters from the web request.
 */
public class TaskFactory extends BaseFactory<Task> {
    private final Integer id;
    private final Integer companyId;
    private final String description;
    private final String jobCode;
    private final Boolean administrative;
    private final Boolean active;

    /**
     * @param id
     *            the unique id of the user
     * @param companyId
     *            the unique id of the company associated with the task
     * @param description
     *            the task description
     * @param jobCode
     *            the task job code
     * @param administrative
     *            whether the task is administrative
     * @param active
     *            whether the task is active or not
     */
    public TaskFactory(@FormParam("id") final Integer id, @FormParam("companyId") final Integer companyId,
            @FormParam("description") final String description, @FormParam("jobCode") final String jobCode,
            @FormParam("administrative") final Boolean administrative, @FormParam("active") final Boolean active) {
        this.id = id;
        this.companyId = companyId;
        this.description = StringEscapeUtils.escapeHtml(description);
        this.jobCode = StringEscapeUtils.escapeHtml(jobCode);
        this.administrative = administrative;
        this.active = active;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Task provide() {
        final Task task = new Task();
        if (this.id != null)
            task.setId(this.id);
        if (this.companyId != null)
            task.setCompanyId(this.companyId);
        if (StringUtils.isNotBlank(this.description))
            task.setDescription(this.description);
        if (StringUtils.isNotBlank(this.jobCode))
            task.setJobCode(this.jobCode);
        if (this.administrative != null)
            task.setAdministrative(this.administrative);
        if (this.active != null)
            task.setActive(this.active);
        return task;
    }

    /**
     * @return a binder that can register this factory
     */
    public static AbstractBinder getBinder() {
        return new AbstractBinder() {
            @Override
            protected void configure() {
                bindFactory(TaskFactory.class).to(Task.class);
            }
        };
    }
}
