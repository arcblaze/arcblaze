
Ext.namespace("ui.grid.user");

ui.grid.user.SupervisorGrid = Ext.extend(Ext.grid.GridPanel, {
	constructor: function(c) {
		if (!c || !c.user)
			throw "SupervisorGrid requires a user.";

		// Determine if the toolbar should be included.
		var tbar = false;
		if (typeof(c.includeToolbar) == "undefined" || c.includeToolbar)
			tbar = true;

		var grid = this;

		var supervisor = new data.model.Supervisor();

		var config = Ext.applyIf(c || {}, {
			title:      'Supervisors',
			id:         'ui.grid.user.supervisorgrid',
			stripeRows: true,
			width:      520,
			height:     300,
			loadMask:   true,
			columns:    supervisor.getColumnModel(),
			store: new data.store.user.SupervisorStore({
				user: c.user
			})
		});

		if (tbar)
			Ext.apply(config, {
				tbar: new ui.tbar.user.SupervisorToolbar({
					user: c.user
				})
			});

		ui.grid.user.SupervisorGrid.superclass.constructor.call(this, config);

		if (tbar) {
			this.getSelectionModel().addListener('selectionchange', function(model) {
				// Get the number of selected rows.
				var count = model.getSelections().length;

				// Get the total number of rows.
				var total = grid.store.getCount();

				// Get the buttons.
				var supervisorDel = Ext.getCmp('action.user.supervisor.dosupervisordelete');

				// Update the buttons based on the selected rows.
				if (supervisorDel)
					(count > 0 && total - count >= 1) ?
						supervisorDel.enable() : supervisorDel.disable();
			});
		}
	},

	getSelectedIds: function() {
		// This will hold all the ids.
		var ids = [ ];

		// Get the selected records.
		var records = this.getSelectionModel().getSelections();

		// Iterate over the selected records.
		for (var i = 0; i < records.length; i++)
			// Add the id to the list.
			ids.push(records[i].data.id);

		// Return the ids.
		return ids;
	}
});

