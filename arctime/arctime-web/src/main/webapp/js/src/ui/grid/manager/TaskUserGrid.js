
Ext.namespace("ui.grid.manager");

ui.grid.manager.TaskUserGrid = Ext.extend(Ext.grid.GridPanel, {
	constructor: function(c) {
		// Make sure the correct parameters were specified.
		if (!c || !c.task)
			throw "TaskUserGrid requires a task.";

		var taskUser = new data.model.TaskUser();

		var config = Ext.applyIf(c || {}, {
			id:               'ui.grid.manager.taskusergrid',
			title:            'Users Assigned to ' +
								c.task.data.description,
			multiSelect:      true,
			stripeRows:       true,
			autoExpandColumn: 'fullName',
			autoWidth:        true,
			autoHeight:       true,
			columns:          taskUser.getColumnModel(),
			loadMask:         true,
			store: new data.store.manager.TaskUserStore({
				task: c.task,
				day:  c.day
			}),
			tbar: new ui.tbar.manager.TaskUserToolbar({
				task: c.task,
				day:  c.day
			})
		});

		ui.grid.manager.TaskUserGrid.superclass.constructor.call(this, config);

		this.getSelectionModel().addListener('selectionchange', function(model) {
			// Get the number of selected rows.
			var count = model.selected.items.length;

			// Get the buttons.
			var userDel = Ext.getCmp('action.manager.taskuser.doassignmentdelete');
			var userUpd = Ext.getCmp('action.manager.taskuser.showassignmentupdate');

			// Update the buttons based on the selected rows.
			(count > 0) ? userDel.enable() : userDel.disable();
			(count == 1) ? userUpd.enable() : userUpd.disable();
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

