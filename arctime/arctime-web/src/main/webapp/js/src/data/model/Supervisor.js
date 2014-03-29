
Ext.namespace("data.model");

Ext.define('data.model.Supervisor', {
	extend: 'Ext.data.Model',
	fields: [
		{
			id:        'id',
			name:      'id',
			dataIndex: 'id',
			header:    'ID',
			width:     40,
			hidden:    true,
			sortable:  true,
			type:      'int'
		}, {
			id:        'login',
			name:      'login',
			dataIndex: 'login',
			header:    'Login',
			width:     80,
			hidden:    true,
			sortable:  true,
			type:      'string'
		}, {
			id:        'firstName',
			name:      'firstName',
			dataIndex: 'firstName',
			header:    'First Name',
			width:     70,
			sortable:  true,
			hidden:    true,
			type:      'string'
		}, {
			id:        'lastName',
			name:      'lastName',
			dataIndex: 'lastName',
			header:    'Last Name',
			width:     90,
			sortable:  true,
			hidden:    true,
			type:      'string'
		}, {
			id:        'fullName',
			name:      'fullName',
			dataIndex: 'fullName',
			header:    'Full Name',
			width:     130,
			sortable:  true,
			type:      'string'
		}, {
			id:        'email',
			name:      'email',
			dataIndex: 'email',
			header:    'Email',
			width:     260,
			sortable:  true,
			type:      'string'
		}, {
			id:        'primary',
			name:      'primary',
			dataIndex: 'primary',
			header:    'Primary',
			width:     60,
			sortable:  true,
			type:      'boolean',
			renderer:  function(val) {
				return ("" + val) == "true" ? "Yes" : "No";
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

