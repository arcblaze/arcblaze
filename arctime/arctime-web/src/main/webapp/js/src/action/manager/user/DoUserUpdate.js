
Ext.namespace("action.manager.user");

action.manager.user.DoUserUpdate = function() {
	return new Ext.Action({
		id:      'action.manager.user.douserupdate',
		text:    'Update',
		iconCls: 'icon-user-edit',
		handler: function() {
			// Get the form panel.
			var form = Ext.getCmp('ui.panel.manager.user.userupdatepanel').form;

			// Make sure the form is valid.
			if (!form.getForm().isValid()) {
				// Display an error message.
				Ext.Msg.alert('Form Incomplete', 'Please resolve form ' +
					'validation problems before continuing.');
				return;
			}

			// Get the form values.
			var vals = form.getForm().getValues();

			// Make sure the passwords are correct.
			if (vals.password && vals.password != vals.confirm) {
				// Display an error message.
				Ext.Msg.alert('Invalid Password', 'The confirm password ' +
					'does not match the password specified.');
				return;
			}

			// Show the progress bar while the user is being saved.
			Ext.Msg.progress('Updating User',
				'Please wait while the user is saved...');

			// Create the ServerIO object.
			var io = new util.io.ServerIO();

			// Submit the form.
			io.doFormRequest(form, {
				// Set the URL.
				url: '/rest/manager/user',
				method: 'PUT',
				message: true,

				// The function to invoke after success.
				mysuccess: function(data) {
					// Get the grid.
					var grid = Ext.getCmp('ui.grid.manager.usergrid');
					grid.selModel.clearSelections();
					grid.selModel.fireEvent('selectionchange', grid.selModel);

					// Reload the data store.
					grid.getStore().reload();
				}
			});
		}
	});
}

