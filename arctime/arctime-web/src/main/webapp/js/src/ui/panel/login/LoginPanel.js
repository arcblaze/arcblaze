
Ext.namespace("ui.panel.login");

ui.panel.login.LoginPanel = Ext.extend(Ext.form.FormPanel, {
	constructor: function(c) {
		var form = this;

		this.redirectUri = new Ext.form.Hidden({
			name:  'redirectUri',
			value: c ? c.redirectUri : undefined
		});

		this.login = new Ext.form.TextField({
			fieldLabel: 'Login or Email',
			name:       'login',
			allowBlank: false,
			width:      300,
			value:      '',
			listeners: {
				specialkey: function(tf, evt) {
					// Listen for the Enter key.
					if (evt.ENTER == evt.getKey()) {
						// Get the form values.
						var vals = form.getForm().getValues();

						// Submit the form if the password has a value.
						if (vals.password) {
							// Get the login action.
							var login = Ext.getCmp(
								'action.login.dologin');

							// Invoke the handler.
							if (login)
								login.handler();
						} else
							// Focus on the password field if it doesn't
							// have a value.
							form.getForm().findField('password').focus();
					}
				}
			}
		});
		this.passwd = new Ext.form.TextField({
			inputType:  'password',
			fieldLabel: 'Password',
			name:       'password',
			width:      300,
			value:      '',
			listeners: {
				specialkey: function(tf, evt) {
					// Listen for the Enter key.
					if (evt.ENTER == evt.getKey()) {
						// Get the form values.
						var vals = form.getForm().getValues();

						// Submit the form if the password has a value.
						if (vals.login) {
							// Get the login action.
							var login = Ext.getCmp(
								'action.login.dologin');

							// Invoke the handler.
							if (login)
								login.handler();
						} else
							// Focus on the login field if it doesn't
							// have a value.
							form.getForm().findField('login').focus();
					}
				}
			}
		});

		var config = Ext.applyIf(c || {}, {
			id:             'ui.panel.login.loginpanel',
			title:          'Login',
			width:          330,
			autoHeight:     true,
			bodyStyle:      'padding: 10px;',
			labelWidth:     140,
			standardSubmit: true,
			items: [
				form.redirectUri,
				form.login,
				form.passwd
			],
			buttons: [
				new Ext.Button(new action.login.DoLogin()),
				new Ext.Button(new action.login.ResetPassword())
			]
		});

		ui.panel.login.LoginPanel.superclass.constructor.call(this, config);
	},

	setInitialFocus: function() {
		this.login.focus();
	}
});

