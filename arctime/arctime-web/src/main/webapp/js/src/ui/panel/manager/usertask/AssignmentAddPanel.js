
Ext.namespace("ui.panel.manager.usertask");

ui.panel.manager.usertask.AssignmentAddPanel = Ext.extend(Ext.form.FormPanel, {
	constructor: function(c) {
		if (!c || !c.user)
			throw "AssignmentAddPanel requires an user to be provided.";

		var config = Ext.applyIf(c || {}, {
			id:         'ui.panel.manager.usertask.assignmentaddpanel',
			title:      'Add a new Task Assignment',
			width:      470,
			autoHeight: true,
			bodyStyle:  'padding: 10px;',
			labelWidth: 120,
			items: [
				{
					xtype: 'hidden',
					name:  'userId',
					value: c.user.id
				}, new Ext.form.ComboBox({
					fieldLabel:     'Task',
					name:           'taskId',
					displayField:   'description',
					valueField:     'id',
					hiddenName:     'taskId',
					mode:           'local',
					forceSelection: true,
					triggerAction:  'all',
					selectOnFocus:  true,
					width:          320,
					allowBlank:     false,
					store: new data.store.manager.TaskStore({
						includeAdministrative: false,
						includeInactive: false
					})
				}), {
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
					width:      300,
					allowBlank: false
				}, {
					xtype:      'textfield',
					fieldLabel: 'Item Name',
					name:       'itemName',
					width:      300,
					allowBlank: false
				}
			],
			buttons: [
				new Ext.Button(new action.manager.usertask.DoAssignmentAdd()),
				new Ext.Button(new action.manager.usertask.ShowUserTaskGrid())
			]
		});

		ui.panel.manager.usertask.AssignmentAddPanel.superclass.constructor.call(this, config);
	},

	setInitialFocus: function() {
		this.getForm().findField('taskId').focus();
	}
});

