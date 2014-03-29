
Ext.namespace("ui.panel.admin.stats");

ui.panel.admin.stats.StatsPanel = Ext.extend(Ext.Panel, {
	constructor: function(c) {
		var panel = this;

		this.system = new ui.panel.admin.stats.SystemStatsPanel();
		this.memoryUsage = new ui.panel.admin.stats.MemoryUsagePanel();
		this.statusCodes = new ui.panel.admin.stats.StatusCodeChartPanel();
		this.monthlyRevenue = new ui.panel.admin.stats.MonthlyRevenueChartPanel();
		this.activeUsers = new ui.panel.admin.stats.ActiveUserChartPanel();
		this.activeCompanies = new ui.panel.admin.stats.ActiveCompanyChartPanel();

		var config = Ext.applyIf(c || {}, {
			id:          'ui.panel.admin.stats.statspanel',
			width:       940,
			border:      false,
			autoHeight:  true,
			layout:      'column',
			items: [
				new Ext.Panel({
					border:      false,
					autoHeight:  true,
					columnWidth: 0.40,
					items: [
						new Ext.Panel({
							border:    false,
							bodyStyle: 'padding-bottom:15px;',
							items:     panel.system
						}),
						new Ext.Panel({
							border:    false,
							bodyStyle: 'padding-bottom:15px;',
							items:     panel.memoryUsage
						}),
						new Ext.Panel({
							border:    false,
							bodyStyle: 'padding-bottom:15px;',
							items:     panel.statusCodes
						})
					]
				}),
				new Ext.Panel({
					border:      false,
					autoHeight:  true,
					columnWidth: 0.60,
					layout:      'column',
					items: [
						new Ext.Panel({
							border:      false,
							columnWidth: 0.5,
							bodyStyle:   'padding-bottom:15px;',
							items:       panel.monthlyRevenue
						}),
						new Ext.Panel({
							border:      false,
							columnWidth: 0.5,
							bodyStyle:   'padding-bottom:15px;',
							items:       panel.activeUsers
						}),
						new Ext.Panel({
							border:      false,
							columnWidth: 0.5,
							bodyStyle:   'padding-bottom:15px;',
							items:       panel.activeCompanies
						})
					]
				})
			]
		});

		ui.panel.admin.stats.StatsPanel.superclass.constructor.call(this, config);
	}
});

