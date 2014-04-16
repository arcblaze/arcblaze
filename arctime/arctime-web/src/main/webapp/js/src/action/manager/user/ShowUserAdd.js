
Ext.namespace("action.manager.user");

action.manager.user.ShowUserAdd = function() {
	return new Ext.Action({
		id:      'action.manager.user.showuseradd',
		text:    'Add',
		iconCls: 'icon-user-add',
		handler: function() {
			var userGrid = Ext.getCmp('ui.grid.manager.usergrid');

			var userAddPanel = new ui.panel.manager.user.UserAddPanel({
				renderTo: 'user-add-panel'
			});

			// Hide the grid and show the panel.
			userGrid.hide();
			userAddPanel.show();

			// Set the focus.
			userAddPanel.setInitialFocus();
		}
	});
}

