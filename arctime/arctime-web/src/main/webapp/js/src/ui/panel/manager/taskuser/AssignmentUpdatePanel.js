
Ext.namespace("ui.panel.manager.taskuser");

ui.panel.manager.taskuser.AssignmentUpdatePanel = Ext.extend(Ext.form.FormPanel, {
	constructor: function(c) {
		if (!c || !c.task)
			throw "AssignmentUpdatePanel requires a task to be provided.";
		this.task = c.task;

		var config = Ext.applyIf(c || {}, {
			id:         'ui.panel.manager.taskuser.assignmentupdatepanel',
			title:      'Update Task Assignment',
			width:      450,
			autoHeight: true,
			bodyStyle:  'padding: 10px;',
			labelWidth: 120,
			items: [
				{
					xtype: 'hidden',
					name:  'id'
				}, {
					xtype: 'hidden',
					name:  'taskId',
					value: c.task.id
				}, {
					xtype: 'hidden',
					name:  'userId'
				}, {
					xtype:      'textfield',
					name:       'task',
					fieldLabel: 'Task',
					width:      250,
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
				new Ext.Button(new action.manager.taskuser.DoAssignmentUpdate()),
				new Ext.Button(new action.manager.taskuser.ShowTaskUserGrid())
			]
		});

		ui.panel.manager.taskuser.AssignmentUpdatePanel.superclass.constructor.call(this, config);
	},

	setInitialFocus: function() {
		this.getForm().findField('start').focus();
	},

	setValues: function(assignment) {
		// Set the form values.
		this.getForm().findField('id').
			setValue(assignment.data.id);
		this.getForm().findField('taskId').
			setValue(assignment.data.taskId);
		this.getForm().findField('userId').
			setValue(assignment.data.userId);
		this.getForm().findField('task').
			setValue(this.task.data.description);
		this.getForm().findField('user').
			setValue(assignment.data.fullName);
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

