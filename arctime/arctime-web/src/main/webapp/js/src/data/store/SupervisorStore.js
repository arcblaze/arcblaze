
Ext.namespace("data.store");

data.store.SupervisorStore = Ext.extend(Ext.data.JsonStore, {
	constructor: function(c) {
		if (!c || !c.user)
			throw "SupervisorStore requires an user.";

		var config = Ext.applyIf(c || {}, {
			model:    'data.model.Supervisor',
			autoLoad: true,
			proxy: {
				type: 'ajax',
				url: '/rest/user/supervisors',
				reader: {
					type: 'json',
					root: 'supervisors'
				}
			},
			baseParams: {
				id: c.user.data.id
			}
		});

		data.store.SupervisorStore.superclass.constructor.call(this, config);
	}
});

