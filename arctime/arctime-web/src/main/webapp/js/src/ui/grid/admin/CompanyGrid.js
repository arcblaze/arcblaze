
Ext.namespace("ui.grid.admin");

ui.grid.admin.CompanyGrid = Ext.extend(Ext.grid.GridPanel, {
	constructor: function(c) {
		var company = new data.model.Company();

		var grid = this;

		this.store = new data.store.admin.CompanyStore();
		this.toolbar = new ui.tbar.admin.CompanyToolbar();

		var config = Ext.applyIf(c || {}, {
			title:            'Companies',
			id:               'ui.grid.admin.companygrid',
			store:            grid.store,
			stripeRows:       true,
			autoWidth:        true,
			height:           400,
			tbar:             grid.toolbar,
			columns:          company.getColumnModel(),
			loadMask:         true
		});

		ui.grid.admin.CompanyGrid.superclass.constructor.call(this, config);

		this.getSelectionModel().addListener('selectionchange', function(model) {
			var count = model.getSelections().length;

			var companyDel = Ext.getCmp('action.admin.company.docompanydelete');
			var companyAct = Ext.getCmp('action.admin.company.docompanyactivate');
			var companyDea = Ext.getCmp('action.admin.company.docompanydeactivate');
			var companyUpd = Ext.getCmp('action.admin.company.showcompanyupdate');

			var allActive = true;
			for (var s = 0; s < count && allActive; s++)
				allActive = model.getSelections()[s].data.active == "1";

			var allInactive = true;
			for (var s = 0; s < count && allInactive; s++)
				allInactive = model.getSelections()[s].data.active == "0";

			if (companyDel)
				(count > 0) ? companyDel.enable() : companyDel.disable();
			if (companyUpd)
				(count == 1) ? companyUpd.enable() : companyUpd.disable();
			if (companyAct)
				(count > 0 && allInactive) ?
					companyAct.enable() : companyAct.disable();
			if (companyDea)
				(count > 0 && allActive) ?
					companyDea.enable() : companyDea.disable();
		});
	},

	getSelectedIds: function() {
		var ids = [ ];
		var records = this.getSelectionModel().getSelections();
		for (var i = 0; i < records.length; i++)
			ids.push(records[i].data.id);
		return ids;
	}
});

