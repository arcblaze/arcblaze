
Ext.namespace("ui.grid.user");

ui.grid.user.SupervisorGrid = Ext.extend(Ext.grid.GridPanel, {
	constructor: function(c) {
		if (!c || !c.user)
			throw "SupervisorGrid requires a user.";

		var grid = this;

		var supervisor = new data.model.Supervisor();

		var config = Ext.applyIf(c || {}, {
			title:       'Supervisors',
			id:          'ui.grid.user.supervisorgrid',
			stripeRows:  true,
			width:       520,
			height:      300,
			loadMask:    true,
			multiSelect: true,
			columns:     supervisor.getColumnModel(),
			store: new data.store.user.SupervisorStore({
				user: c.user
			})
		});

		ui.grid.user.SupervisorGrid.superclass.constructor.call(this, config);
	}
});

