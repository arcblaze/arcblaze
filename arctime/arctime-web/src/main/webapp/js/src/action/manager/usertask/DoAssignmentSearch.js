
Ext.namespace("action.manager.usertask");

action.manager.usertask.DoAssignmentSearch = function(user) {
	return new Ext.Action({
		id:      'action.manager.usertask.doassignmentsearch',
		iconCls: 'icon-search',
		handler: function() {
			// Get the text field.
			var txt = Ext.getCmp('ui.field.manager.usertask.search').getValue();

			// Get the grid.
			var grid = Ext.getCmp('ui.grid.manager.usertaskgrid');

			// Check to see if the text is valid.
			if (txt != undefined && txt.length > 0) {
				// Get the regular expression.
				var r = new RegExp(txt, 'i');

				// Add the filter to the store.
				grid.getStore().filterBy(function(rec, recId) {
					// Check the searchable fields.
					return rec.data.description.match(r) ||
						   rec.data.jobCode.match(r) ||
						   rec.data.laborCat.match(r) ||
						   rec.data.itemName.match(r) ||
						   (rec.data.start && rec.data.start.match(r)) ||
						   (rec.data.end && rec.data.end.match(r));
				});
			} else
				// Clear the filter on the store.
				grid.getStore().clearFilter();
		}
	});
}

