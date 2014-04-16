
Ext.namespace("action.manager.task");

action.manager.task.DoTaskDeactivate = function() {
	return new Ext.Action({
		id:       'action.manager.task.dotaskdeactivate',
		text:     'Deactivate',
		iconCls:  'icon-task-edit',
		disabled: true,
		handler: function() {
			// Get the grid.
			var grid = Ext.getCmp('ui.grid.manager.taskgrid');

			// Generate the array of ids to delete.
			var ids = grid.getSelectedIds();

			// Check to see if we have multiple tasks to delete.
			var t = ids.length > 1 ? 'tasks' : 'task';
			var T = ids.length > 1 ? 'Tasks' : 'Task';

			// Show the progress bar while the task is being saved.
			Ext.Msg.progress('Deactivating ' + T,
				'Please wait while deactivating the ' + t + '...');

			// Create the ServerIO object.
			var io = new util.io.ServerIO();

			// Submit the form.
			io.doAjaxRequest({
				// Set the URL.
				url: '/rest/manager/task/deactivate',
				method: 'PUT',

				// Add the parameters to send to the server.
				headers: {
					ids: ids
				},

				// The function to invoke after success.
				mysuccess: function(data) {
					// Get the grid.
					var grid = Ext.getCmp('ui.grid.manager.taskgrid');

					// Reload the data store.
					grid.getStore().reload();
				}
			});
		}
	});
}

