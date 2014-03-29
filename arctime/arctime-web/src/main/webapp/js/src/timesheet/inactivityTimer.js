
// The number of seconds to allow inactivity.
var INACTIVE_SECONDS = 15 * 60; // 15 minutes.

// The remaining number of seconds.
var secs = INACTIVE_SECONDS;

// The timer identifier.
var timerId = undefined;


// Used to start the timer.
function startTimer() {
	if (timerId) {
		// Just update the counter.
		resetTimer();
	} else {
		// Do the inactivity update.
		updateInactivity();
	}
}

// Reset the timer.
function resetTimer() {
	// Clear the inactive seconds.
	secs = INACTIVE_SECONDS;
}

// Update the display to show the current inactivity count-down.
function updateInactivity() {
	// Retrieve the inactivity document element.
	var inactivity = document.getElementById("inactivity");

	// Make sure we found it.
	if (!inactivity)
		return;

	// This will hold the time to display.
	var time;

	// Check to see if the timer has run out.
	if (secs == 0) {
		// Log out in 2 seconds.
		self.setTimeout("inactivityTimeout()", 2000);

		// This is the message we will display.
		time = 'Automatically logging out...';
	} else {
		// Calculate the minutes and seconds.
		var mins = Math.floor(secs / 60);
		var seconds = secs % 60;

		// This is the time that will be displayed.
		time = mins + ':' + (seconds < 10 ? '0' : '') + seconds;
	}

	// Determine the color to use when displaying the timeout.
	var color = (secs < 60) ? "red" : "green";

	// Build the HTML display.
	inactivity.innerHTML = 'Inactivity Timeout: <font color="' + color + '">'
		+ time + '</font>';

	// If there are more seconds left, reset the timer.
	if (secs > 0)
		timerId = self.setTimeout("updateInactivity()", 1000);

	// Decrement the second counter.
	secs--;
}

// Invoked when the inactivity timeout happens.
function inactivityTimeout() {
	// Go to the logout page.
	document.location = "/rest/logout";
}


