
Ext.namespace("util.io");

util.io.ServerIO = Ext.extend(Ext.util.Observable, {
	// Send an Ajax request to the server.
	doAjaxRequest: function(c) {
		// Build the base config.
		var config = Ext.applyIf(c || { }, {
			// Set the default timeout to 5 minutes.
			timeout: 300000,

			// Add a default success function.
			success: function(response) {
				try {
					// Check the response status.
					if (response.status == 200) {
						// Parse the JSON response.
						var data = eval('(' + response.responseText + ')');

						// Alert of the success.
						if (config.message && data.msg) {
							var title = data.title ? data.title : 'Success';
							Ext.Msg.alert(title, data.msg);
						}

						// Call the configured function if it is specified.
						if (c.mysuccess)
							c.mysuccess(data);
					} else
						// Alert if the status was invalid.
						Ext.Msg.alert('Response Status Error', response.status);
				} catch (error) {
					// Alert when an error occurs.
					Ext.Msg.alert('Response Error', 'Error: ' + error);
				}
			},

			// Add a default failure function.
			failure: function(response) {
				// Display the error message from the server.
				var msg = 'Error ' + response.status + ', ' + response.statusText;
				if (response.responseText)
					msg = response.responseText;
				Ext.Msg.alert('Failed', msg);
			},

			// By default, we display a success message.
			message: true
		});

		// Perform the Ext.Ajax request.
		Ext.Ajax.request(config);
	},

	// Send an Ajax request to the server using a form panel.
	doFormRequest: function(formPanel, c) {
		// Build the base config.
		var config = Ext.applyIf(c || { }, {
			// Set the default timeout to 5 minutes.
			timeout: 300,

			// Add a default success function.
			success: function(form, action) {
				// Alert of the success.
				if (config.message && action.result.msg) {
					var title = action.result.title ? action.result.title : 'Success';
					Ext.Msg.alert(title, action.result.msg);
				} else
					Ext.Msg.close();

				// Call the configuration function if it is specified.
				if (c.mysuccess)
					c.mysuccess(action.result);
			},

			// Add a default failure function.
			failure: function(form, action) {
				// Determine what to do based on failure type.
				switch (action.failureType) {
					// Form validation problems.
					case Ext.form.Action.CLIENT_INVALID:
						Ext.Msg.alert('Failure',
							'Correct form validation problems.');
						break;

					// Cannot communicate with the server.
					case Ext.form.Action.CONNECT_FAILURE:
						Ext.Msg.alert('Failure',
							'Server communication failed.');
						break;

					// Server said it failed.
					default:
						Ext.Msg.alert('Failure', action.result.msg);
						break;
				}

				// Call the configuration function if it is specified.
				if (c.myfailure)
					c.myfailure(action.result);
			}
		});

		// Push the form values to the server.
		formPanel.getForm().submit(config);
	}
});

