
// Retrieve the current timesheet from the back-end and display it on the UI.
function retrieveCurrentTimesheet(divId, user) {
	// Create a new ServerIO object.
	var io = new util.io.ServerIO();

	// Submit the form.
	io.doAjaxRequest({
		// Set the URL.
		url: '/rest/user/timesheet/current',

		// Invoked on a successful completion.
		mysuccess: function(data) {
			displayTimesheet(divId, user, data.timesheet);
			startTimer();
			initCellManagement([data.timesheet]);
		}
	});
}

function showPreviousTimesheet(divId, currentBegin) {
	var date = Ext.Date.format(new Date(currentBegin), 'Ymd');

	// Create a new ServerIO object.
	var io = new util.io.ServerIO();

	// Submit the form.
	io.doAjaxRequest({
		// Set the URL.
		url: '/rest/user/timesheet/previous/' + date,

		// Invoked on a successful completion.
		mysuccess: function(data) {
			displayTimesheet(divId, user, data.timesheet);
			initCellManagement([data.timesheet]);
			resetTimer();
		}
	});
}

function showNextTimesheet(divId, currentBegin) {
	var date = Ext.Date.format(new Date(currentBegin), 'Ymd');

	// Create a new ServerIO object.
	var io = new util.io.ServerIO();

	// Submit the form.
	io.doAjaxRequest({
		// Set the URL.
		url: '/rest/user/timesheet/next/' + date,

		// Invoked on a successful completion.
		mysuccess: function(data) {
			displayTimesheet(divId, user, data.timesheet);
			resetTimer();
			initCellManagement([data.timesheet]);
		}
	});
}

// Display the provided timesheet on the UI.
function displayTimesheet(divId, user, timesheet) {
	var html = '';
	html += '<table id="timesheet-table" cellpadding="0" cellspacing="0">';
	html += '  <tr>';
	html += '    <td>';
	html += getActionButtons(user, timesheet);
	html += '    </td>';
	html += '  </tr>';
	html += '  <tr>';
	html += '    <td>';
	html += getUserInfo(user, timesheet);
	html += '    </td>';
	html += '  </tr>';
	html += '  <tr>';
	html += '    <td>';
	html += getTimesheetData(divId, user, timesheet);
	html += '    </td>';
	html += '  </tr>';
	html += '  <tr>';
	html += '    <td>';
	html += '      <div id="inactivity"></div>';
	html += '    </td>';
	html += '  </tr>';
	html += '</table>';

	var tsdiv = document.getElementById(divId);
	tsdiv.innerHTML = html;

	activateExtComponents(user, timesheet);
}

function getActionButtons(user, timesheet) {
	var owner = user.id == timesheet.user.id;
	var edit = owner && !timesheet.completed;
	var fix = owner && timesheet.completed && !timesheet.approved;

	var html = '';
	html += '<table id="timesheet-action-buttons">';
	html += '  <tr>';
	html += '    <td>';
	if (edit) {
		html += '<div id="complete-timesheet-button"></div>';
		html += '<div id="save-timesheet-button"></div>';
	}
	if (fix) {
		html += '<div id="fix-timesheet-button"></div>';
	}
	html += '    </td>';
	html += '  </tr>';
	html += '</table>';
	return html;
}

function getUserInfo(user, timesheet) {
	var date = Ext.Date.format(new Date(), 'F j, g:ia');
	var html = '';
	html += '<table id="timesheet-user-table">';
	html += '  <tr>';
	html += '    <td id="user-label">User:</td>';
	html += '    <td id="user-name">' + timesheet.user.firstName + ' ';
	html +=        timesheet.user.lastName + '</td>';
	html += '    <td id="timesheet-date">' + date + '</td>';
	html += '    <td id="timesheet-completion">'
	html += getTimesheetStatus(user, timesheet);
	html += '    </td>'
	html += '  </tr>';
	html += '</table>';
	return html;
}

function getTimesheetStatus(user, timesheet) {
	var html = '';
	html += '<span class="status">Completed</span>';
	html += '<span id="completion-status-' + timesheet.id + '">';
	if (timesheet.completed)
		html += '<img src="/img/bullet_green.png" border="0" '
			+ 'alt="Completed" title="Completed"/>';
	else
		html += '<img src="/img/bullet_red.png" border="0" '
			+ 'alt="Not Completed" title="Not Completed"/>';
	html += '</span>';

	html += '<span class="status">Approved</span>';
	html += '<span id="approval-status-' + timesheet.id + '">';
	if (timesheet.approved)
		html += '<img src="/img/bullet_green.png" border="0" '
			+ 'alt="Approved" title="Approved"/>';
	else
		html += '<img src="/img/bullet_red.png" border="0" '
			+ 'alt="Not Approved" title="Not Approved"/>';
	html += '</span>';

	html += '<span class="status">Payroll</span>';
	html += '<span id="payroll-status-' + timesheet.id + '">';
	if (timesheet.verified)
		html += '<img src="/img/bullet_green.png" border="0" '
			+ 'alt="Payroll Processed" title="Payroll Processed"/>';
	else
		html += '<img src="/img/bullet_red.png" border="0" '
			+ 'alt="Not Processed" title="Not Processed"/>';
	html += '</span>';
	return html;
}

function getTimesheetData(divId, user, timesheet) {
	var html = '';
	html += '<table id="timesheet-data-table">';
	html += '  <tr>';
	html += getPayPeriodNavigation(divId, user, timesheet);
	html += getDateHeaders(user, timesheet);
	html += '  </tr>';
	html += '  <tr>';
	html += getDayOfWeekHeaders(user, timesheet);
	html += '  </tr>';
	html += getTasks(user, timesheet);
	html += '  <tr>';
	html += getDailyTotals(user, timesheet);
	html += '  </tr>';
	html += '  <tr>';
	html += getWeeklyTotals(user, timesheet);
	html += '  </tr>';
	html += '</table>';
	return html;
}

function getPayPeriodNavigation(divId, user, timesheet) {
	var begin = Ext.Date.format(new Date(timesheet.payPeriod.begin), 'M j');
	var end = Ext.Date.format(new Date(timesheet.payPeriod.end), 'M j');

	var prevLink = 'showPreviousTimesheet(\'' + divId + '\', ' +
			timesheet.payPeriod.begin + ');';
	var nextLink = 'showNextTimesheet(\'' + divId + '\', ' +
			timesheet.payPeriod.begin + ');';
	var chooseLink = 'choosePayPeriod(\'' + divId + '\');';

	var html = '';
	html += '<td id="pay-period-nav" colspan="2">';
	html += '  <table id="nav-table">';
	html += '    <tr>';
	html += '      <td id="prev">';
	html += '        <a href="javascript:' + prevLink + '"';
	html += '           title="Show the previous pay period"><img';
	html += '           src="/img/prev.png" border="0"';
	html += '           alt="Show the previous pay period"/></a>';
	html += '      </td>';
	html += '      <td id="range">';
	html += '        ' + begin + ' - ' + end;
	html += '      </td>';
	html += '      <td id="next">';
	html += '        <a href="javascript:' + nextLink + '"';
	html += '           title="Show the next pay period"><img';
	html += '           src="/img/next.png" border="0"';
	html += '           alt="Show the next pay period"/></a>';
	html += '      </td>';
	html += '      <td id="calendar">';
	html += '        <a href="javascript:' + chooseLink + '"';
	html += '           title="Choose a pay period to view"><img';
	html += '           src="/img/calendar.png" border="0"';
	html += '           alt="Choose a pay period to view"/></a>';
	html += '        <div id="date-picker"></div>';
	html += '      </td>';
	html += '    </tr>';
	html += '  </table>';
	html += '</td>';
	return html;
}

function getDateHeaders(user, timesheet) {
	var html = '';

	var date = timesheet.payPeriod.begin;
	var end = timesheet.payPeriod.end;
	while (date <= end) {
		var clazz = 'header-' +
			(isWeekend(date) ? "weekend" :
			(isHoliday(timesheet, date) ? "holiday" : "day"));

		html += '<td class="' + clazz + '">';
		html += Ext.Date.format(new Date(date), 'n/j');
		html += '</td>';

		date += 24 * 60 * 60 * 1000;
	}

	// Add the column for the totals.
	html += '<td id="header-task-total" rowspan="2">Total</td>';
	return html;
}

function getDayOfWeekHeaders(user, timesheet) {
	var html = '';
	html += '<td id="task-header" colspan="2">Task</td>';

	var date = timesheet.payPeriod.begin;
	var end = timesheet.payPeriod.end;
	while (date <= end) {
		var clazz = 'header-day-' +
			(isWeekend(date) ? "weekend" :
			(isHoliday(timesheet, date) ? "holiday" : "day"));

		html += '<td class="' + clazz + '">';
		html += Ext.Date.format(new Date(date), 'D');
		html += '</td>';

		date += 24 * 60 * 60 * 1000;
	}

	return html;
}

function getTasks(user, timesheet) {
	var html = '';

	var prev = undefined;
	for (var t = 0; t < timesheet.tasks.length; t++) {
		var task = timesheet.tasks[t];

		if (prev && !prev.administrative && task.administrative)
			html += getAdminTaskSeparator(user, timesheet);

		if (task.administrative || task.bills.length > 0)
			html += addTimesheetRow(user, timesheet, task, undefined);

		for (var a = 0; a < task.assignments.length; a++)
			html += addTimesheetRow(user, timesheet, task, a);

		prev = task;
	}

	return html;
}

function getAdminTaskSeparator(user, timesheet) {
	// Determine the number of columns needed.
	var cols = 3;
	var date = timesheet.payPeriod.begin;
	var end = timesheet.payPeriod.end;
	while (date <= end) {
		cols++;
		date += 24 * 60 * 60 * 1000;
	}

	var html = '';
	html += '<tr>';
	html += '  <td id="task-separator" colspan="' + cols + '">';
	html += '    &nbsp;</td>';
	html += '</tr>';
	return html;
}

function addTimesheetRow(user, timesheet, task, assignmentIndex) {
	var html = '';
	html += '<tr>';

	var assignment = (typeof(assignmentIndex) == "undefined") ?
		undefined : task.assignments[assignmentIndex];

	// Add the task label.
	html += getTaskLabel(task, assignmentIndex);

	var date = timesheet.payPeriod.begin;
	var end = timesheet.payPeriod.end;
	while (date <= end) {
		var clazz =
			(isWeekend(date) ? "weekend" :
			(isHoliday(timesheet, date) ? "holiday" : "day"));

		var owner = user.id == timesheet.user.id;
		var edit = owner && !timesheet.completed;

		if (edit && isToday(date) && !isEnd(timesheet, date) && !task.administrative)
			clazz += '-today';

		var hours = getHours(task, assignment, date);
		var expired = isExpired(task, assignment, date);
		var editable = canEdit(task, date);

		var id = 'cell' + timesheet.id + '_' + task.id + '_' +
			(assignment ? assignment.id : '') + '_' +
			Ext.Date.format(new Date(date), 'Ymd');

		if (expired) {
			html += '<td class="' + clazz + '-unavailable">';
			html += '<img src="/img/bullet_red.png" border="0" ';
			html += 'alt="Task unavailable" title="Task unavailable"/>';
			html += '</td>';
		} else if (edit && editable) {
			html += '<td id="' + id + 'td" class="' + clazz + '">';
			html += '<input id="' + id + '" type="text" ';
			html += 'class="' + clazz + '-input" value="' + hours + '">';
			html += '</td>';
		} else {
			html += '<td id="' + id + 'td" class="' + clazz + '">';
			html += '<input id="' + id + '" type="text" ';
			html += 'class="' + clazz + '-input" value="' + hours + '" readonly>';
			html += '</td>';
		}

		date += 24 * 60 * 60 * 1000;
	}

	// Add the column for the totals.
	html += '<td id="tsktot' + timesheet.id + '_' + task.id + '_' +
		(assignment ? assignment.id : '') + '" class="task-total"></td>';

	html += '</tr>';
	return html;
}

function getTaskLabel(task, assignmentIndex) {
	var html = '';
	if (typeof(assignmentIndex) == "undefined" ||
			(task.assignments.length == 1 && task.bills.length == 0)) {
		html += '  <td class="task-label" colspan="2">';
		html += task.description;
		html += '  </td>';
	} else {
		html += '  <td class="task-name">';
		html += task.description;
		html += '  </td>';
		html += '  <td class="task-lcat">';
		html += task.assignments[assignmentIndex].laborCat;
		html += '  </td>';
	}
	return html;
}

function getDailyTotals(user, timesheet) {
	var html = '';
	html += '<td id="label-daily-total" colspan="2">Total</td>';

	var date = timesheet.payPeriod.begin;
	var end = timesheet.payPeriod.end;
	while (date <= end) {
		html += '<td id="daytot' + timesheet.id + '_' +
			Ext.Date.format(new Date(date), 'Ymd') + '" ';
		html += 'class="daily-total">&nbsp;</td>';

		date += 24 * 60 * 60 * 1000;
	}

	html += '<td id="total' + timesheet.id + '" class="total">&nbsp;</td>';
	return html;
}

function getWeeklyTotals(user, timesheet) {
	var html = '';
	html += '<td id="label-weekly-total" colspan="2">Weekly Totals</td>';

	var date = timesheet.payPeriod.begin;
	var end = timesheet.payPeriod.end;
	var colspan = 0;

	while (date <= end) {
		var day = Ext.Date.format(new Date(date), 'D');

		if (day == 'Fri') {
			html += '<td id="weektot' + timesheet.id + '_';
			html += Ext.Date.format(new Date(date), 'Ymd') + '" ';
			html += 'class="weekly-total" colspan="' + (colspan + 1) + '">';
			html += '&nbsp;</td>';
		} else if (isWeekend(date)) {
			colspan = 0;
			html += '<td id="weektot' + timesheet.id + '_';
			html += Ext.Date.format(new Date(date), 'Ymd') + '" ';
			html += 'class="weekly-total-weekend">&nbsp;</td>';
		} else
			colspan++;

		date += 24 * 60 * 60 * 1000;
	}

	if (colspan > 0) {
		var yesterday = date - (24 * 60 * 60 * 1000);
		html += '<td id="weektot' + timesheet.id + '_';
		html += Ext.Date.format(new Date(yesterday), 'Ymd') + '" ';
		html += 'class="weekly-total" colspan="' + colspan + '">';
		html += '&nbsp;</td>';
	}

	html += '<td class="weekly-total-total">&nbsp;</td>';
	return html;
}

function getHours(task, assignment, date) {
	var dateStr = Ext.Date.format(new Date(date), 'Ymd');
	var bills = (assignment ? assignment.bills : task.bills);
	for (var b = 0; b < bills.length; b++) {
		var bill = bills[b];
		var billDate = Ext.Date.format(new Date(bill.day), 'Ymd');

		if (dateStr == billDate)
			return bill.hours;
	}
	return '';
}

function isExpired(task, assignment, date) {
	if (task.administrative || !assignment)
		return false;

	return assignment.begin > date || assignment.end < date;
}

function canEdit(task, date) {
	if (task.administrative)
		return true;

	// If date is in the future, than not editable.
	return Ext.Date.format(new Date(date), 'Ymd') <=
		Ext.Date.format(new Date(), 'Ymd');
}

function isToday(date) {
	return Ext.Date.format(new Date(), 'Ymd') ==
		Ext.Date.format(new Date(date), 'Ymd');
}

function isEnd(timesheet, date) {
	return Ext.Date.format(new Date(timesheet.payPeriod.end), 'Ymd') ==
		Ext.Date.format(new Date(date), 'Ymd');
}

function isWeekend(day) {
	var dayNum = Ext.Date.format(new Date(day), 'w');
	return dayNum == 0 || dayNum == 6;
}

function isHoliday(timesheet, day) {
	for (var h = 0; h < timesheet.holidays.length; h++)
		if (timesheet.holidays[h].day == day)
			return true;
	return false;
}

function activateExtComponents(user, timesheet) {
	if (document.getElementById('complete-timesheet-button')) {
		new Ext.Button(new action.timesheet.DoComplete(timesheet.id))
			.render('complete-timesheet-button');
	}
	if (document.getElementById('save-timesheet-button')) {
		new Ext.Button(new action.timesheet.DoSave(timesheet.id))
			.render('save-timesheet-button');
	}
	if (document.getElementById('fix-timesheet-button')) {
		new Ext.Button(new action.timesheet.DoFix(timesheet.id))
			.render('fix-timesheet-button');
	}
}

