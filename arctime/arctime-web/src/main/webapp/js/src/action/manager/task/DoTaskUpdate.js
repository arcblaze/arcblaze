
Ext.namespace("action.manager.task");

action.manager.task.DoTaskUpdate = function() {
	return new Ext.Action({
		id:      'action.manager.task.dotaskupdate',
		text:    'Update',
		iconCls: 'icon-task-edit',
		handler: function() {
			// Get the form panel.
			var formPanel = Ext.getCmp('ui.panel.manager.task.taskupdatepanel');

			// Make sure the form is valid.
			if (!formPanel.getForm().isValid()) {
				// Display an error message.
				Ext.Msg.alert('Form Incomplete', 'Please resolve form ' +
					'validation problems before continuing.');
				return;
			}

			// Show the progress bar while the task is being saved.
			Ext.Msg.progress('Updating Task',
				'Please wait while the task is saved...');

			// Create the ServerIO object.
			var io = new util.io.ServerIO();

			// Submit the form.
			io.doFormRequest(formPanel, {
				// Set the URL.
				url: '/rest/manager/task',
				method: 'PUT',
				message: true,

				// The function to invoke after success.
				mysuccess: function(data) {
					// Get the grid.
					var grid = Ext.getCmp('ui.grid.manager.taskgrid');
					grid.selModel.clearSelections();
					grid.selModel.fireEvent('selectionchange', grid.selModel);

					// Reload the data store.
					grid.getStore().reload();
				}
			});
		}
	});
}

