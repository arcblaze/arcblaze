
Ext.namespace("ui.panel.admin.stats");

ui.panel.admin.stats.MonthlyRevenueChartPanel = Ext.extend(Ext.Panel, {
	constructor: function(c) {
		var panel = this;

		this.chartWidth = 240;
		this.chartHeight = 180;

		var config = Ext.applyIf(c || {}, {
			id:         'ui.panel.admin.stats.monthlyrevenuechartpanel',
			title:      'Monthly Revenue',
			width:  panel.chartWidth + 20,
			height: panel.chartHeight + 30,
			tools: [
				{
					type:   'help',
					handler: function() {
						document.location = '/admin/transactions/';
					}
				}
			],
			items: [
				new Ext.Panel({
					html:   '<div id="monthly-revenue-chart"></div>',
					border: false,
					width:  panel.chartWidth,
					height: panel.chartHeight,
					listeners: {
						afterrender: function() {
							var margin = {
								top:    20,
								right:  6,
								bottom: 30,
								left:   35
							};

							var width = panel.chartWidth - margin.left - margin.right;
							var height = panel.chartHeight - margin.top - margin.bottom;

							var x = d3.scale.ordinal().rangeRoundBands([0, width], .1);
							var y = d3.scale.linear().range([height, 0]);

							var xAxis = d3.svg.axis().scale(x).orient("bottom");
							var yAxis = d3.svg.axis().scale(y).orient("left");

							var tip = d3.tip()
							  .attr('class', 'd3-tip-revenue-chart')
							  .offset([-10, 0])
							  .html(function(d) {
								return "<strong>Amount:</strong> <span style='color:red'>$" +
									d.amount + "</span>";
							  });

							var svg = d3.select("#monthly-revenue-chart").append("svg")
								.attr("width", width + margin.left + margin.right)
								.attr("height", height + margin.top + margin.bottom)
							    .append("g")
								.attr("transform", "translate(" + margin.left + "," + margin.top + ")");

							svg.call(tip);

							var begin = new Date(new Date().getTime() - 334*24*60*60*1000);
							var end = new Date(new Date().getTime() + 24*60*60*1000);

							var url = "/rest/admin/stats/revenue"
									+ "?begin=" + Ext.Date.format(begin, 'Y-m') + '-01'
									+ "&end=" + Ext.Date.format(end, 'Y-m-d');

							d3.tsv(url, function(d) { return d; }, function(error, data) {
							  x.domain(data.map(function(d) { return d.index; }));
							  y.domain([0, d3.max(data, function(d) { return d.amount; })]);

							  svg.append("g")
								  .attr("class", "x axis")
								  .attr("transform", "translate(0," + height + ")")
								  .call(xAxis);

							  svg.append("g")
								  .attr("class", "y axis")
								  .call(yAxis)
								.append("text")
								  .attr("y", -6)
								  .attr("dy", ".71em")
								  .style("text-anchor", "end")
								  .text("$");

							  svg.selectAll(".bar")
								  .data(data)
								.enter().append("rect")
								  .attr("class", "bar")
								  .attr("x", function(d) { return x(d.index); })
								  .attr("width", x.rangeBand())
								  .attr("y", function(d) { return y(d.amount); })
								  .attr("height", function(d) { return height - y(d.amount); })
								  .on('mouseover', tip.show)
								  .on('mouseout', tip.hide)

							});
						}
					}
				})
			]
		});

		ui.panel.admin.stats.MonthlyRevenueChartPanel.superclass.constructor.call(this, config);
	}
});

