
Ext.namespace("ui.panel.manager.task");

ui.panel.manager.task.TaskAddPanel = Ext.extend(Ext.form.FormPanel, {
	constructor: function(c) {
		var config = Ext.applyIf(c || {}, {
			id:         'ui.panel.manager.task.taskaddpanel',
			title:      'Add a new Task',
			width:      450,
			autoHeight: true,
			bodyStyle:  'padding: 10px;',
			labelWidth: 120,
			items: [
				{
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
							id:         'task-admin-yes',
							inputValue: 1,
							checked:    false,
							style:      'border: 0px;'
						}, {
							boxLabel:   'No',
							name:       'administrative',
							id:         'task-admin-no',
							inputValue: 0,
							checked:    true,
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
							id:         'task-active-yes',
							inputValue: 1,
							checked:    true,
							style:      'border: 0px;'
						}, {
							boxLabel:   'No',
							name:       'active',
							id:         'task-active-no',
							inputValue: 0,
							checked:    false,
							style:      'border: 0px;'
						}
					]
				}
			],
			buttons: [
				new Ext.Button(new action.manager.task.DoTaskAdd()),
				new Ext.Button(new action.manager.task.ShowTaskGrid())
			]
		});

		ui.panel.manager.task.TaskAddPanel.superclass.constructor.call(this, config);
	},

	setInitialFocus: function() {
		this.getForm().findField('description').focus();
	}
});

