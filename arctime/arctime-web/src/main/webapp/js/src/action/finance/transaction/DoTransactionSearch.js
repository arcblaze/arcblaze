
Ext.namespace("action.finance.transaction");

action.finance.transaction.DoTransactionSearch = function() {
	return new Ext.Action({
		id:      'action.finance.transaction.dotransactionsearch',
		iconCls: 'icon-search',
		handler: function() {
			var txt = Ext.getCmp('ui.field.finance.transaction.search').getValue();
			var grid = Ext.getCmp('ui.grid.finance.transactiongrid');

			if (txt != undefined && txt.length > 0) {
				var r = new RegExp(txt, 'i');

				var store = grid.getStore();
				store.reload({
					params: {
						limit: store.lastOptions.limit,
						start: store.lastOptions.start,
						filter: txt
					}
				});
			} else {
				var store = grid.getStore();
				store.reload({
					params: {
						limit: store.lastOptions.limit,
						start: store.lastOptions.start,
						filter: undefined
					}
				});
			}
		}
	});
}
