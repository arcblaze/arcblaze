
// Invoked when the user wants to choose a new timesheet pay period.
function choosePayPeriod(divId) {
	// Create and display the date picker.
	new Ext.DatePicker({
		// Where to draw the picker.
		renderTo: 'date-picker',

		// Invoked after a date is chosen.
		handler: function(picker, chosenDate) {
			// Hide the date picker.
			picker.hide();
			var date = Ext.Date.format(chosenDate, 'Ymd');

			// Create a new ServerIO object.
			var io = new util.io.ServerIO();

			// Submit the form.
			io.doAjaxRequest({
				// Set the URL.
				url: '/rest/user/timesheet/custom/' + date,

				// Invoked on a successful completion.
				mysuccess: function(data) {
					displayTimesheet(divId, user, data.timesheet);
					startTimer();
					initCellManagement([data.timesheet]);
				}
			});
		}
	}).show();
}

