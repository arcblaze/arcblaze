
Ext.namespace("action.manager.user");

action.manager.user.ShowUserUpdate = function() {
	return new Ext.Action({
		id:       'action.manager.user.showuserupdate',
		text:     'Update',
		iconCls:  'icon-user-edit',
		disabled: true,
		handler: function() {
			// Get the grid.
			var userGrid = Ext.getCmp('ui.grid.manager.usergrid');

			// Get the selected user.
			var user = userGrid.selModel.selected.items[0];

			// Hide the grid.
			userGrid.hide();

			// Create the panel.
			var userUpdPanel = new ui.panel.manager.user.UserUpdatePanel({
				renderTo: 'user-update-panel',
				user: user
			});

			// Set the focus and values.
			userUpdPanel.setValues(user);
		}
	});
}

