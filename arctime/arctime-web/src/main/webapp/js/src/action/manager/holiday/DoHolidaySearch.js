
Ext.namespace("action.manager.holiday");

action.manager.holiday.DoHolidaySearch = function() {
	return new Ext.Action({
		id:      'action.manager.holiday.doholidaysearch',
		iconCls: 'icon-search',
		handler: function() {
			// Get the text field.
			var txt = Ext.getCmp('ui.field.manager.holiday.search').getValue();

			// Get the grid.
			var grid = Ext.getCmp('ui.grid.manager.holidaygrid');

			// Check to see if the text is valid.
			if (txt != undefined && txt.length > 0) {
				// Get the regular expression.
				var r = new RegExp(txt, 'i');

				// Add the filter to the store.
				grid.getStore().filterBy(function(rec, recId) {
					// Check the searchable fields.
					var day = Ext.Date.format(new Date(rec.get('day')), 'D, M j, Y');
					return rec.get('description').match(r) ||
						   rec.get('config').match(r) ||
						   day.match(r);
				});
			} else
				// Clear the filter on the store.
				grid.getStore().clearFilter();
		}
	});
}

