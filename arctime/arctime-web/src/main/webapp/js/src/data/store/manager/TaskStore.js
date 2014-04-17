
Ext.namespace("data.store.manager");

data.store.manager.TaskStore = Ext.extend(Ext.data.JsonStore, {
	constructor: function(c) {
		var config = Ext.applyIf(c || {}, {
			model:    'data.model.Task',
			autoLoad: true,
			proxy: {
				type: 'ajax',
				url: '/rest/manager/task',
				reader: {
					type: 'json',
					root: 'tasks'
				},
				extraParams: {
					includeAdministrative: c ? c.includeAdministrative : true,
					includeInactive: c ? c.includeInactive : true
				}
			},
			listeners: {
				load: function(store, records, options) {
					// Get the filter checkbox.
					var filterCB = Ext.getCmp('ui.field.manager.task.inactive');

					// If the filter field exists and is not checked, then
					// hide all the inactive tasks.
					if (filterCB)
						filterCB.getValue() ?
							store.clearInactiveFilter() :
							store.setInactiveFilter();
				}
			}
		});

		data.store.manager.TaskStore.superclass.constructor.call(this, config);
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

