
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
					value: c.user.id
				}, {
					xtype: 'hidden',
					name:  'taskId'
				}, {
					xtype:      'textfield',
					name:       'task',
					fieldLabel: 'Task',
					width:      320,
					disabled:   true
				}, {
					xtype:      'textfield',
					name:       'user',
					fieldLabel: 'User',
					width:      250,
					disabled:   true
				}, {
					xtype:      'datefield',
					fieldLabel: 'Assignment Start',
					name:       'start'
				}, {
					xtype:      'datefield',
					fieldLabel: 'Assignment End',
					name:       'end'
				}, {
					xtype:      'textfield',
					fieldLabel: 'Labor Category',
					name:       'laborCat',
					width:      300
				}, {
					xtype:      'textfield',
					fieldLabel: 'Item Name',
					name:       'itemName',
					width:      300
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
		this.getForm().findField('start').focus();
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
			setValue(this.user.data.fullName);
		this.getForm().findField('task').
			setValue(assignment.data.description);
		this.getForm().findField('start').setValue(assignment.data.start);
		this.getForm().findField('end').setValue(assignment.data.end);
		this.getForm().findField('laborCat').
			setValue(assignment.data.laborCat);
		this.getForm().findField('itemName').
			setValue(assignment.data.itemName);

		// Set the form focus.
		this.setInitialFocus();
	}
});

