
Ext.namespace("action.manager.taskuser");

action.manager.taskuser.DoAssignmentDelete = function(task) {
	return new Ext.Action({
		id:       'action.manager.taskuser.doassignmentdelete',
		text:     'Delete',
		iconCls:  'icon-assignment-delete',
		disabled: true,
		handler: function() {
			// Get the grid.
			var grid = Ext.getCmp('ui.grid.manager.taskusergrid');

			// Generate the array of user ids to delete.
			var ids = grid.getSelectedIds();

			// Check to see if we have multiple task assignments to delete.
			var t = ids.length > 1 ?
				'task assignments' : 'task assignment';
			var T = ids.length > 1 ?
				'Task Assignments' : 'Task Assignment';

			// Confirm the deletion of the tasks.
			Ext.Msg.confirm('Are you sure?',
				'Are you sure you want to delete the specified ' + t + '?',

				// Handle the confirmation response.
				function(btn) {
					// Make sure the user clicked the 'yes' button.
					if (btn != 'yes')
						return;

					// Let the user know what we are doing.
					Ext.Msg.progress('Deleting ' + T,
						'Please wait while removing the ' + t + '...');

					// Create the ServerIO object.
					var io = new util.io.ServerIO();

					// Send the Ajax request.
					io.doAjaxRequest({
						// Add the URL.
						url: '/rest/manager/assignment',
						method: 'DELETE',
						message: true,

						// Add the parameters to send to the server.
						headers: {
							ids: ids
						},

						// Add the mysuccess function.
						mysuccess: function(data) {
							// Get the grid.
							var grid = Ext.getCmp(
								'ui.grid.manager.taskusergrid');

							// Reload the data store.
							grid.getStore().reload();
						}
					});
				}
			);
		}
	});
}

