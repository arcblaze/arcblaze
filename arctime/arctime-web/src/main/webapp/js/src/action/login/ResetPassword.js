
Ext.namespace("action.login");

action.login.ResetPassword = function() {
	return new Ext.Action({
		id:      'action.login.resetpassword',
		text:    'Reset Password',
		iconCls: 'icon-reset-password',
		handler: function() {
			// Get the panel containing the form data.
			var formPanel = Ext.getCmp('ui.panel.login.loginpanel');

			// Make sure the form is valid.
			if (!formPanel.getForm().isValid()) {
				// Display an error message.
				Ext.Msg.alert('Form Incomplete', 'Please enter a valid ' +
					'login or email. This is necessary to identify ' +
					'which account to reset.');
				return;
			}

			// Show the progress bar while the login happens.
			Ext.Msg.progress('Processing Request',
				'Please wait while your request is processed...');

			// Create a new ServerIO object.
			var io = new util.io.ServerIO();

			// Submit the form.
			io.doAjaxRequest({
				// Set the URL.
				url: '/rest/login/reset',

				// Specify the form parameters.
				params: formPanel.getForm().getValues()
			});
		}
	});
}

