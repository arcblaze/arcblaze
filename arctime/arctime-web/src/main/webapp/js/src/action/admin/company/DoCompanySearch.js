
Ext.namespace("action.admin.company");

action.admin.company.DoCompanySearch = function() {
	return new Ext.Action({
		id:      'action.admin.company.docompanysearch',
		iconCls: 'icon-search',
		handler: function() {
			var txt = Ext.getCmp('ui.field.admin.company.search').getValue();
			var grid = Ext.getCmp('ui.grid.admin.companygrid');

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

