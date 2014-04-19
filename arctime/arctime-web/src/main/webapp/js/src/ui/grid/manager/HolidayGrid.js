
Ext.namespace("ui.grid.manager");

ui.grid.manager.HolidayGrid = Ext.extend(Ext.grid.GridPanel, {
	constructor: function(c) {
		var holiday = new data.model.Holiday();

		var config = Ext.applyIf(c || {}, {
			title:       'Holidays',
			id:          'ui.grid.manager.holidaygrid',
			store:       new data.store.manager.HolidayStore(),
			multiSelect: true,
			stripeRows:  true,
			width:       660,
			autoHeight:  true,
			maxHeight:   ((document.height !== undefined) ?
					document.height : document.body.offsetHeight) - 145,
			tbar:        new ui.tbar.manager.HolidayToolbar(),
			columns:     holiday.getColumnModel(),
			loadMask:    true
		});

		ui.grid.manager.HolidayGrid.superclass.constructor.call(this, config);

		this.getSelectionModel().addListener('selectionchange', function(model) {
			// Get the number of selected rows.
			var count = model.selected.items.length;

			// Get the buttons.
			var holidayDel = Ext.getCmp('action.manager.holiday.doholidaydelete');
			var holidayUpd = Ext.getCmp('action.manager.holiday.showholidayupdate');

			// Update the buttons based on the selected rows.
			if (holidayDel)
				(count > 0) ? holidayDel.enable() : holidayDel.disable();
			if (holidayUpd)
				(count == 1) ? holidayUpd.enable() : holidayUpd.disable();
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

