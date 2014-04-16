
Ext.namespace("action.manager.user");

action.manager.user.DoUserSearch = function() {
	return new Ext.Action({
		id:      'action.manager.user.dousersearch',
		iconCls: 'icon-search',
		handler: function() {
			// Get the text field.
			var txt = Ext.getCmp('ui.field.manager.user.search').getValue();

			// Get the grid.
			var grid = Ext.getCmp('ui.grid.manager.usergrid');

			// Check to see if the text is valid.
			if (txt != undefined && txt.length > 0) {
				// Get the regular expression.
				var r = new RegExp(txt, 'i');

				// Add the filter to the store.
				grid.getStore().filterBy(function(rec, recId) {
					var roles = [ ];
					for (var i = 0; rec.data.roles && i < rec.data.roles.length; i++)
						roles.push(rec.data.roles[i].name);

					// Check the searchable fields.
					return rec.data.fullName.match(r) ||
						   rec.data.login.match(r) ||
						   rec.data.email.match(r) ||
						   roles.join(" ").match(r);
				});
			} else
				// Clear the filter on the store.
				grid.getStore().clearFilter();
		}
	});
}

