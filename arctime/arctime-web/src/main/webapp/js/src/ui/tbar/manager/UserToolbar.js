
Ext.namespace("ui.tbar.manager");

ui.tbar.manager.UserToolbar = Ext.extend(Ext.Toolbar, {
	constructor: function(c) {
		var config = Ext.applyIf(c || {}, {
			items: [
				new action.manager.user.ShowUserAdd(),
				new action.manager.user.ShowUserUpdate(),
				new action.manager.user.DoUserActivate(),
				new action.manager.user.DoUserDeactivate(),
				new action.manager.user.DoUserDelete(),

				'-',

				new action.manager.user.ShowUserTasks(),

				'->',

				new Ext.form.Label({
					text: 'Include inactive users',
					style: 'padding-right:10px;'
				}),
				new Ext.form.Checkbox({
					id: 'ui.field.manager.user.inactive',
					checked: false,
					listeners: {
						change: function(cb, checked) {
							// Get the grid.
							var grid = Ext.getCmp('ui.grid.manager.usergrid');

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
					id: 'ui.field.user.search',
					width: 100,
					listeners: {
						specialkey: function(tf, evt) {
							// Listen for the Enter key.
							if (evt.ENTER == evt.getKey()) {
								// Get the search action.manager.
								var search = Ext.getCmp(
									'action.manager.user.dousersearch');

								// Invoke the handler.
								search.handler();
							}
						}
					}
				}),
				new action.manager.user.DoUserSearch()
			]
		});

		ui.tbar.manager.UserToolbar.superclass.constructor.call(this, config);
	}
});

