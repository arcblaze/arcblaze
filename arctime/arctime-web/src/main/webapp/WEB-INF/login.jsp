<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<% String redirUri = String.valueOf(request.getAttribute("javax.servlet.forward.request_uri")); %>
<!DOCTYPE html>
<html>
  <head>
    <%@ include file="/ssi/meta.jspf" %>
  </head>
  <body>
    <%@ include file="/ssi/header.jspf" %>
    <%@ include file="/ssi/scripts.jspf" %>

    <div id="container">
      <table id="login-table">
        <tr>
          <td valign="top">
            <!-- TODO: Display failed login error here. -->
    
            <div id="login-form"></div>
          </td>
        </tr>
      </table>
    
      <script src="/js/src/action/login/DoLogin.js"></script>
      <script src="/js/src/action/login/ResetPassword.js"></script>
      <script src="/js/src/util/io/ServerIO.js"></script>
      <script src="/js/src/ui/panel/login/LoginPanel.js"></script>
      <script>
        Ext.onReady(function() {
            // Create the login panel.
            var loginPanel = new ui.panel.login.LoginPanel({
                redirectUri: '<%= StringEscapeUtils.escapeJavaScript(redirUri) %>'
            });
            loginPanel.render('login-form');
            loginPanel.setInitialFocus();
        });
      </script>
    </div>

    <%@ include file="/ssi/footer.jspf" %>
  </body>
</html>
