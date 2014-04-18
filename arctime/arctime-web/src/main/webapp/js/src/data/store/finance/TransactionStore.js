
Ext.namespace("data.store.finance");

data.store.finance.TransactionStore = Ext.extend(Ext.data.JsonStore, {
	constructor: function(c) {
		var config = Ext.applyIf(c || {}, {
			model:    'data.model.Transaction',
			autoLoad: true,
			proxy: {
				type: 'ajax',
				url: '/rest/finance/transaction',
				reader: {
					type: 'json',
					root: 'transactions'
				}
			}
		});

		data.store.finance.TransactionStore.superclass.constructor.call(this, config);
	}
});

