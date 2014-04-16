
Ext.namespace("action.manager.user");

action.manager.user.DoUserAdd = function() {
	return new Ext.Action({
		id:      'action.manager.user.douseradd',
		text:    'Add',
		iconCls: 'icon-user-add',
		handler: function() {
			// Get the panel containing the form data.
			var formPanel = Ext.getCmp('ui.panel.manager.user.useraddpanel');

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

			// Show the progress bar while the user is being added.
			Ext.Msg.progress('Adding User',
				'Please wait while the user is added...');

			// Create a new ServerIO object.
			var io = new util.io.ServerIO();

			// Submit the form.
			io.doFormRequest(formPanel, {
				// Set the URL.
				url: '/rest/manager/user',
				method: 'POST',
				message: true,

				// The function to invoke after success.
				mysuccess: function(data) {
					// Get the grid.
					var grid = Ext.getCmp('ui.grid.manager.usergrid');

					// Reload the data store.
					grid.getStore().reload();
				}
			});
		}
	});
}

