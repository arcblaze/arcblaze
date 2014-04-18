
Ext.namespace("ui.tbar.finance");

ui.tbar.finance.TransactionToolbar = Ext.extend(Ext.Toolbar, {
	constructor: function(c) {
		var config = Ext.applyIf(c || {}, {
			items: [

				'->',

				new Ext.form.TextField({
					id: 'ui.field.finance.transaction.search',
					width: 100,
					listeners: {
						specialkey: function(tf, evt) {
							if (evt.ENTER == evt.getKey()) {
								var search = Ext.getCmp('action.finance.transaction.dotransactionsearch');
								search.handler();
							}
						}
					}
				}),
				new action.finance.transaction.DoTransactionSearch()
			]
		});

		ui.tbar.finance.TransactionToolbar.superclass.constructor.call(this, config);
	}
});

