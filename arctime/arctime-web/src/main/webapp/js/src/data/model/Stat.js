
Ext.namespace("data.model");

Ext.define('data.model.Stat', {
	extend: 'Ext.data.Model',
	fields: [
		{
			name:      'name',
			dataIndex: 'name',
			header:    'Name',
			width:     140,
			sortable:  true
		}, {
			name:      'value',
			dataIndex: 'value',
			header:    'Value',
			width:     80,
			sortable:  true
		}
	],

	getColumnModel: function() {
		var flds = [ ];
		this.fields.each(function(field) {
			flds.push({
				dataIndex: field.dataIndex,
				hidden:    field.hidden,
				renderer:  field.renderer,
				sortable:  field.sortable,
				text:      field.header,
				type:      field.type,
				width:     field.width
			});
		});
		return flds;
	}
});

