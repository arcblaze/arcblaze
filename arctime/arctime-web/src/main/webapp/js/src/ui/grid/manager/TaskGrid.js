
Ext.namespace("ui.grid.manager");

ui.grid.manager.TaskGrid = Ext.extend(Ext.grid.GridPanel, {
	constructor: function(c) {
		var task = new data.model.Task();

		var config = Ext.applyIf(c || {}, {
			title:            'Tasks',
			id:               'ui.grid.manager.taskgrid',
			store:            new data.store.manager.TaskStore(),
            multiSelect:      true,
			stripeRows:       true,
			autoExpandColumn: 'description',
			autoWidth:        true,
			autoHeight:       true,
			tbar:             new ui.tbar.manager.TaskToolbar(),
			columns:          task.getColumnModel(),
			loadMask:         true
		});

		ui.grid.manager.TaskGrid.superclass.constructor.call(this, config);

		this.getSelectionModel().addListener('selectionchange', function(model) {
			// Get the number of selected rows.
			var count = model.selected.items.length;

			// Get the buttons.
			var taskDel = Ext.getCmp('action.manager.task.dotaskdelete');
			var taskAct = Ext.getCmp('action.manager.task.dotaskactivate');
			var taskDea = Ext.getCmp('action.manager.task.dotaskdeactivate');
			var taskUpd = Ext.getCmp('action.manager.task.showtaskupdate');
			var taskUsr = Ext.getCmp('action.manager.task.showtaskusers');

			var allActive = true;
			for (var s = 0; s < count && allActive; s++)
				allActive = model.selected.items[s].data.active;

			var allInactive = true;
			for (var s = 0; s < count && allInactive; s++)
				allInactive = !model.selected.items[s].data.active;

			// Update the buttons based on the selected rows.
			if (taskDel)
				(count > 0) ? taskDel.enable() : taskDel.disable();
			if (taskUpd)
				(count == 1) ? taskUpd.enable() : taskUpd.disable();
			if (taskUsr)
				(count == 1 && model.selected.items[0].data.admin == "0") ?
					taskUsr.enable() : taskUsr.disable();
			if (taskAct)
				(count > 0 && allInactive) ?
					taskAct.enable() : taskAct.disable();
			if (taskDea)
				(count > 0 && allActive) ?
					taskDea.enable() : taskDea.disable();
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

