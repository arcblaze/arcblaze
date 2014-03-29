<!DOCTYPE html>
<html>
  <head>
    <title>ArcTime: Companies</title>
    <%@ include file="/ssi/meta.jspf" %>
  </head>
  <body>
    <%@ include file="/ssi/header.jspf" %>
    <%@ include file="/ssi/user.jspf" %>
    <%@ include file="/ssi/scripts.jspf" %>

    <div id="container-maxwidth">
	  <div id="company-management-grid"></div>
	  <div id="company-add-panel"></div>
	  <div id="company-update-panel"></div>

	  <script src="/js/src/action/admin/company/DoCompanyActivate.js"></script>
	  <script src="/js/src/action/admin/company/DoCompanyAdd.js"></script>
	  <script src="/js/src/action/admin/company/DoCompanyDeactivate.js"></script>
	  <script src="/js/src/action/admin/company/DoCompanyDelete.js"></script>
	  <script src="/js/src/action/admin/company/DoCompanySearch.js"></script>
	  <script src="/js/src/action/admin/company/DoCompanyUpdate.js"></script>
	  <script src="/js/src/action/admin/company/ShowCompanyAdd.js"></script>
	  <script src="/js/src/action/admin/company/ShowCompanyGrid.js"></script>
	  <script src="/js/src/action/admin/company/ShowCompanyUpdate.js"></script>
	  <script src="/js/src/data/model/Company.js"></script>
	  <script src="/js/src/data/store/admin/CompanyStore.js"></script>
	  <script src="/js/src/ui/grid/admin/CompanyGrid.js"></script>
	  <script src="/js/src/ui/panel/admin/company/CompanyAddPanel.js"></script>
	  <script src="/js/src/ui/panel/admin/company/CompanyUpdatePanel.js"></script>
	  <script src="/js/src/ui/tbar/admin/CompanyToolbar.js"></script>
	  <script src="/js/src/util/io/ServerIO.js"></script>
	  <script>
		Ext.onReady(function() {
			new ui.grid.admin.CompanyGrid({
				renderTo: 'company-management-grid'
			});
		});
	  </script>
    </div>

    <%@ include file="/ssi/footer.jspf" %>
  </body>
</html>

