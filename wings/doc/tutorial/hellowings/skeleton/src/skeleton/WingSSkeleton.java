package skeleton;
 
import org.wings.SContainer;
import org.wings.SLabel;
import org.wings.SFrame;

import javax.servlet.ServletContext;

/**
 * <h1>WingSSkeletonSession.java</h1>
 * <p>A very simple <code>SessionServlet</code>.</p>
 * <p><b>Created 2002, mercatis GmbH</b></p>
 **/
public class WingSSkeleton {

  /**
   * <p>creates the GUI of the webapplication</p>
   **/
  public WingSSkeleton (  ) {
    // call super-constructor, not necessary, but if ever, it's here
    super ( );
    initGUI();
  }

  /**
   * <p>initializes the GUI</p>
   **/
  private void initGUI () {
    // create a Frame
    SFrame frame = new SFrame ();

    // accessing the servletContext
    ServletContext sctx = frame.getSession().getServletContext();

    // get the pane to put components in
    SContainer pane = frame.getContentPane();

    // create and add a label to the pane
    SLabel label = new SLabel ( "Add as you like..." );
    pane.addComponent( label );

    // tell the WingServlet that we have something to show
    frame.show();

    // note: no frame.setSize(..) needed
  }
    
}// WingSSkeleton






