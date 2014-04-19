
Ext.namespace("data.store.finance");

data.store.finance.TransactionStore = Ext.extend(Ext.data.JsonStore, {
	constructor: function(c) {
		var config = Ext.applyIf(c || {}, {
			model:    'data.model.Transaction',
			autoLoad: true,
			proxy: {
				type: 'ajax',
				url: '/rest/finance/transaction',
				pageParam: undefined, // we don't use this.
				reader: {
					type: 'json',
					root: 'transactions',
					totalProperty: 'total'
				}
			}
		});

		data.store.finance.TransactionStore.superclass.constructor.call(this, config);
	}
});

