
Ext.namespace("data.store.admin");

data.store.admin.SystemStatsStore = Ext.extend(Ext.data.JsonStore, {
	constructor: function(c) {
		var config = Ext.applyIf(c || {}, {
			model:    'data.model.Stat',
			autoLoad: true,
			proxy: {
				type: 'ajax',
				url: '/rest/admin/stats/system',
				reader: {
					type: 'json',
					root: 'stats'
				}
			}
		});

		data.store.admin.SystemStatsStore.superclass.constructor.call(this, config);
	}
});

