
Ext.namespace("action.user");

action.user.DoProfileUpdate = function() {
	return new Ext.Action({
		id:      'action.user.doprofileupdate',
		text:    'Update',
		iconCls: 'icon-user-edit',
		handler: function() {
			// Get the form panel.
			var form = Ext.getCmp('ui.panel.user.profileupdatepanel');

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
			Ext.Msg.progress('Updating Profile',
				'Please wait while your profile is saved...');

			// Create the ServerIO object.
			var io = new util.io.ServerIO();

			// Submit the form.
			io.doFormRequest(form, {
				// The URL to which the updated information will be posted.
				url: '/rest/user/profile',

				// Display whatever message comes back from the server.
				message: true
			});
		}
	});
}

