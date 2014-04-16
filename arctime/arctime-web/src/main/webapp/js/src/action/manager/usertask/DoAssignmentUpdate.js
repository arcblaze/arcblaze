
Ext.namespace("action.manager.usertask");

action.manager.usertask.DoAssignmentUpdate = function(user) {
	return new Ext.Action({
		id:      'action.manager.usertask.doassignmentupdate',
		text:    'Update',
		iconCls: 'icon-assignment-edit',
		handler: function() {
			// Get the form panel.
			var formPanel = Ext.getCmp(
				'ui.panel.manager.usertask.assignmentupdatepanel');

			// Make sure the form is valid.
			if (!formPanel.getForm().isValid()) {
				// Display an error message.
				Ext.Msg.alert('Form Incomplete', 'Please resolve form ' +
					'validation problems before continuing.');
				return;
			}

			// Show the progress bar while the task assignment is being
			// saved.
			Ext.Msg.progress('Updating Task Assignment',
				'Please wait while the task assignment is saved...');

			// Create the ServerIO object.
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
					grid.selModel.clearSelections();
					grid.selModel.fireEvent('selectionchange', grid.selModel);

					// Reload the data store.
					grid.getStore().reload();
				}
			});
		}
	});
}

