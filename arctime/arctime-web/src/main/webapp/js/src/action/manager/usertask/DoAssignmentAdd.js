
Ext.namespace("action.manager.usertask");

action.manager.usertask.DoAssignmentAdd = function(user) {
	return new Ext.Action({
		id:      'action.manager.usertask.doassignmentadd',
		text:    'Add',
		iconCls: 'icon-assignment-add',
		handler: function() {
			// Get the panel containing the form data.
			var formPanel = Ext.getCmp(
				'ui.panel.manager.usertask.assignmentaddpanel');

			// Make sure the form is valid.
			if (!formPanel.getForm().isValid()) {
				// Display an error message.
				Ext.Msg.alert('Form Incomplete', 'Please resolve form ' +
					'validation problems before continuing.');
				return;
			}

			// Show the progress bar while the task is being added.
			Ext.Msg.progress('Assigning Task to User', 'Please ' +
				'wait while the task is assigned to the user...');

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
					var grid = Ext.getCmp('ui.grid.manager.usertaskgrid');

					// Reload the data store.
					grid.getStore().reload();
				}
			});
		}
	});
}

