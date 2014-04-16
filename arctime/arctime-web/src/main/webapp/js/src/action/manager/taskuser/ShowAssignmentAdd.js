
Ext.namespace("action.manager.taskuser");

action.manager.taskuser.ShowAssignmentAdd = function(task) {
	return new Ext.Action({
		id:      'action.manager.taskuser.showassignmentadd',
		text:    'Add',
		iconCls: 'icon-assignment-add',
		handler: function() {
			var taskUserGrid = Ext.getCmp('ui.grid.manager.taskusergrid');

			var assignmentAddPanel = new ui.panel.taskuser.AssignmentAddPanel({
				task: task,
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

