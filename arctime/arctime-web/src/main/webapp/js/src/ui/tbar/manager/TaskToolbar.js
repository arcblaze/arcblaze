
Ext.namespace("ui.tbar.manager");

ui.tbar.manager.TaskToolbar = Ext.extend(Ext.Toolbar, {
	constructor: function(c) {
		var config = Ext.applyIf(c || {}, {
			items: [
				new action.manager.task.ShowTaskAdd(),
				new action.manager.task.ShowTaskUpdate(),
				new action.manager.task.DoTaskActivate(),
				new action.manager.task.DoTaskDeactivate(),
				new action.manager.task.DoTaskDelete(),

				'-',

				new action.manager.task.ShowTaskUsers(),

				'->',

				new Ext.form.Label({
					text: 'Include inactive tasks',
					style: 'padding-right:10px;'
				}),
				new Ext.form.Checkbox({
					id: 'ui.field.manager.task.inactive',
					checked: false,
					listeners: {
						change: function(cb, checked) {
							// Get the grid.
							var grid = Ext.getCmp('ui.grid.manager.taskgrid');

							// Update the grid filters.
							if (grid) {
								var store = grid.getStore();
								checked ? store.clearInactiveFilter() :
										  store.setInactiveFilter();
							}
						}
					}
				}),

				'-',

				new Ext.form.TextField({
					id: 'ui.field.manager.task.search',
					width: 100,
					listeners: {
						specialkey: function(tf, evt) {
							// Listen for the Enter key.
							if (evt.ENTER == evt.getKey()) {
								// Get the search action.manager.
								var search = Ext.getCmp(
									'action.manager.task.dotasksearch');

								// Invoke the handler.
								search.handler();
							}
						}
					}
				}),
				new action.manager.task.DoTaskSearch()
			]
		});

		ui.tbar.manager.TaskToolbar.superclass.constructor.call(this, config);
	}
});

