
Ext.namespace("data.model");

Ext.define('data.model.Supervisor', {
	extend: 'Ext.data.Model',
	fields: [
		{
			name:      'id',
			dataIndex: 'id',
			header:    'ID',
			width:     40,
			hidden:    true,
			sortable:  true,
			type:      'int'
		}, {
			name:      'fullName',
			dataIndex: 'fullName',
			header:    'Name',
			width:     180,
			sortable:  true,
			type:      'string'
		}, {
			name:      'primary',
			dataIndex: 'primary',
			header:    'Primary',
			width:     60,
			sortable:  true,
			type:      'boolean',
			renderer:  function(val) {
				return val ? "Yes" : "No";
			}
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

