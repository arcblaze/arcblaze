
Ext.namespace("data.store.manager");

data.store.manager.UserStore = Ext.extend(Ext.data.JsonStore, {
	constructor: function(c) {
		var config = Ext.applyIf(c || {}, {
			model:    'data.model.User',
			autoLoad: true,
			proxy: {
				type: 'ajax',
				url: '/rest/manager/user',
				reader: {
					type: 'json',
					root: 'users'
				},
				extraParams: {
					includeInactive: c ? c.includeInactive : true,
					filterMe: c ? c.filterMe : false
				}
			},
			listeners: {
				load: function(store, records, options) {
					// If specific configuration options were passed in,
					// don't do any filtering.
					if (c) return;

					// Get the filter checkbox.
					var filterCB = Ext.getCmp('ui.field.manager.user.inactive');

					// If the filter field exists and is not checked, then
					// hide all the inactive users.
					if (filterCB)
						filterCB.getValue() ?
							store.clearInactiveFilter() :
							store.setInactiveFilter();
				}
			}
		});

		data.store.manager.UserStore.superclass.constructor.call(this, config);
	},

	setInactiveFilter: function() {
		this.filterBy(function(record) {
			// Only return true if the record is active.
			return record.data.active;
		});
	},

	clearInactiveFilter: function() {
		this.clearFilter(false);
	}
});

