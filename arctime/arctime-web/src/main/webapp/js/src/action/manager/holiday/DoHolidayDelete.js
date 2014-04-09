
Ext.namespace("action.manager.holiday");

action.manager.holiday.DoHolidayDelete = function() {
	return new Ext.Action({
		id:       'action.manager.holiday.doholidaydelete',
		text:     'Delete',
		iconCls:  'icon-holiday-delete',
		disabled: true,
		handler: function() {
			// Get the grid.
			var grid = Ext.getCmp('ui.grid.manager.holidaygrid');

			// Generate the array of ids to delete.
			var ids = grid.getSelectedIds();

			// Check to see if we have multiple holidays to delete.
			var h = ids.length > 1 ? 'holidays' : 'holiday';
			var H = ids.length > 1 ? 'Holidays' : 'Holiday';

			// Confirm the deletion of the holidays.
			Ext.Msg.confirm('Are you sure?',
				'Are you sure you want to delete the specified ' + h + '?',

				// Handle the confirmation response.
				function(btn) {
					// Make sure the user clicked the 'yes' button.
					if (btn != 'yes')
						return;

					// Let the user know what we are doing.
					Ext.Msg.progress('Deleting ' + H,
						'Please wait while removing the ' + h + '...');

					// Create the ServerIO object.
					var io = new util.io.ServerIO();

					// Send the Ajax request.
					io.doAjaxRequest({
						// Add the URL.
						url: '/rest/manager/holiday/delete',

						// Add the parameters to send to the server.
						params: {
							ids: ids
						},

						// Add the mysuccess function.
						mysuccess: function(data) {
							// Get the grid.
							var grid = Ext.getCmp('ui.grid.manager.holidaygrid');

							// Reload the data store.
							grid.getStore().reload();
						}
					});
				}
			);
		}
	});
}

