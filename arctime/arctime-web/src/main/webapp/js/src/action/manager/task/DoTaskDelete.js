
Ext.namespace("action.manager.task");

action.manager.task.DoTaskDelete = function() {
	return new Ext.Action({
		id:       'action.manager.task.dotaskdelete',
		text:     'Delete',
		iconCls:  'icon-task-delete',
		disabled: true,
		handler: function() {
			// Get the grid.
			var grid = Ext.getCmp('ui.grid.manager.taskgrid');

			// Generate the array of ids to delete.
			var ids = grid.getSelectedIds();

			// Check to see if we have multiple tasks to delete.
			var t = ids.length > 1 ? 'tasks' : 'task';
			var T = ids.length > 1 ? 'Tasks' : 'Task';

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
						url: '/rest/manager/task',
						method: 'DELETE',
						message: true,

						// Add the parameters to send to the server.
						headers: {
							ids: ids
						},

						// Add the mysuccess function.
						mysuccess: function(data) {
							// Get the grid.
							var grid = Ext.getCmp('ui.grid.manager.taskgrid');

							// Reload the data store.
							grid.getStore().reload();
						}
					});
				}
			);
		}
	});
}

