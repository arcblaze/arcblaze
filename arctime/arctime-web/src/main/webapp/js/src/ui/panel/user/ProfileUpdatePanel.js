
Ext.namespace("ui.panel.user");

ui.panel.user.ProfileUpdatePanel = Ext.extend(Ext.form.FormPanel, {
	constructor: function(c) {
		if (!c || !c.user)
			throw "ProfileUpdatePanel requires an user.";

		var form = this;

		var config = Ext.applyIf(c || {}, {
			id:         'ui.panel.user.profileupdatepanel',
			border:     true,
			frame:      false,
			title:       'Update Profile',
			width:       390,
			autoHeight:  true,
			bodyStyle:   'padding: 10px;',
			labelWidth:  110,
			items: [
				{
					xtype:      'textfield',
					fieldLabel: 'First Name',
					name:       'firstName',
					allowBlank: false,
					width:      300
				}, {
					xtype:      'textfield',
					fieldLabel: 'Last Name',
					name:       'lastName',
					allowBlank: false,
					width:      300
				}, {
					xtype:      'textfield',
					fieldLabel: 'Login',
					name:       'login',
					allowBlank: false,
					width:      240
				}, {
					xtype:      'textfield',
					inputType:  'password',
					fieldLabel: 'Password',
					name:       'password',
					width:      240
				}, {
					xtype:      'textfield',
					inputType:  'password',
					fieldLabel: 'Confirm Password',
					name:       'confirm',
					width:      240
				}, {
					xtype:      'textfield',
					fieldLabel: 'Email',
					name:       'email',
					allowBlank: false,
					width:      360
				}
			],
			buttons: [
				new Ext.Button(new action.user.DoProfileUpdate())
			]
		});

		ui.panel.user.ProfileUpdatePanel.superclass.constructor.call(this, config);

		// Set the values in the form.
		this.setValues(c.user);
	},

	setInitialFocus: function() {
		this.getForm().findField('firstName').focus();
	},

	setValues: function(user) {
		// Set the form values.
		this.getForm().findField('firstName').
			setValue(user.data.firstName);
		this.getForm().findField('lastName').
			setValue(user.data.lastName);
		this.getForm().findField('login').setValue(user.data.login);
		this.getForm().findField('password').setValue('');
		this.getForm().findField('confirm').setValue('');
		this.getForm().findField('email').setValue(user.data.email);

		// Set the form focus.
		this.setInitialFocus();
	}
});

