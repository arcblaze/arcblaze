
Ext.namespace("action.manager.supervisor");

action.manager.supervisor.ShowSupervisorAdd = function(user) {
	return new Ext.Action({
		id:      'action.manager.supervisor.showsupervisoradd',
		text:    'Add',
		iconCls: 'icon-supervisor-add',
		handler: function() {
			// Create the form.
			var form = new Ext.form.FormPanel({
				border:    false,
				frame:     false,
				bodyStyle: 'padding: 10px;',
				items: [
					new Ext.form.ComboBox({
						fieldLabel:     'Supervisor',
						name:           'supervisorId',
						displayField:   'fullName',
						valueField:     'id',
						hiddenName:     'supervisorId',
						mode:           'local',
						forceSelection: true,
						triggerAction:  'all',
						selectOnFocus:  true,
						width:          300,
						labelWidth:     90,
						allowBlank:     false,
						store: new data.store.manager.UserStore({
							includeInactive: false
						})
					}), {
						xtype:      'radiogroup',
						fieldLabel: 'Primary',
						name:       'primary',
						width:      240,
						labelWidth: 90,
						items: [
							{
								boxLabel:   'Yes',
								name:       'primary',
								id:         'employee-primary-yes',
								inputValue: "true",
								checked:    false,
								style:      'border: 0px;'
							}, {
								boxLabel:   'No',
								name:       'primary',
								id:         'employee-primary-no',
								inputValue: "false",
								checked:    true,
								style:      'border: 0px;'
							}
						]
					}
				]
			});

			// Create the window.
			var win = new Ext.Window({
				title:  'Add a Supervisor',
				width:  340,
				height: 130,
				items:  form,
				buttons: [
					{
						text: 'Add Supervisor',
						iconCls: 'icon-supervisor-add',
						handler: function() {
							// Get the form values.
							var vals = form.getForm().getValues();

							// This is used to communicate with the server.
							var io = new util.io.ServerIO();

							// Submit the request.
							io.doAjaxRequest({
								// Set the URL.
								url: '/rest/manager/supervisor',
								method: 'POST',
								message: true,

								// Add the parameters to send to the server.
								params: Ext.apply(vals, {
									userId: user.data.id
								}),

								// The function to invoke after success.
								mysuccess: function(data) {
									// Close the window.
									win.close();

									// Get the grid.
									var grid = Ext.getCmp('ui.grid.manager.supervisorgrid');

									// Reload the data store.
									grid.getStore().reload();
								}
							});
						}
					}, {
						text: 'Cancel',
						handler: function() {
							// Close the window.
							win.close();
						}
					}
				]
			});

			// Show the window.
			win.show();
		}
	});
}

