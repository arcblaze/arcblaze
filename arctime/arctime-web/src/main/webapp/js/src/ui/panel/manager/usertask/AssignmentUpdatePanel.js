
Ext.namespace("ui.panel.manager.usertask");

ui.panel.manager.usertask.AssignmentUpdatePanel = Ext.extend(Ext.form.FormPanel, {
	constructor: function(c) {
		if (!c || !c.user)
			throw "AssignmentUpdatePanel requires an user to be provided.";
		this.user = c.user;

		var config = Ext.applyIf(c || {}, {
			id:         'ui.panel.manager.usertask.assignmentupdatepanel',
			title:      'Update Task Assignment',
			width:      470,
			autoHeight: true,
			bodyStyle:  'padding: 10px;',
			labelWidth: 120,
			items: [
				{
					xtype: 'hidden',
					name:  'id'
				}, {
					xtype: 'hidden',
					name:  'userId',
					value: c.user.data.id
				}, {
					xtype: 'hidden',
					name:  'taskId'
				}, {
					xtype:      'textfield',
					name:       'task',
					fieldLabel: 'Task',
					width:      400,
					disabled:   true
				}, {
					xtype:      'textfield',
					name:       'user',
					fieldLabel: 'User',
					width:      400,
					disabled:   true
                }, {
		            xtype: 'label',
		            html:  '<div style="padding:0px 5px 6px 104px;">Choose ' +
		                   'a date range during which the user can bill hours ' +
		                   'to the above task.' +
		                   '</div>'
				}, {
					xtype:      'datefield',
					fieldLabel: 'Assignment Start',
					name:       'begin',
					width:      220
				}, {
					xtype:      'datefield',
					fieldLabel: 'Assignment End',
					name:       'end',
					width:      220
                }, {
                    xtype: 'label',
                    html:  '<div style="padding:0px 5px 6px 104px;">Provide ' +
                           'the labor category used to describe the type of ' +
                           'work being performed on the task.' +
                           '</div>'
				}, {
					xtype:      'textfield',
					fieldLabel: 'Labor Category',
					name:       'laborCat',
					width:      400
                }, {
                    xtype: 'label',
                    html:  '<div style="padding:0px 5px 6px 104px;">Provide ' +
                           'the name of the item associated with this ' +
                           'assignment in the accounting system. (See ' +
                           'information on <a href="/integration/">Integration' +
                           '</a> for more information.) Example: ' +
                           '<li>John Doe:Example Task</li>' +
                           '</div>'
				}, {
					xtype:      'textfield',
					fieldLabel: 'Item Name',
					name:       'itemName',
					width:      400
				}
			],
			buttons: [
				new Ext.Button(new action.manager.usertask.DoAssignmentUpdate()),
				new Ext.Button(new action.manager.usertask.ShowUserTaskGrid())
			]
		});

		ui.panel.manager.usertask.AssignmentUpdatePanel.superclass.constructor.call(this, config);
	},

	setInitialFocus: function() {
		this.getForm().findField('begin').focus();
	},

	setValues: function(assignment) {
		// Set the form values.
		this.getForm().findField('id').
			setValue(assignment.data.id);
		this.getForm().findField('userId').
			setValue(assignment.data.userId);
		this.getForm().findField('taskId').
			setValue(assignment.data.taskId);
		this.getForm().findField('user').
			setValue(this.user.data.fullName +
					' (' + this.user.data.login + ')');
		this.getForm().findField('task').
			setValue(assignment.data.description +
					' (' + assignment.data.jobCode + ')');
		this.getForm().findField('begin').setValue(
				new Date(assignment.data.begin));
		this.getForm().findField('end').setValue(
				new Date(assignment.data.end));
		this.getForm().findField('laborCat').
			setValue(assignment.data.laborCat);
		this.getForm().findField('itemName').
			setValue(assignment.data.itemName);

		// Set the form focus.
		this.setInitialFocus();
	}
});

