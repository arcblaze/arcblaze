
Ext.namespace("ui.panel.manager.task");

ui.panel.manager.task.TaskUpdatePanel = Ext.extend(Ext.form.FormPanel, {
	constructor: function(c) {
		var config = Ext.applyIf(c || {}, {
			id:         'ui.panel.manager.task.taskupdatepanel',
			title:      'Update Task',
			width:      450,
			autoHeight: true,
			bodyStyle:  'padding: 10px;',
			labelWidth: 120,
			items: [
				{
					xtype: 'hidden',
					name:  'id'
				}, {
					xtype:      'label',
					html:       '<div style="padding:0px 5px 6px 104px;">' +
								'A brief description of the task to be displayed ' +
								'on user timesheets. For sub-contracts, it may ' +
								'make sense to include the prime company name also. ' +
								'Examples:' +
								'<li>Initech - TPS Redesign</li>' +
								'<li>Manhattan Project</li>' +
								'<li>Corporate Training</li>' +
								'</div>'
				}, {
					xtype:      'textfield',
					fieldLabel: 'Description',
					name:       'description',
					allowBlank: false,
					width:      400
				}, {
					xtype:      'label',
					html:       '<div style="padding:0px 5px 6px 104px;">' +
								'Used to uniquely identify this job in accounting ' +
								'and invoicing systems. (See information on ' +
								'<a href="/integration/">Integration</a> for more ' +
								'information.) Examples: ' +
								'<li>Initech:TPS Redesign</li>' +
								'<li>MyCompany:Manhattan Project</li>' +
								'<li>MyCompany:Corporate Training</li>' +
								'</div>'
				}, {
					xtype:      'textfield',
					fieldLabel: 'Job Code',
					name:       'jobCode',
					allowBlank: false,
					width:      400
				}, {
					xtype:      'label',
					html:       '<div style="padding:0px 5px 6px 104px;">' +
								'Administrative tasks are available to all users ' +
								'on every timesheet, without restrictions. Common ' +
								'examples of administrative tasks include Overhead, ' +
								'Holiday, Paid Time Off, Training, etc. If only ' +
								'specific users should see a task, or if a task can ' +
								'only be used during a specific date range, then ' +
								'it should not be administrative.' +
								'</div>'
				}, {
					xtype:      'radiogroup',
					fieldLabel: 'Administrative',
					name:       'administrative',
					width:      240,
					items: [
						{
							boxLabel:   'Yes',
							name:       'administrative',
							id:         'task-admin-modify-yes',
							inputValue: 'true',
							style:      'border: 0px;'
						}, {
							boxLabel:   'No',
							name:       'administrative',
							id:         'task-admin-modify-no',
							inputValue: 'false',
							style:      'border: 0px;'
						}
					]
				}, {
					xtype:      'label',
					html:       '<div style="padding:0px 5px 6px 104px;">' +
								'Defines whether the task is available for use ' +
								'on user timesheets.' +
								'</div>'
				}, {
					xtype:      'radiogroup',
					fieldLabel: 'Active',
					name:       'active',
					width:      240,
					items: [
						{
							boxLabel:   'Yes',
							name:       'active',
							id:         'task-active-modify-yes',
							inputValue: 'true',
							style:      'border: 0px;'
						}, {
							boxLabel:   'No',
							name:       'active',
							id:         'task-active-modify-no',
							inputValue: 'false',
							style:      'border: 0px;'
						}
					]
				}
			],
			buttons: [
				new Ext.Button(new action.manager.task.DoTaskUpdate()),
				new Ext.Button(new action.manager.task.ShowTaskGrid())
			]
		});

		ui.panel.manager.task.TaskUpdatePanel.superclass.constructor.call(this, config);
	},

	setInitialFocus: function() {
		this.getForm().findField('description').focus();
	},

	setValues: function(task) {
		// Set the form values.
		this.getForm().findField('id').setValue(task.data.id);
		this.getForm().findField('description').
			setValue(task.data.description);
		this.getForm().findField('jobCode').setValue(task.data.jobCode);
			
		Ext.getCmp('task-admin-modify-yes').setValue(task.data.administrative);
		Ext.getCmp('task-admin-modify-no').setValue(!task.data.administrative);
		Ext.getCmp('task-active-modify-yes').setValue(task.data.active);
		Ext.getCmp('task-active-modify-no').setValue(!task.data.active);

		// Set the form focus.
		this.setInitialFocus();
	}
});

