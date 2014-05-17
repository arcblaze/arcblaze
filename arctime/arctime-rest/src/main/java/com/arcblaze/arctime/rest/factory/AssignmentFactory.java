package com.arcblaze.arctime.rest.factory;

import java.text.ParseException;

import javax.ws.rs.FormParam;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import com.arcblaze.arccore.rest.factory.BaseFactory;
import com.arcblaze.arctime.common.model.Assignment;
import com.arcblaze.arctime.common.model.Task;

/**
 * Provides access to {@link Task} objects within the REST resource classes, based on parameters from the web request.
 */
public class AssignmentFactory extends BaseFactory<Assignment> {
    private final static String[] FMT = { "MM/dd/yyyy", "yyyy-MM-dd" };

    private final Integer id;
    private final Integer companyId;
    private final Integer taskId;
    private final Integer userId;
    private final String beginstr;
    private final String endstr;
    private final String laborCat;
    private final String itemName;

    /**
     * @param id
     *            the unique id of the user
     * @param companyId
     *            the unique id of the company associated with the assignment
     * @param taskId
     *            the unique id of the task associated with the assignment
     * @param userId
     *            the unique id of the user associated with the assignment
     * @param beginstr
     *            the date indicating the beginning of the assignment
     * @param endstr
     *            the date indicating the ending of the assignment
     * @param laborCat
     *            the labor category of the user in the assignment
     * @param itemName
     *            the item name of the assignment, as used in the accounting system
     */
    public AssignmentFactory(@FormParam("id") final Integer id, @FormParam("companyId") final Integer companyId,
            @FormParam("taskId") final Integer taskId, @FormParam("userId") final Integer userId,
            @FormParam("begin") final String beginstr, @FormParam("end") final String endstr,
            @FormParam("laborCat") final String laborCat, @FormParam("itemName") final String itemName) {
        this.id = id;
        this.companyId = companyId;
        this.taskId = taskId;
        this.userId = userId;
        this.beginstr = beginstr;
        this.endstr = endstr;
        this.laborCat = StringEscapeUtils.escapeHtml(laborCat);
        this.itemName = StringEscapeUtils.escapeHtml(itemName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Assignment provide() {
        final Assignment assignment = new Assignment();
        if (this.id != null)
            assignment.setId(this.id);
        if (this.companyId != null)
            assignment.setCompanyId(this.companyId);
        if (this.taskId != null)
            assignment.setTaskId(this.taskId);
        if (this.userId != null)
            assignment.setUserId(this.userId);
        if (StringUtils.isNotBlank(this.laborCat))
            assignment.setLaborCat(this.laborCat);
        if (StringUtils.isNotBlank(this.itemName))
            assignment.setItemName(this.itemName);
        if (StringUtils.isNotBlank(this.beginstr)) {
            try {
                assignment.setBegin(DateUtils.parseDate(this.beginstr, FMT));
            } catch (final ParseException badDate) {
                throw new IllegalArgumentException("Invalid assignment begin date: " + this.beginstr, badDate);
            }
        }
        if (StringUtils.isNotBlank(this.endstr)) {
            try {
                assignment.setEnd(DateUtils.parseDate(this.endstr, FMT));
            } catch (final ParseException badDate) {
                throw new IllegalArgumentException("Invalid assignment end date: " + this.endstr, badDate);
            }
        }
        return assignment;
    }

    /**
     * @return a binder that can register this factory
     */
    public static AbstractBinder getBinder() {
        return new AbstractBinder() {
            @Override
            protected void configure() {
                bindFactory(AssignmentFactory.class).to(Assignment.class);
            }
        };
    }
}
