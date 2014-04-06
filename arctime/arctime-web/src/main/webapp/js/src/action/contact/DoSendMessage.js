
Ext.namespace("action.contact");

action.contact.DoSendMessage = function() {
	return new Ext.Action({
		id:      'action.contact.dosendmessage',
		text:    'Send',
		iconCls: 'icon-send-message',
		handler: function() {
			// Get the form panel.
			var form = Ext.getCmp('ui.panel.contact.contactuspanel');

			// Make sure the form is valid.
			if (!form.getForm().isValid()) {
				// Display an error message.
				Ext.Msg.alert('Form Incomplete', 'Please resolve form ' +
					'validation problems before continuing.');
				return;
			}

			// Show the progress bar while the user is being saved.
			Ext.Msg.progress('Sending Message',
				'Please wait while your message is sent...');

			// Create the ServerIO object.
			var io = new util.io.ServerIO();

			// Submit the form.
			io.doFormRequest(form, {
				// The URL to which the updated information will be posted.
				url: '/rest/contact/send',

				// Display whatever message comes back from the server.
				message: true
			});
		}
	});
}

