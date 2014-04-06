<!DOCTYPE html>
<html>
  <head>
    <title>ArcTime: Contact Us</title>
    <%@ include file="/ssi/meta.jspf" %>
  </head>
  <body>
    <%@ include file="/ssi/header.jspf" %>
    <%@ include file="/ssi/user.jspf" %>
    <%@ include file="/ssi/scripts.jspf" %>


    <div id="container">
      <table id="contact-table">
        <tr>
          <td valign="top">
            <!-- This is where the contact-us form will be rendered. -->
            <div id="contact-form"></div>
          </td>
        </tr>
      </table>

      <!-- Add the profile management scripts. -->
      <script src="/js/src/util/io/ServerIO.js"></script>
      <script src="/js/src/action/contact/DoSendMessage.js"></script>
      <script src="/js/src/ui/panel/contact/ContactUsPanel.js"></script>
      <script>
        // Invoked when the page is ready.
        Ext.onReady(function() {
            // Create the panel.
            new ui.panel.contact.ContactUsPanel({
                // Specify where the panel will be rendered.
                renderTo: 'contact-form',

                // Provide the user data.
                user: { data: user }
            });
        });
      </script>
    </div>


    <%@ include file="/ssi/footer.jspf" %>
  </body>
</html>
