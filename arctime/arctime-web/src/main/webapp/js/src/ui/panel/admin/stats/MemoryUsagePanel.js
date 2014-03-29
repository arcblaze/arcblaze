
Ext.namespace("ui.panel.admin.stats");

ui.panel.admin.stats.MemoryUsagePanel = Ext.extend(Ext.Panel, {
	constructor: function(c) {
		var panel = this;

		var config = Ext.applyIf(c || {}, {
			id:     'ui.panel.admin.stats.memoryusagepanel',
			title:  'Memory Usage',
			width:  350,
			items: [
				new Ext.Panel({
					html:   '<span id="heap-memory-usage"></span>' +
					        '<span id="nonheap-memory-usage"></span>' +
					        '<span id="memory-details"></span>',
					border: false,
					width:  350,
					listeners: {
						afterrender: function() {
							var heapConfig = {
								size: 120,
								label: "Heap",
								min: 0,
								max: 100,
								minorTicks: 5
							}
							var nonHeapConfig = {
								size: 120,
								label: "Non-Heap",
								min: 0,
								max: 100,
								minorTicks: 5
							}

							var yellow = 0.60;
							var red = 0.80;

							heapConfig.yellowZones = [{ from: 100*yellow, to: 100*red }];
							heapConfig.redZones = [{ from: 100*red, to: 100 }];
							nonHeapConfig.yellowZones = [{ from: 100*yellow, to: 100*red }];
							nonHeapConfig.redZones = [{ from: 100*red, to: 100 }];

							panel.heapGauge = new Gauge("heap-memory-usage", heapConfig);
							panel.heapGauge.render();
							panel.nonHeapGauge = new Gauge("nonheap-memory-usage", nonHeapConfig);
							panel.nonHeapGauge.render();
						}
					}
				})
			]
		});

		ui.panel.admin.stats.MemoryUsagePanel.superclass.constructor.call(this, config);

		this.update();
		setInterval(this.update, 5000);
	},

	update: function() {
		var panel = Ext.getCmp('ui.panel.admin.stats.memoryusagepanel');
		var io = new util.io.ServerIO();
		if (panel.heapGauge) {
			io.doAjaxRequest({
				url: '/rest/admin/stats/memory/heap',

				mysuccess: function(data) {
					panel.heapGauge.redraw(data.usage);
				}
			});
		}
		if (panel.nonHeapGauge) {
			io.doAjaxRequest({
				url: '/rest/admin/stats/memory/nonheap',

				mysuccess: function(data) {
					panel.nonHeapGauge.redraw(data.usage);
				}
			});
		}
		io.doAjaxRequest({
			url: '/rest/admin/stats/memory/info',

			mysuccess: function(data) {
				var div = document.getElementById('memory-details');
				if (div) {
					var html = '';
					html += '<table id="memory-details-table">';
					html += '  <tr>';
					html += '    <td id="heap-details">';
					html += '      <table id="heap-details-table">';
					html += '        <tr>';
					html += '          <td class="label">Total:</td>';
					html += '          <td class="value">' + data.maxHeap + 'M</td>';
					html += '        </tr>';
					html += '        <tr>';
					html += '          <td class="label">Used:</td>';
					html += '          <td class="value">' + data.usedHeap + 'M</td>';
					html += '        </tr>';
					html += '        <tr>';
					html += '          <td class="label">Used %:</td>';
					html += '          <td class="value">' + data.heapPercentUsed + '%</td>';
					html += '        </tr>';
					html += '      </table>';
					html += '    </td>';
					html += '    <td id="nonheap-details">';
					html += '      <table id="nonheap-details-table">';
					html += '        <tr>';
					html += '          <td class="label">Total:</td>';
					html += '          <td class="value">' + data.maxNonHeap + 'M</td>';
					html += '        </tr>';
					html += '        <tr>';
					html += '          <td class="label">Used:</td>';
					html += '          <td class="value">' + data.usedNonHeap + 'M</td>';
					html += '        </tr>';
					html += '        <tr>';
					html += '          <td class="label">Used %:</td>';
					html += '          <td class="value">' + data.nonHeapPercentUsed + '%</td>';
					html += '        </tr>';
					html += '      </table>';
					html += '    </td>';
					html += '  </tr>';
					html += '</table>';
					div.innerHTML = html;
				}
			}
		});
	}
});

