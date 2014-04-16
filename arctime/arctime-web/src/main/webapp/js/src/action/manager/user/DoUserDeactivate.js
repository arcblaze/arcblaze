
Ext.namespace("action.manager.user");

action.manager.user.DoUserDeactivate = function() {
	return new Ext.Action({
		id:       'action.manager.user.douserdeactivate',
		text:     'Deactivate',
		iconCls:  'icon-user-deactivate',
		disabled: true,
		handler: function() {
			// Get the grid.
			var grid = Ext.getCmp('ui.grid.manager.usergrid');

			// Generate the array of ids to deactivate.
			var ids = grid.getSelectedIds();

			// Check to see if we have multiple users to deactivate.
			var u = ids.length > 1 ? 'users' : 'user';
			var U = ids.length > 1 ? 'Users' : 'User';

			// Show the progress bar while the user is being saved.
			Ext.Msg.progress('Deactivating ' + U,
				'Please wait while deactivating the ' + u + '...');

			// Create the ServerIO object.
			var io = new util.io.ServerIO();

			// Submit the form.
			io.doAjaxRequest({
				// Set the URL.
				url: '/rest/manager/user/deactivate',
				method: 'PUT',
				message: true,

				// Add the parameters to send to the server.
				headers: {
					ids: ids
				},

				// The function to invoke after success.
				mysuccess: function(data) {
					// Get the grid.
					var grid = Ext.getCmp('ui.grid.manager.usergrid');

					// Reload the data store.
					grid.getStore().reload();
				}
			});
		}
	});
}

