
Ext.namespace("ui.grid.manager");

ui.grid.manager.SupervisorGrid = Ext.extend(Ext.grid.GridPanel, {
	constructor: function(c) {
		if (!c || !c.user)
			throw "SupervisorGrid requires a user.";

		var grid = this;

		var supervisor = new data.model.Supervisor();

		var config = Ext.applyIf(c || {}, {
			title:       'Supervisors',
			id:          'ui.grid.manager.supervisorgrid',
			stripeRows:  true,
			width:       320,
			height:      285,
			loadMask:    true,
			multiSelect: true,
			columns:     supervisor.getColumnModel(),
			store: new data.store.manager.SupervisorStore({
				user: c.user
			}),
			tbar: new ui.tbar.manager.SupervisorToolbar({
				user: c.user
			})
		});

		ui.grid.manager.SupervisorGrid.superclass.constructor.call(this, config);

		this.getSelectionModel().addListener('selectionchange', function(model) {
			// Get the number of selected rows.
			var count = model.selected.items.length;

			// Get the total number of rows.
			var total = grid.store.getCount();

			// Get the buttons.
			var supervisorDel = Ext.getCmp('action.manager.supervisor.dosupervisordelete');

			// Update the buttons based on the selected rows.
			if (supervisorDel)
				(count > 0 && total - count >= 1) ?
					supervisorDel.enable() : supervisorDel.disable();
		});
	},

	getSelectedIds: function() {
		// This will hold all the ids.
		var ids = [ ];

		// Get the selected records.
		var records = this.selModel.selected.items;

		// Iterate over the selected records.
		for (var i = 0; i < records.length; i++)
			// Add the id to the list.
			ids.push(records[i].data.id);

		// Return the ids.
		return ids;
	}
});

