
Ext.namespace("ui.tbar.manager");

ui.tbar.manager.UserTaskToolbar = Ext.extend(Ext.Toolbar, {
	constructor: function(c) {
		// Make sure the correct parameters were specified.
		if (!c || !c.user)
			throw "UserTaskToolbar requires an user.";

		var config = Ext.applyIf(c || {}, {
			items: [
				new action.manager.usertask.ShowAssignmentAdd(c.user),
				new action.manager.usertask.ShowAssignmentUpdate(c.user),
				new action.manager.usertask.DoAssignmentDelete(c.user),

				'-',

				new action.manager.user.ShowUserGrid(),

				'->',

				new Ext.form.Label({
					text: 'Active on:',
					style: 'padding-right:10px;'
				}),
				new Ext.form.DateField({
					id: 'ui.field.manager.usertask.asof',
					value: c.day,
					listeners: {
						select: function(field, newVal) {
							var grid = Ext.getCmp('ui.grid.manager.usertaskgrid');
							grid.store.setAsOf(newVal);
						},
						change: function(field, newVal, oldVal) {
							var grid = Ext.getCmp('ui.grid.manager.usertaskgrid');
							grid.store.setAsOf(newVal);
						}
					}
				}),

				'-',

				new Ext.form.TextField({
					id: 'ui.field.manager.usertask.search',
					width: 100,
					listeners: {
						specialkey: function(tf, evt) {
							// Listen for the Enter key.
							if (evt.ENTER == evt.getKey()) {
								// Get the search action.manager.
								var search = Ext.getCmp(
									'action.manager.usertask.doassignmentsearch');

								// Invoke the handler.
								search.handler();
							}
						}
					}
				}),
				new action.manager.usertask.DoAssignmentSearch(c.user)
			]
		});

		ui.tbar.manager.UserTaskToolbar.superclass.constructor.call(this, config);
	}
});

