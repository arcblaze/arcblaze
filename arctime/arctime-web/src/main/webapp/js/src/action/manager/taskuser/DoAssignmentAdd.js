
Ext.namespace("action.manager.taskuser");

action.manager.taskuser.DoAssignmentAdd = function(task) {
	return new Ext.Action({
		id:      'action.manager.taskuser.doassignmentadd',
		text:    'Add',
		iconCls: 'icon-assignment-add',
		handler: function() {
			// Get the panel containing the form data.
			var formPanel = Ext.getCmp(
				'ui.panel.manager.taskuser.assignmentaddpanel');

			// Make sure the form is valid.
			if (!formPanel.getForm().isValid()) {
				// Display an error message.
				Ext.Msg.alert('Form Incomplete', 'Please resolve form ' +
					'validation problems before continuing.');
				return;
			}

			// Show the progress bar while the task is being added.
			Ext.Msg.progress('Assigning User to Task', 'Please ' +
				'wait while the user is assigned to the task...');

			// Create a new ServerIO object.
			var io = new util.io.ServerIO();

			// Submit the form.
			io.doFormRequest(formPanel, {
				// Set the URL.
				url: '/rest/manager/assignment',
				method: 'POST',
				message: true,

				// The function to invoke after success.
				mysuccess: function(data) {
					// Get the grid.
					var grid = Ext.getCmp('ui.grid.manager.taskusergrid');

					// Reload the data store.
					grid.getStore().reload();
				}
			});
		}
	});
}

