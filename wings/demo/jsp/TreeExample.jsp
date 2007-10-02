<%@ page language="java"
import="wingset.*,org.wings.*,org.wings.style.*,org.wings.servlet.*,org.wings.externalizer.*,org.wings.io.*,org.wings.session.*,java.awt.*,java.io.*,javax.swing.tree.*"
%>

<%!
	 
    boolean initialized = false;

    WingServlet servlet = new WingServlet() {
        protected void initExternalizer(ServletConfig config) {
          // we want to use the servlet externalizer
          getExternalizeManager().setExternalizer(new ServletExternalizer("ExternalizerServlet"));
        }
        
        public SessionServlet generateSessionServlet(HttpServletRequest req)
          throws Exception
        {
          // create new default session and set plaf
          DefaultSession session = new DefaultSession();
          session.getCGManager().setLookAndFeel(new org.wings.plaf.xhtml.css1.CSS1LookAndFeel());
          
          // return a new wingset session
          return new SessionServlet(session, false) {};
        }
      };

%>
<%

  if ( !initialized ) {
    servlet.init(config);
    initialized = true;
  } 

  servlet.doPost(request, response);

  SFrame frame = servlet.getSessionServlet(request).getFrame();
%>


<html><head>
<%

  // Style Sheets werden vom Frame verwaltet, also hier von Hand...

  StyleSheet styleSheet = frame.getStyleSheet();

  if (styleSheet != null) {
    ExternalizeManager ext = frame.getExternalizeManager();
    String link = null;

    if (ext != null) {
      try {
        link = ext.externalize(styleSheet);
      } 
      catch (java.io.IOException e) {
        // dann eben nicht !!
        e.printStackTrace(System.err);
      }
    }
  
    if (link != null) {
      out.write("<link rel=\"stylesheet\" type=\"text/css\" href=\"");
      out.write(link);
      out.write("\" />");
    }
  }
%>

<title>Test Swinglets Tree Page</title></head>
<body bgcolor="#f0f0f0">

<%
  TreeExample tree = (TreeExample)session.getValue("TreeExample"); 
  if ( tree == null ) {
    tree = new TreeExample();
    frame.getContentPane().add(tree);
    session.putValue("TreeExample", tree);
  }
  
  StringBufferDevice outdev = new StringBufferDevice();
  tree.write(outdev);

  out.print(outdev);
%>

</body>
</html>
