
Ext.namespace("action.manager.task");

action.manager.task.DoTaskSearch = function() {
	return new Ext.Action({
		id:      'action.manager.task.dotasksearch',
		iconCls: 'icon-search',
		handler: function() {
			// Get the text field.
			var txt = Ext.getCmp('ui.field.manager.task.search').getValue();

			// Get the grid.
			var grid = Ext.getCmp('ui.grid.manager.taskgrid');

			// Check to see if the text is valid.
			if (txt != undefined && txt.length > 0) {
				// Get the regular expression.
				var r = new RegExp(txt, 'i');

				// Add the filter to the store.
				grid.getStore().filterBy(function(rec, recId) {
					// Check the searchable fields.
					return rec.data.description.match(r) ||
						   rec.data.jobCode.match(r);
				});
			} else
				// Clear the filter on the store.
				grid.getStore().clearFilter();
		}
	});
}

