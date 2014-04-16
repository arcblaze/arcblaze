
Ext.namespace("action.manager.user");

action.manager.user.DoUserDelete = function() {
	return new Ext.Action({
		id:       'action.manager.user.douserdelete',
		text:     'Delete',
		iconCls:  'icon-user-delete',
		disabled: true,
		handler: function() {
			// Get the grid.
			var grid = Ext.getCmp('ui.grid.manager.usergrid');

			// Generate the array of ids to delete.
			var ids = grid.getSelectedIds();

			// Check to see if we have multiple users to delete.
			var u = ids.length > 1 ? 'users' : 'user';
			var U = ids.length > 1 ? 'Users' : 'User';

			// Confirm the deletion of the users.
			Ext.Msg.confirm('Are you sure?',
				'Are you sure you want to delete the specified ' + u + '?',

				// Handle the confirmation response.
				function(btn) {
					// Make sure the user clicked the 'yes' button.
					if (btn != 'yes')
						return;

					// Let the user know what we are doing.
					Ext.Msg.progress('Deleting ' + U,
						'Please wait while removing the ' + u + '...');

					// Create the ServerIO object.
					var io = new util.io.ServerIO();

					// Send the Ajax request.
					io.doAjaxRequest({
						// Add the URL.
						url: '/rest/manager/user',
						method: 'DELETE',
						message: true,

						// Add the parameters to send to the server.
						headers: {
							ids: ids
						},

						// Add the mysuccess function.
						mysuccess: function(data) {
							// Get the grid.
							var grid = Ext.getCmp('ui.grid.manager.usergrid');

							// Reload the data store.
							grid.getStore().reload();
						}
					});
				}
			);
		}
	});
}

