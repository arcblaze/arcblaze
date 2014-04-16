
Ext.namespace("action.manager.user");

action.manager.user.ShowUserGrid = function() {
	return new Ext.Action({
		id:      'action.manager.user.showusergrid',
		text:    'Back to Users',
		iconCls: 'icon-user-go',
		handler: function() {
			// Get the panels.
			var userAddPanel = Ext.getCmp('ui.panel.manager.user.useraddpanel');
			var userUpdPanel = Ext.getCmp('ui.panel.manager.user.userupdatepanel');
			var assignmentGrid = Ext.getCmp('ui.grid.manager.usertaskgrid');

			// Hide the panels.
			if (userAddPanel) userAddPanel.destroy();
			if (userUpdPanel) userUpdPanel.destroy();
			if (assignmentGrid) assignmentGrid.destroy();

			// Show the grid.
			Ext.getCmp('ui.grid.manager.usergrid').show();
		}
	});
}

