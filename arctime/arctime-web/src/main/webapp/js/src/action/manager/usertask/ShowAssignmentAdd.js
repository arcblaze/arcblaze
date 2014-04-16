
Ext.namespace("action.manager.usertask");

action.manager.usertask.ShowAssignmentAdd = function(user) {
	return new Ext.Action({
		id:      'action.manager.usertask.showassignmentadd',
		text:    'Add',
		iconCls: 'icon-assignment-add',
		handler: function() {
			var taskUserGrid = Ext.getCmp('ui.grid.manager.usertaskgrid');

            var assignmentAddPanel = new ui.panel.manager.usertask.AssignmentAddPanel({
                user: user,
                renderTo: 'assignment-add-panel'
            });

			// Hide the grid and show the panel.
			taskUserGrid.hide();
			assignmentAddPanel.show();

			// Set the focus.
			assignmentAddPanel.setInitialFocus();
		}
	});
}

