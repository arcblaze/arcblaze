
Ext.namespace("action.admin.company");

action.admin.company.DoCompanySearch = function() {
	return new Ext.Action({
		id:      'action.admin.company.docompanysearch',
		iconCls: 'icon-search',
		handler: function() {
			var txt = Ext.getCmp('ui.field.company.search').getValue();
			var grid = Ext.getCmp('ui.grid.admin.companygrid');

			if (txt != undefined && txt.length > 0) {
				var r = new RegExp(txt, 'i');

				grid.getStore().filterBy(function(rec, recId) {
					return rec.data.name.match(r);
				});
			} else
				grid.getStore().clearFilter();
		}
	});
}

