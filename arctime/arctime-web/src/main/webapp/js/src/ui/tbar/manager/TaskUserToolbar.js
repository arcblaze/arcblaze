
Ext.namespace("ui.tbar.manager");

ui.tbar.manager.TaskUserToolbar = Ext.extend(Ext.Toolbar, {
	constructor: function(c) {
		// Make sure the correct parameters were specified.
		if (!c || !c.task)
			throw "TaskUserToolbar requires a task.";

		var config = Ext.applyIf(c || {}, {
			items: [
				new action.manager.taskuser.ShowAssignmentAdd(c.task),
				new action.manager.taskuser.ShowAssignmentUpdate(c.task),
				new action.manager.taskuser.DoAssignmentDelete(c.task),

				'-',

				new action.manager.task.ShowTaskGrid(),

				'->',

				new Ext.form.Label({
					text: 'As of',
					style: 'padding-right:10px;'
				}),
				new Ext.form.DateField({
					id: 'ui.field.manager.taskuser.asof',
					value: c.day,
					listeners: {
						select: function(field, val) {
							var grid = Ext.getCmp('ui.grid.manager.taskusergrid');
							grid.getStore().setAsOf(val);
						},
						change: function(field, newVal, oldVal) {
							var grid = Ext.getCmp('ui.grid.manager.taskusergrid');
							grid.getStore().setAsOf(newVal);
						}
					}
				}),

				'-',

				new Ext.form.TextField({
					id: 'ui.field.manager.taskuser.search',
					width: 100,
					listeners: {
						specialkey: function(tf, evt) {
							// Listen for the Enter key.
							if (evt.ENTER == evt.getKey()) {
								// Get the search action.manager.
								var search = Ext.getCmp(
									'action.manager.taskuser.doassignmentsearch');

								// Invoke the handler.
								search.handler();
							}
						}
					}
				}),
				new action.manager.taskuser.DoAssignmentSearch(c.task)
			]
		});

		ui.tbar.manager.TaskUserToolbar.superclass.constructor.call(this, config);
	}
});

