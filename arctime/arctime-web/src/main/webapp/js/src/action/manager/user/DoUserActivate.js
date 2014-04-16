
Ext.namespace("action.manager.user");

action.manager.user.DoUserActivate = function() {
	return new Ext.Action({
		id:       'action.manager.user.douseractivate',
		text:     'Activate',
		iconCls:  'icon-user-activate',
		disabled: true,
		handler: function() {
			// Get the grid.
			var grid = Ext.getCmp('ui.grid.manager.usergrid');

			// Generate the array of ids to activate.
			var ids = grid.getSelectedIds();

			// Check to see if we have multiple users to activate.
			var u = ids.length > 1 ? 'users' : 'user';
			var U = ids.length > 1 ? 'Users' : 'User';

			// Show the progress bar while the user is being saved.
			Ext.Msg.progress('Activating ' + U,
				'Please wait while activating the ' + u + '...');

			// Create the ServerIO object.
			var io = new util.io.ServerIO();

			// Submit the form.
			io.doAjaxRequest({
				// Set the URL.
				url: '/rest/manager/user/activate',
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
					grid.selModel.clearSelections();
					grid.selModel.fireEvent('selectionchange', grid.selModel);

					// Reload the data store.
					grid.getStore().reload();
				}
			});
		}
	});
}

