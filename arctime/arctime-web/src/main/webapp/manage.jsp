<%
	response.setStatus(response.SC_MOVED_PERMANENTLY);
	if (request.isUserInRole("PAYROLL"))
		response.setHeader("Location", "/payroll/");
	else if (request.isUserInRole("FINANCE"))
		response.setHeader("Location", "/finance/");
	else if (request.isUserInRole("SUPERVISOR"))
		response.setHeader("Location", "/supervisor/");
	else if (request.isUserInRole("MANAGER"))
		response.setHeader("Location", "/manager/");
	else
		response.setHeader("Location", "/user/");
%>
