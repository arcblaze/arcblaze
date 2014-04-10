
Ext.namespace("action.manager.holiday");

action.manager.holiday.DoHolidayAdd = function() {
	return new Ext.Action({
		id:      'action.manager.holiday.doholidayadd',
		text:    'Add',
		iconCls: 'icon-holiday-add',
		handler: function() {
			// Get the panel containing the form data.
			var formPanel = Ext.getCmp('ui.panel.manager.holiday.holidayaddpanel');

			// Make sure the form is valid.
			if (!formPanel.getForm().isValid()) {
				// Display an error message.
				Ext.Msg.alert('Form Incomplete', 'Please resolve form ' +
					'validation problems before continuing.');
				return;
			}

			// Show the progress bar while the holiday is being added.
			Ext.Msg.progress('Adding Holiday',
				'Please wait while the holiday is added...');

			// Create a new ServerIO object.
			var io = new util.io.ServerIO();

			// Submit the form.
			io.doFormRequest(formPanel, {
				// Set the URL.
				url: '/rest/manager/holiday',
				method: 'POST',
				message: true,
				
				params: formPanel.getValues(),

				// The function to invoke after success.
				mysuccess: function(data) {
					// Get the grid.
					var grid = Ext.getCmp('ui.grid.manager.holidaygrid');

					// Reload the data store.
					grid.getStore().reload();
				}
			});
		}
	});
}

