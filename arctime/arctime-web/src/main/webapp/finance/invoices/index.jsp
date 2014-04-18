<!DOCTYPE html>
<html>
  <head>
    <title>ArcTime: Invoices</title>
    <%@ include file="/ssi/meta.jspf" %>
  </head>
  <body>
    <%@ include file="/ssi/header.jspf" %>
    <%@ include file="/ssi/user.jspf" %>
    <%@ include file="/ssi/scripts.jspf" %>

    <div id="container-maxwidth">
      <%@ include file="/ssi/manager_navigation.jspf" %>

      <!-- This is where the user grid and panels will be rendered. -->
      <div id="transaction-grid"></div>

      <!-- Add the employee management scripts. -->
      <script src="/js/src/util/io/ServerIO.js"></script>
      <script src="/js/src/action/finance/transaction/DoTransactionSearch.js"></script>
      <script src="/js/src/data/model/Transaction.js"></script>
      <script src="/js/src/data/store/finance/TransactionStore.js"></script>
      <script src="/js/src/ui/grid/finance/TransactionGrid.js"></script>
      <script src="/js/src/ui/tbar/finance/TransactionToolbar.js"></script>
      <script>
        // Invoked when the page is ready.
        Ext.onReady(function() {
            // Create the grid.
	        new ui.grid.finance.TransactionGrid({
                // Specify where the grid will be rendered.
                renderTo: 'transaction-grid'
            });
        });
      </script>

    </div>

    <%@ include file="/ssi/footer.jspf" %>
  </body>
</html>
