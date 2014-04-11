
Ext.namespace("data.model");

Ext.define('data.model.Company', {
	extend: 'Ext.data.Model',
	fields: [
		{
			id:        'id',
			name:      'id',
			dataIndex: 'id',
			header:    'ID',
			width:     40,
			hidden:    true,
			sortable:  true
		}, {
			id:        'name',
			name:      'name',
			dataIndex: 'name',
			header:    'Name',
			width:     400,
			sortable:  true
		}, {
			id:        'active',
			name:      'active',
			dataIndex: 'active',
			header:    'Active',
			width:     60,
			sortable:  true,
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
				id:        field.id,
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

