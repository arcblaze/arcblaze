
Ext.namespace("ui.panel.manager.user");

ui.panel.manager.user.UserUpdatePanel = Ext.extend(Ext.Panel, {
	constructor: function(c) {
		if (!c || !c.user)
			throw "UserUpdatePanel requires an user.";

		var panel = this;

		var supervisorGrid = new ui.grid.manager.SupervisorGrid({
			user: c.user,
			columnWidth: 0.44
		});

		var privilegeItems = [ ];
		for (var r = 0; r < roles.length; r++) {
			privilegeItems.push({
				boxLabel:   roles[r],
				name:       'roles',
				id:         'user-privileges-' + roles[r].toLowerCase(),
				inputValue: roles[r],
				checked:    false
			});
		}

		// Add the update panel.
		this.form = new Ext.form.FormPanel({
			title:       'Update User Account',
			width:       400,
			autoHeight:  true,
			bodyStyle:   'padding: 10px;',
			labelWidth:  110,
			items: [
				{
					xtype: 'hidden',
					name:  'id'
				}, {
					xtype:      'textfield',
					fieldLabel: 'First Name',
					name:       'firstName',
					allowBlank: false,
					width:      260,
					labelWidth: 120
				}, {
					xtype:      'textfield',
					fieldLabel: 'Last Name',
					name:       'lastName',
					allowBlank: false,
					width:      260,
					labelWidth: 120
				}, {
					xtype:      'textfield',
					fieldLabel: 'Login',
					name:       'login',
					allowBlank: false,
					width:      220,
					labelWidth: 120
				}, {
					xtype:      'textfield',
					inputType:  'password',
					fieldLabel: 'Password',
					name:       'password',
					width:      220,
					labelWidth: 120
				}, {
					xtype:      'textfield',
					inputType:  'password',
					fieldLabel: 'Confirm Password',
					name:       'confirm',
					width:      220,
					labelWidth: 120
				}, {
					xtype:      'textfield',
					fieldLabel: 'Email',
					name:       'email',
					allowBlank: false,
					width:      375,
					labelWidth: 120
				}, {
					xtype:      'checkboxgroup',
					fieldLabel: 'Privileges',
					name:       'privileges',
					columns:    2,
					width:      320,
					labelWidth: 120,
					items:      privilegeItems
				}, {
					xtype:      'radiogroup',
					fieldLabel: 'Active',
					name:       'active',
					width:      240,
					labelWidth: 120,
					items: [
						{
							boxLabel:   'Yes',
							name:       'active',
							id:         'user-active-modify-yes',
							inputValue: 'true',
							style:      'border: 0px;'
						}, {
							boxLabel:   'No',
							name:       'active',
							id:         'user-active-modify-no',
							inputValue: 'false',
							style:      'border: 0px;'
						}
					]
				}
			],
			buttons: [
				new Ext.Button(new action.manager.user.DoUserUpdate()),
				new Ext.Button(new action.manager.user.ShowUserGrid())
			]
		});

		var config = Ext.applyIf(c || {}, {
			id:         'ui.panel.manager.user.userupdatepanel',
			border:     false,
			frame:      false,
			autoHeight: true,
			width:      780,
			layout:     'column',
			items: [
				// Add the update form.
				panel.form,

				// Add the supervisor grid.
				new Ext.Panel({
					border:    false,
					frame:     false,
					bodyStyle: 'padding-left:20px;',
					items:     supervisorGrid
				})
			]
		});

		ui.panel.manager.user.UserUpdatePanel.superclass.constructor.call(this, config);
	},

	setInitialFocus: function() {
		this.form.getForm().findField('firstName').focus();
	},

	setValues: function(user) {
		// Set the form values.
		this.form.getForm().findField('id').setValue(user.data.id);
		this.form.getForm().findField('firstName').
			setValue(user.data.firstName);
		this.form.getForm().findField('lastName').
			setValue(user.data.lastName);
		this.form.getForm().findField('login').setValue(user.data.login);
		this.form.getForm().findField('password').setValue('');
		this.form.getForm().findField('confirm').setValue('');
		this.form.getForm().findField('email').setValue(user.data.email);

		Ext.getCmp('user-active-modify-yes').setValue(user.data.active);
		Ext.getCmp('user-active-modify-no').setValue(!user.data.active);
		
		if (user.data.roles) {
			for (var r = 0; r < user.data.roles.length; r++) {
				var checkbox = Ext.getCmp('user-privileges-'
					+ user.data.roles[r].name.toLowerCase());
				if (checkbox)
					checkbox.setValue(true);
			}
		}

		// Set the form focus.
		this.setInitialFocus();
	}
});

