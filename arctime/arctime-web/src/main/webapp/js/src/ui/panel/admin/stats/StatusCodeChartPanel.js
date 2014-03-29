
Ext.namespace("ui.panel.admin.stats");

ui.panel.admin.stats.StatusCodeChartPanel = Ext.extend(Ext.Panel, {
	constructor: function(c) {
		var panel = this;

		this.chartWidth = 350;
		this.chartHeight = 180;

		var config = Ext.applyIf(c || {}, {
			id:     'ui.panel.admin.stats.statuscodechartpanel',
			title:  'Status Codes',
			width:  panel.chartWidth,
			height: panel.chartHeight + 27,
			items: [
				new Ext.Panel({
					html:   '<div id="status-code-chart"></div>',
					border: false,
					width:  panel.chartWidth,
					height: panel.chartHeight,
					listeners: {
						afterrender: function() {
							d3.json("/rest/admin/metrics", function(error, data) {
								// Parse the data into the statusData arr.
								var statusData = [ ];
								var index = 0;
								for (var i in data.meters) {
									var val = data.meters[i];
									var lastIndex = i.lastIndexOf('.');
									statusData.push({
										index: index++,
										name: i.substring(lastIndex+1, i.length),
										count: val.count,
										rate: val.mean_rate * 1000,
									});
								}

								var domainData = [ ];
								statusData.sort(function(a,b) {
									var diff = b.count - a.count;
									return diff != 0 ? diff : a.name.localeCompare(b.name);
								});
								for (var i = 0; i < statusData.length; i++)
									domainData.push(statusData[i].index);

								var chart = d3.select("#status-code-chart") 
								  .append('svg')
								  .attr('class', 'status-code-chart')
								  .attr('width', panel.chartWidth)
								  .attr('height', panel.chartHeight);

								var x = d3.scale.linear()
								   .domain([0, d3.max(statusData, function(v) { return v.count; })])
								   .range([0, panel.chartWidth]);

								var y = d3.scale.ordinal()
								   .domain(domainData)
								   .rangeBands([0, panel.chartHeight]);

								chart.selectAll("rect")
								   .data(statusData)
								   .enter()
								   .append("rect")
								   .attr("class", "bar")
								   .attr("x", 0)
								   .attr("y", function(v) { return y(v.index); })
								   .attr("width", function (v) {  return x(v.count); })
								   .attr("height", y.rangeBand());

								chart.selectAll("text")
									.data(statusData)
									.enter().append("text")
									.attr("class", "bar-text")
									.attr("x", panel.chartWidth)
									.attr("y", function(v) { return y(v.index) + y.rangeBand()/2;})
									.attr("dx", -5)
									.attr("dy", ".36em")
									.attr("text-anchor", "end")
									.text(function(v) { return v.name + ' (' + v.count + ')'; });
							});
						}
					}
				})
			]
		});

		ui.panel.admin.stats.StatusCodeChartPanel.superclass.constructor.call(this, config);
	}
});

