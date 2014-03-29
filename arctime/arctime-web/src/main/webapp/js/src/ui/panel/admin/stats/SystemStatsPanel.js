
Ext.namespace("ui.panel.admin.stats");

ui.panel.admin.stats.SystemStatsPanel = Ext.extend(Ext.Panel, {
	constructor: function(c) {
		var panel = this;

		this.store = new data.store.admin.SystemStatsStore();

		var config = Ext.applyIf(c || {}, {
			id:             'ui.panel.admin.stats.systemstatspanel',
			title:          'System Statistics',
			width:          350,
			autoHeight:     true,
			items: [
				new Ext.DataView({
					itemSelector: 'table',
					autoHeight:   true,
					store:        panel.store,
					tpl:          new Ext.XTemplate(
						'<table id="system-stats-table" class="x-form-item">',
						  '<tpl for=".">',
							'<tr>',
							  '<td class="name" nowrap>',
								'{name}:',
							  '</td>',
							  '<td class="value">',
								'{value}',
							  '</td>',
							'</tr>',
						  '</tpl>',
						'</table>'
					)
				})
			]
		});

		ui.panel.admin.stats.SystemStatsPanel.superclass.constructor.call(this, config);
	}
});

