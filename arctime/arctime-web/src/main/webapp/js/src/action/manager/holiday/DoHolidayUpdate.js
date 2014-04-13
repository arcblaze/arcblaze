
Ext.namespace("action.manager.holiday");

action.manager.holiday.DoHolidayUpdate = function() {
	return new Ext.Action({
		id:      'action.manager.holiday.doholidayupdate',
		text:    'Update',
		iconCls: 'icon-holiday-edit',
		handler: function() {
			// Get the panel containing the form data.
			var formPanel = Ext.getCmp('ui.panel.manager.holiday.holidayupdatepanel');

			// Make sure the form is valid.
			if (!formPanel.getForm().isValid()) {
				// Display an error message.
				Ext.Msg.alert('Form Incomplete', 'Please resolve form ' +
					'validation problems before continuing.');
				return;
			}

			// Show the progress bar while the holiday is being saved.
			Ext.Msg.progress('Saving Holiday',
				'Please wait while the holiday is saved...');

			// Create a new ServerIO object.
			var io = new util.io.ServerIO();

			// Submit the form.
			io.doFormRequest(formPanel, {
				// Set the URL.
				url: '/rest/manager/holiday',
				method: 'PUT',
				message: true,
				
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

