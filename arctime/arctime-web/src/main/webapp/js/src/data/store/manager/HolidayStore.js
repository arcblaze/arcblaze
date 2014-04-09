
Ext.namespace("data.store.manager");

data.store.manager.HolidayStore = Ext.extend(Ext.data.JsonStore, {
	constructor: function(c) {
		var config = Ext.applyIf(c || {}, {
			model:    'data.model.Holiday',
			autoLoad: true,
			proxy: {
				type: 'ajax',
				url: '/rest/manager/holiday',
				reader: {
					type: 'json',
					root: 'holidays'
				}
			}
		});

		data.store.manager.HolidayStore.superclass.constructor.call(this, config);
	}
});

