
Ext.namespace("data.store.manager");

data.store.manager.SupervisorStore = Ext.extend(Ext.data.JsonStore, {
	constructor: function(c) {
		if (!c || !c.user)
			throw "SupervisorStore requires a user.";

		var config = Ext.applyIf(c || {}, {
			model:    'data.model.Supervisor',
			autoLoad: true,
			proxy: {
				type: 'ajax',
				url: '/rest/manager/supervisor/' + c.user.data.id,
				reader: {
					type: 'json',
					root: 'supervisors'
				}
			}
		});

		data.store.manager.SupervisorStore.superclass.constructor.call(this, config);
	}
});

