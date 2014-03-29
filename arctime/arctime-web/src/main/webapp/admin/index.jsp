<!DOCTYPE html>
<html>
  <head>
    <title>ArcTime: Admin</title>
    <%@ include file="/ssi/meta.jspf" %>
  </head>
  <body>
    <%@ include file="/ssi/header.jspf" %>
    <%@ include file="/ssi/user.jspf" %>
    <%@ include file="/ssi/scripts.jspf" %>

    <div id="container-maxwidth">
      <!-- This is where the UI components will be injected into the page. -->
      <div id="system-stats"></div>

      <script src="http://d3js.org/d3.v3.min.js" charset="utf-8"></script>
      <script src="http://labratrevenge.com/d3-tip/javascripts/d3.tip.min.js"></script>
      <script src="/js/guage.js"></script>

      <script src="/js/src/data/model/Stat.js"></script>
      <script src="/js/src/data/store/admin/SystemStatsStore.js"></script>
      <script src="/js/src/util/io/ServerIO.js"></script>
      <script src="/js/src/ui/panel/admin/stats/ActiveCompanyChartPanel.js"></script>
      <script src="/js/src/ui/panel/admin/stats/ActiveUserChartPanel.js"></script>
      <script src="/js/src/ui/panel/admin/stats/MemoryUsagePanel.js"></script>
      <script src="/js/src/ui/panel/admin/stats/MonthlyRevenueChartPanel.js"></script>
      <script src="/js/src/ui/panel/admin/stats/StatsPanel.js"></script>
      <script src="/js/src/ui/panel/admin/stats/StatusCodeChartPanel.js"></script>
      <script src="/js/src/ui/panel/admin/stats/SystemStatsPanel.js"></script>
      <script>
        Ext.onReady(function() {
            var statsPanel = new ui.panel.admin.stats.StatsPanel();
            statsPanel.render('system-stats');
        });
      </script>

    </div>

    <%@ include file="/ssi/footer.jspf" %>
  </body>
</html>
