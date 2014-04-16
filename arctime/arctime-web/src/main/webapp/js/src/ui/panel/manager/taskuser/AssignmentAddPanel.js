
Ext.namespace("ui.panel.manager.taskuser");

ui.panel.manager.taskuser.AssignmentAddPanel = Ext.extend(Ext.form.FormPanel, {
	constructor: function(c) {
		if (!c || !c.task)
			throw "AssignmentAddPanel requires a task to be provided.";

		var config = Ext.applyIf(c || {}, {
			id:         'ui.panel.manager.taskuser.assignmentaddpanel',
			title:      'Add a new Task Assignment',
			width:      450,
			autoHeight: true,
			bodyStyle:  'padding: 10px;',
			labelWidth: 120,
			items: [
				{
					xtype: 'hidden',
					name:  'taskId',
					value: c.task.id
				}, new Ext.form.ComboBox({
					fieldLabel:     'User',
					name:           'userId',
					displayField:   'fullName',
					valueField:     'id',
					hiddenName:     'userId',
					mode:           'local',
					forceSelection: true,
					triggerAction:  'all',
					selectOnFocus:  true,
					width:          210,
					allowBlank:     false,
					store: new data.store.manager.UserStore({
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
				new Ext.Button(new action.manager.taskuser.DoAssignmentAdd()),
				new Ext.Button(new action.manager.taskuser.ShowTaskUserGrid())
			]
		});

		ui.panel.manager.taskuser.AssignmentAddPanel.superclass.constructor.call(this, config);
	},

	setInitialFocus: function() {
		this.getForm().findField('userId').focus();
	}
});

