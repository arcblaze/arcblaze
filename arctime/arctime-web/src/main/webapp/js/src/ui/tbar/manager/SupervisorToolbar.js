
Ext.namespace("ui.tbar.manager");

ui.tbar.manager.SupervisorToolbar = Ext.extend(Ext.Toolbar, {
	constructor: function(c) {
		if (!c || !c.user)
			throw "SupervisorToolbar requires a user.";

		var config = Ext.applyIf(c || {}, {
			items: [
				new action.manager.supervisor.ShowSupervisorAdd(c.user),
				new action.manager.supervisor.DoSupervisorDelete(c.user)
			]
		});

		ui.tbar.manager.SupervisorToolbar.superclass.constructor.call(this, config);
	}
});

