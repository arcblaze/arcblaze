
Ext.namespace("data.store.admin");

data.store.admin.CompanyStore = Ext.extend(Ext.data.JsonStore, {
	constructor: function(c) {
		var config = Ext.applyIf(c || {}, {
			model:    'data.model.Company',
			autoLoad: true,
			proxy: {
				type: 'ajax',
				url: '/rest/admin/company',
				reader: {
					type: 'json',
					root: 'companies'
				}
			},
			listeners: {
				load: function(store, records, options) {
					if (c) return;

					var filterCB = Ext.getCmp('ui.field.company.inactive');

					if (filterCB)
						filterCB.getValue() ?
							store.clearInactiveFilter() :
							store.setInactiveFilter();
				}
			}
		});

		data.store.admin.CompanyStore.superclass.constructor.call(this, config);
	},

	setInactiveFilter: function() {
		this.filterBy(function(record) {
			return record.data.active == "1";
		});
	},

	clearInactiveFilter: function() {
		this.clearFilter(false);
	}
});

