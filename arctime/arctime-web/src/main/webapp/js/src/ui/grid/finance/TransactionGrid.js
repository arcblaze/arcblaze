
Ext.namespace("ui.grid.finance");

ui.grid.finance.TransactionGrid = Ext.extend(Ext.grid.GridPanel, {
	constructor: function(c) {
		var transaction = new data.model.Transaction();

		var grid = this;

		this.store = new data.store.finance.TransactionStore();
		this.toolbar = new ui.tbar.finance.TransactionToolbar();

		var config = Ext.applyIf(c || {}, {
			title:       'Invoice Transactions',
			id:          'ui.grid.finance.transactiongrid',
			store:       grid.store,
			multiSelect: true,
			stripeRows:  true,
			autoWidth:   true,
			autoHeight:  true,
			tbar:        grid.toolbar,
			columns:     transaction.getColumnModel(),
			loadMask:    true
		});

		ui.grid.finance.TransactionGrid.superclass.constructor.call(this, config);
	},

	getSelectedIds: function() {
		var ids = [ ];
		var records = this.selModel.selected.items;
		for (var i = 0; i < records.length; i++)
			ids.push(records[i].data.id);
		return ids;
	}
});

