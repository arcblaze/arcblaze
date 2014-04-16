
Ext.namespace("action.manager.task");

action.manager.task.DoTaskAdd = function() {
	return new Ext.Action({
		id:      'action.manager.task.dotaskadd',
		text:    'Add',
		iconCls: 'icon-task-add',
		handler: function() {
			// Get the panel containing the form data.
			var formPanel = Ext.getCmp('ui.panel.manager.task.taskaddpanel');

			// Make sure the form is valid.
			if (!formPanel.getForm().isValid()) {
				// Display an error message.
				Ext.Msg.alert('Form Incomplete', 'Please resolve form ' +
					'validation problems before continuing.');
				return;
			}

			// Show the progress bar while the task is being added.
			Ext.Msg.progress('Adding Task',
				'Please wait while the task is added...');

			// Create a new ServerIO object.
			var io = new util.io.ServerIO();

			// Submit the form.
			io.doFormRequest(formPanel, {
				// Set the URL.
				url: '/rest/manager/task',
				method: 'POST',
				message: true,

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

