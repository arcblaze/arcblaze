
Ext.namespace("action.manager.supervisor");

action.manager.supervisor.DoSupervisorAdd = function(employee) {
	return new Ext.Action({
		id:      'action.manager.supervisor.dosupervisoradd',
		text:    'Add',
		iconCls: 'icon-supervisor-add',
		handler: function() {
			// Get the panel containing the form data.
			var formPanel = Ext.getCmp('ui.panel.manager.supervisor.supervisoraddpanel');

			// Make sure the form is valid.
			if (!formPanel.getForm().isValid()) {
				// Display an error message.
				Ext.Msg.alert('Form Incomplete', 'Please resolve form ' +
					'validation problems before continuing.');
				return;
			}

			// Get the form values.
			var vals = formPanel.getForm().getValues();

			// Make sure the passwords are correct.
			if (vals.password != vals.confirm) {
				// Display an error message.
				Ext.Msg.alert('Invalid Password', 'The confirm password ' +
					'does not match the password specified.');
				return;
			}

			// Show the progress bar while the supervisor is being added.
			Ext.Msg.progress('Adding Supervisor',
				'Please wait while the supervisor is added...');

			// Create a new ServerIO object.
			var io = new util.io.ServerIO();

			// Submit the form.
			io.doFormRequest(formPanel, {
				// Set the URL.
				url: '/rest/manager/supervisor',
				method: 'POST',
				message: true,

				// The function to invoke after success.
				mysuccess: function(data) {
					// Get the grid.
					var grid = Ext.getCmp('ui.grid.manager.supervisorgrid');

					// Reload the data store.
					grid.getStore().reload();
				}
			});
		}
	});
}

