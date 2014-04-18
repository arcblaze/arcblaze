
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

				grid.getStore().filterBy(function(rec, recId) {
					var day = Ext.Date.format(new Date(rec.data.timestamp), 'm/d/Y');
					var notes = rec.data.notes;
					return rec.data.description.match(r) ||
						   rec.data.transactionType.match(r) ||
						   rec.data.amount.match(r) ||
						   (notes && notes.match(r)) ||
						   day.match(r);
				});
			} else
				grid.getStore().clearFilter();
		}
	});
}
