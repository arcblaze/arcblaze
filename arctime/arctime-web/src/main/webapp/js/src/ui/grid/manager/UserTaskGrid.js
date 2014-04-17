
Ext.namespace("ui.grid.manager");

ui.grid.manager.UserTaskGrid = Ext.extend(Ext.grid.GridPanel, {
	constructor: function(c) {
		// Make sure the correct parameters were specified.
		if (!c || !c.user)
			throw "UserTaskGrid requires a user.";

		var userTask = new data.model.UserTask();

		var config = Ext.applyIf(c || {}, {
			id:               'ui.grid.manager.usertaskgrid',
			title:            'Tasks Assigned to ' +
								c.user.data.fullName,
			multiSelect:      true,
			stripeRows:       true,
			autoExpandColumn: 'description',
			autoWidth:        true,
			autoHeight:       true,
			columns:          userTask.getColumnModel(),
			loadMask:         true,
			store: new data.store.manager.UserTaskStore({
				user: c.user,
				day:  c.day
			}),
			tbar: new ui.tbar.manager.UserTaskToolbar({
				user: c.user,
				day:  c.day
			})
		});

		ui.grid.manager.UserTaskGrid.superclass.constructor.call(this, config);

		this.getSelectionModel().addListener('selectionchange', function(model) {
			// Get the number of selected rows.
			var count = model.selected.items.length;

			// Get the buttons.
			var taskDel = Ext.getCmp('action.manager.usertask.doassignmentdelete');
			var taskUpd = Ext.getCmp('action.manager.usertask.showassignmentupdate');

			// Update the buttons based on the selected rows.
			(count > 0) ? taskDel.enable() : taskDel.disable();
			(count == 1) ? taskUpd.enable() : taskUpd.disable();
		});
	},

	getSelectedIds: function() {
		// This will hold all the ids.
		var ids = [ ];

		// Get the selected records.
		var records = this.getSelectionModel().selected.items;

		// Iterate over the selected records.
		for (var i = 0; i < records.length; i++)
			// Add the id to the list.
			ids.push(records[i].data.id);

		// Return the ids.
		return ids;
	}
});

