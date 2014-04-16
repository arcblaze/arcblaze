
Ext.namespace("action.manager.supervisor");

action.manager.supervisor.DoSupervisorDelete = function(user) {
	return new Ext.Action({
		id:       'action.manager.supervisor.dosupervisordelete',
		text:     'Delete',
		iconCls:  'icon-supervisor-delete',
		disabled: true,
		handler: function() {
			// Get the grid.
			var grid = Ext.getCmp('ui.grid.manager.supervisorgrid');

			// Generate the array of ids to delete.
			var ids = grid.getSelectedIds();

			// Check to see if we have multiple supervisors to delete.
			var s = ids.length > 1 ? 'supervisors' : 'supervisor';
			var S = ids.length > 1 ? 'Supervisors' : 'Supervisor';

			// Confirm the deletion of the supervisors.
			Ext.Msg.confirm('Are you sure?',
				'Are you sure you want to delete the specified ' + s + '?',

				// Handle the confirmation response.
				function(btn) {
					// Make sure the user clicked the 'yes' button.
					if (btn != 'yes')
						return;

					// Let the user know what we are doing.
					Ext.Msg.progress('Deleting ' + S,
						'Please wait while removing the ' + s + '...');

					// Create the ServerIO object.
					var io = new util.io.ServerIO();

					// Send the Ajax request.
					io.doAjaxRequest({
						// Add the URL.
						url: '/rest/manager/supervisor',
						method: 'DELETE',
						message: true,

						// Add the parameters to send to the server.
						headers: {
							userId: user.data.id,
							supervisorIds: ids
						},

						// Add the mysuccess function.
						mysuccess: function(data) {
							// Get the grid.
							var grid = Ext.getCmp('ui.grid.manager.supervisorgrid');

							// Reload the data store.
							grid.getStore().reload();
						}
					});
				}
			);
		}
	});
}

