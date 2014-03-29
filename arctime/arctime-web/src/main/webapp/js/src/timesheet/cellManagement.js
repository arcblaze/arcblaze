
// The flag used to keep track of whether the timesheet needs saved.
var needsSaved = { };

// Keep track of any reasons specified for changing values.
var reasons = { };

// The timesheets under management.
var timesheets = { };

// Initialize the cell management for all available timesheets.
function initCellManagement(ts) {
	// Iterate over all the available timesheets.
	for (var t = 0; t < ts.length; t++) {
		timesheets[ts[t].id] = ts[t];

		// Iterate over all the available days.
		var date = ts[t].payPeriod.begin;
		var end = ts[t].payPeriod.end;
		while (date <= end) {
			var day = Ext.Date.format(new Date(date), 'Ymd');

			// Iterate over all the available tasks.
			for (var c = 0; c < ts[t].tasks.length; c++) {
				var task = ts[t].tasks[c];

				// Build the id for this task/day.
				var id = 'cell' + ts[t].id + '_' +
					task.id + '__' + day;
				var cell = document.getElementById(id);
				if (cell) {
					cell.onfocus = cellFocus;
					cell.onblur = cellBlur;
					formatCell(cell);
					updateTaskTotal(ts[t].id, task.id, '');
				}

				for (var a = 0; a < task.assignments.length; a++) {
					var assignment = task.assignments[a];
					// Build the id for this assignment/day.
					var id = 'cell' + ts[t].id + '_' + task.id + '_' +
						assignment.id + '_' + day;
					var cell = document.getElementById(id);
					if (cell) {
						cell.onfocus = cellFocus;
						cell.onblur = cellBlur;
						formatCell(cell);
						updateTaskTotal(ts[t].id, task.id, assignment.id);
					}
				}
			}

			updateDayTotal(ts[t].id, day);
			updateWeekTotal(ts[t].id, day);
			date += 24 * 60 * 60 * 1000;
		}

		updateTotal(ts[t].id);
	}
}

// Retrieve all the data contained in this timesheet.
function getCellData(timesheetId) {
	var ts = timesheets[timesheetId];

	// This array will hold all the data.
	var data = [ ];

	// Iterate over all the available days.
	var date = ts.payPeriod.begin;
	var end = ts.payPeriod.end;
	while (date <= end) {
		var day = Ext.Date.format(new Date(date), 'Ymd');

		// Iterate over all the available tasks.
		for (var c = 0; c < ts.tasks.length; c++) {
			var task = ts.tasks[c];

			// Build the id for this task/day.
			var id = 'cell' + ts.id + '_' + task.id + '__' + day;
			var cell = document.getElementById(id);
			if (cell && !isNaN(parseFloat(cell.value))) {
				var reason = reasons[ts.id + ':' + task.id + '::' + day];
				data.push(task.id + '_:' + day + ':' + cell.value +
						(reason ? ':' + reason : ''));
			}

			for (var a = 0; a < task.assignments.length; a++) {
				var assignment = task.assignments[a];

				// Build the id for this task/day.
				var id = 'cell' + ts.id + '_' + task.id + '_' +
					assignment.id + '_' + day;
				var cell = document.getElementById(id);
				if (cell && !isNaN(parseFloat(cell.value))) {
					var reason = reasons[ts.id + ':' + task.id + ':' +
						assignment.id + ':' + day];
					data.push(task.id + '_' + assignment.id + ':' + day +
							':' + cell.value + (reason ? ':' + reason : ''));
				}
			}
		}

		date += 24 * 60 * 60 * 1000;
	}

	// Return the identified cell data all joined together.
	return data.join(';');
}

// Keep track of the previous value in a cell before it was modified.
var previousValue = undefined;

// This function is invoked when a timesheet cell gains the focus.
function cellFocus(event) {
	// Reset the activity timer.
	resetTimer();

	// Get the cell.
	var cell = event ? event.target : this;

	// Save the previous value.
	previousValue = cell.value;
}

// This function is invoked when a timesheet cell loses the focus.
function cellBlur(event) {
	// Reset the activity timer.
	resetTimer();

	// Get the cell.
	var cell = event ? event.target : this;

	// Validate the cell value.
	if (!validateCell(cell))
		return false;

	formatCell(cell);

	// Update all the totals.
	var timesheetId = getTimesheetIdFromCellId(cell.id);
	updateTaskTotal(timesheetId, getTaskIdFromCellId(cell.id),
			getAssignmentIdFromCellId(cell.id));
	updateDayTotal(timesheetId, getDayFromCellId(cell.id));
	updateWeekTotal(timesheetId, getDayFromCellId(cell.id));
	updateTotal(timesheetId);

	// Check to see if the value was modified.
	if (previousValue != cell.value && !cell.style.backgroundImage) {
		// Update the cell's class to show that it is dirty.
		cell.style.backgroundImage = 'url(/img/dirty.gif)';
		cell.style.backgroundRepeat = 'no-repeat';
		cell.style.backgroundPosition = 'left top';

		// Update the save flag.
		needsSaved[timesheetId] = true;

		// Get the cell day.
		var cellDay = getDayFromCellId(cell.id);

		// Get today.
		var today = Ext.Date.format(new Date(), 'Ymd');

		// Get the task id for the cell.
		var taskId = getTaskIdFromCellId(cell.id);
		// Get the assignment id for the cell.
		var assignmentId = getAssignmentIdFromCellId(cell.id);

		// Do we need the user to provide a reason for changing the value?
		if (previousValue != '' && cellDay != today) {
			// Create the input field.
			var reasonField = new Ext.form.TextField({
				name: 'reason',
				fieldLabel: 'Reason',
				width: 320
			});

			// Create the reason panel.
			var pnl = new Ext.form.FormPanel({
				border: false,
				frame: false,
				labelWidth: 40,
				bodyStyle: 'padding: 10px;',
				items: [
					new Ext.Panel({
						border: false,
						frame: false,
						bodyStyle: 'padding-bottom: 10px;',
						items: new Ext.form.Label({
							text: 'When changing previously entered hours '
								+ 'in your time sheet, you are required to '
								+ 'provide a reason for making the change. '
								+ 'This is a DCAA auditing requirement. '
								+ 'Please provide a reason below.\n'
						})
					}),
					reasonField
				]
			});

			// Show the reason window.
			var win = new Ext.Window({
				title: 'Time Change Reason',
				width: 380, height: 167,
				layout: 'fit',
				modal: true,
				items: pnl,
				closable: false,
				buttons: [
					{
						text: 'Done',
						handler: function() {
							// Get the reason value.
							var rsn = reasonField.getValue();

							// Make sure the reason is valid.
							if (!rsn || rsn.match(/^\s*$/)) {
								// Display the error message.
								reasonField.markInvalid(
									'A reason must be provided.');
							} else {
								// Remove all colons and semi-colons
								// from the reason.
								rsn = rsn.replace(/:/g, ' ');
								rsn = rsn.replace(/;/g, ' ');

								// Save the reason.
								reasons[timesheetId + ':' + taskId + ':' +
									assignmentId + ':' + cellDay] = rsn;

								// Close the window.
								win.close();
							}
						}
					}
				]
			});
			win.show();
		}
	}
}

// Make sure the number in the cell is correct.
function formatCell(cell) {
	if (!cell)
		return;

	// Determine what to do based on the value.
	if (cell.value.match(/^\s*$/)) {
		// Blank out zero values.
		cell.value = "";
	} else {
		// Make sure the cell value is rounded appropriately.
		cell.value = Math.round(cell.value * 20) / 20;

		// Make sure the correct number of decimals is displayed.
		var pieces = ("" + cell.value).split(/\./);
		if (pieces.length == 1)
			cell.value += ".00";
		else if (pieces[1].length == 1)
			cell.value += "0";
	}
}

// Clear the dirty flag on all the cells.
function clearDirtyFlags(timesheetId) {
	var ts = timesheets[timesheetId];

	// Iterate over all the available days.
	var date = ts.payPeriod.begin;
	var end = ts.payPeriod.end;
	while (date <= end) {
		var day = Ext.Date.format(new Date(date), 'Ymd');

		// Iterate over all the available tasks.
		for (var c = 0; c < ts.tasks.length; c++) {
			var task = ts.tasks[c];

			var id = 'cell' + ts.id + '_' + task.id + '__' + day;
			var cell = document.getElementById(id);
			if (cell && cell.style.backgroundImage)
				cell.style.backgroundImage = '';

			for (var a = 0; a < task.assignments.length; a++) {
				var assignment = task.assignments[a];

				var id = 'cell' + ts.id + '_' + task.id + '_' +
					assignment.id + '_' + day;
				var cell = document.getElementById(id);
				if (cell && cell.style.backgroundImage)
					cell.style.backgroundImage = '';
			}
		}

		date += 24 * 60 * 60 * 1000;
	}

	// Update the save flag.
	needsSaved[timesheetId] = false;
}

// Update the task total column.
function updateTaskTotal(timesheetId, taskId, assignmentId) {
	var ts = timesheets[timesheetId];

	// This will keep track of the total.
	var total = 0;

	// Iterate over all the available days.
	var date = ts.payPeriod.begin;
	var end = ts.payPeriod.end;
	while (date <= end) {
		var day = Ext.Date.format(new Date(date), 'Ymd');

		// Build the id for the requested assignment/day.
		var id = 'cell' + ts.id + '_' + taskId + '_' +
			assignmentId + '_' + day;
		var cell = document.getElementById(id);
		if (cell) {
			var cellVal = parseFloat(cell.value);
			if (!isNaN(cellVal))
				total += cellVal;
		}

		date += 24 * 60 * 60 * 1000;
	}

	// Make sure the correct number of decimals is displayed.
	total = Math.round(total * 20) / 20;
	var pieces = ("" + total).split(/\./);
	if (pieces.length == 1)
		total += ".00";
	else if (pieces[1].length == 1)
		total += "0";

	// Get the task total element to update.
	var tsktot = document.getElementById(
			'tsktot' + timesheetId + '_' + taskId + '_' + assignmentId);

	// Set the new value.
	tsktot.innerHTML = total;
}

// Update the day total column.
function updateDayTotal(timesheetId, day) {
	var ts = timesheets[timesheetId];

	// This will keep track of the total.
	var total = 0;

	// Iterate over the available tasks.
	for (var c = 0; c < ts.tasks.length; c++) {
		var task = ts.tasks[c];

		// Build the id for this task/day.
		var id = 'cell' + timesheetId + '_' + task.id + '__' + day;
		var cell = document.getElementById(id);
		if (cell) {
			var cellVal = parseFloat(cell.value);
			if (!isNaN(cellVal))
				total += cellVal;
		}

		for (var a = 0; a < task.assignments.length; a++) {
			var assignment = task.assignments[a];

			// Build the id for this assignment/day.
			var id = 'cell' + timesheetId + '_' + task.id + '_' +
				assignment.id + '_' + day;
			var cell = document.getElementById(id);
			if (cell) {
				var cellVal = parseFloat(cell.value);
				if (!isNaN(cellVal))
					total += cellVal;
			}
		}
	}

	// Make sure the correct number of decimals is displayed.
	total = Math.round(total * 20) / 20;
	var pieces = ("" + total).split(/\./);
	if (pieces.length == 1)
		total += ".00";
	else if (pieces[1].length == 1)
		total += "0";

	// Get the day total element to update.
	var daytot = document.getElementById('daytot' + timesheetId + '_' + day);

	// Set the new value.
	daytot.innerHTML = total;
}


// Convert a day string like "20120101" into a Date object.
function dayToDate(day) {
	var date = day.substring(4, 6) + '/' + day.substring(6, 8)
			+ '/' + day.substring(0, 4);

	return new Date(Date.parse(date));
}


// Update the weekly total cell.
function updateWeekTotal(timesheetId, day) {
	var ts = timesheets[timesheetId];

	// Make sure we aren't on a weekend.
	var dayDate = dayToDate(day);
	if (dayDate.getDay() == 0 || dayDate.getDay() == 6)
		return;

	var days = [ ];
	var date = ts.payPeriod.begin;
	var end = ts.payPeriod.end;
	while (date <= end) {
		days.push(Ext.Date.format(new Date(date), 'Ymd'));
		date += 24 * 60 * 60 * 1000;
	}

	// Iterate over all the available days to find which one changed.
	var idx = -1;
	for (var d = 0; idx < 0 && d < days.length; d++)
		if (day == days[d])
			idx = d;

	// Determine the week beginning and ending days.
	var begin = dayToDate(day);
	var beginIdx = idx;
	var end = dayToDate(day);
	var endIdx = idx;
	while (begin.getDay() > 1 && beginIdx > 0)
		begin = dayToDate(days[--beginIdx]);
	while (end.getDay() < 5 && endIdx < days.length - 1)
		end = dayToDate(days[++endIdx]);

	// Determine the total hours for the week.
	total = 0;
	for (var d = beginIdx; d <= endIdx; d++) {
		val = document.getElementById('daytot' + ts.id + '_' + days[d]).innerHTML;
		total += parseFloat(val);
	}

	// Make sure the correct number of decimals is displayed.
	total = Math.round(total * 20) / 20;
	var pieces = ("" + total).split(/\./);
	if (pieces.length == 1)
		total += ".00";
	else if (pieces[1].length == 1)
		total += "0";

	// Get the day total element to update.
	var weektot = document.getElementById('weektot' + ts.id + '_' + days[endIdx]);

	// Set the new value.
	if (weektot)
		weektot.innerHTML = total;
}

// Update the total cell.
function updateTotal(timesheetId) {
	// Find the timesheet.
	var ts = timesheets[timesheetId];

	// This will keep track of the total.
	var total = 0;

	// Iterate over the available tasks.
	for (var c = 0; c < ts.tasks.length; c++) {
		var task = ts.tasks[c];

		// Build the id for this task total.
		var id = 'tsktot' + timesheetId + '_' + task.id + '_';
		var tsktot = document.getElementById(id);
		if (tsktot) {
			var tsktotVal = parseFloat(tsktot.innerHTML);
			if (!isNaN(tsktotVal))
				total += tsktotVal;
		}

		for (var a = 0; a < task.assignments.length; a++) {
			var assignment = task.assignments[a];
			// Build the id for this task total.
			var id = 'tsktot' + timesheetId + '_' + task.id +
				'_' + assignment.id;
			var tsktot = document.getElementById(id);
			if (tsktot) {
				var tsktotVal = parseFloat(tsktot.innerHTML);
				if (!isNaN(tsktotVal))
					total += tsktotVal;
			}
		}
	}

	// Make sure the correct number of decimals is displayed.
	total = Math.round(total * 20) / 20;
	var pieces = ("" + total).split(/\./);
	if (pieces.length == 1)
		total += ".00";
	else if (pieces[1].length == 1)
		total += "0";

	// Get the total element to update.
	var totalEl = document.getElementById('total' + timesheetId);

	// Set the new value.
	totalEl.innerHTML = total;
}

// Perform validation on a timesheet value.
function validateCell(cell) {
	// Make sure the cell value is numeric.
	if (isNaN(cell.value)) {
		Ext.Msg.show({
			title: "Invalid Value",
			msg: "You must enter a numeric value.",
			buttons: Ext.Msg.OK,
			icon: Ext.MessageBox.ERROR
		});
		cell.value = previousValue;
		return false;
	}

	// Make sure the cell value is numeric.
	if (parseFloat(cell.value) > 24) {
		Ext.Msg.show({
			title: "Invalid Value",
			msg: "The maximum number of hours for one day is 24.",
			buttons: Ext.Msg.OK,
			icon: Ext.MessageBox.ERROR
		});
		cell.value = previousValue;
		return false;
	}

	// Everything seems to look good.
	return true;
}

// Parse the timesheet id from the provided cell id.
function getTimesheetIdFromCellId(cellId) {
	// Cut the 'cell' from the front.
	timesheetId = cellId.substring(4);

	// Find the underscore character.
	var us = timesheetId.indexOf('_');

	// Use only up to the underscore.
	if (us > 0)
		return timesheetId.substring(0, us);

	// Could not find it.
	return undefined;
}

// Parse the task id from the provided cell id.
function getTaskIdFromCellId(cellId) {
	// Find the underscore character.
	var us = cellId.indexOf('_');

	// Make sure we found it.
	if (us < 0)
		return undefined;

	// Start after the underscore.
	taskId = cellId.substring(us + 1);

	// Find the next underscore character.
	var us = taskId.indexOf('_');

	// Use only up to the underscore.
	if (us > 0)
		return taskId.substring(0, us);

	// Could not find it.
	return undefined;
}

// Parse the assignment id from the provided cell id.
function getAssignmentIdFromCellId(cellId) {
	// Find the underscore character.
	var us = cellId.indexOf('_');

	// Make sure we found it.
	if (us < 0)
		return undefined;

	// Find the next underscore character.
	us = cellId.indexOf('_', us + 1);

	// Make sure we found it.
	if (us < 0)
		return undefined;

	// Start after the underscore.
	assignmentId = cellId.substring(us + 1);

	// Find the next underscore character.
	us = assignmentId.indexOf('_');

	// Make sure we found it.
	if (us < 0)
		return assignmentId;

	// Use only up to the underscore.
	return assignmentId.substring(0, us);
}

// Parse the day from the provided cell id.
function getDayFromCellId(cellId) {
	// Find the underscore.
	var us = cellId.indexOf('_');

	// Make sure we found it.
	if (us < 0)
		return undefined;

	// Find the next underscore.
	us = cellId.indexOf('_', us + 1);

	// Make sure we found it.
	if (us < 0)
		return undefined;

	// Find the next underscore.
	us = cellId.indexOf('_', us + 1);

	// Return everything after the underscore.
	if (us > 0)
		return cellId.substring(us + 1);

	// Could not find it.
	return undefined;
}

// Check to see if the timesheet needs to be saved before navigating away from
// the page.
function checkSave() {
	// Check to see if the timesheet data needs to be saved.
	var complain = false;
	for (i in needsSaved)
		if (needsSaved[i])
			complain = true;

	if (!complain)
		return undefined;

	// Hide any open message boxes.
	Ext.Msg.hide();

	// Return an error message.
	return 'Your timesheet contains unsaved information, and leaving this ' +
		   'page without saving the timesheet will cause that information ' +
		   'to be lost.';
}

