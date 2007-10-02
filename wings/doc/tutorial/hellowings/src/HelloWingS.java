
import org.wings.session.Session;
import org.wings.SContainer;
import org.wings.SLabel;
import org.wings.SFrame;

/**
 * <h1>HelloWingS.java</h1>
 * <p>A very simple <em>WingS application</em>.</p>
 * <p>Only prints out "<code>Hello WingS!</code>".</p>
 * <p><b>Created 2002, mercatis GmbH</b></p>
 **/
public class HelloWingS
{
    /**
     * <p>Constructor initializes the GUI</p>
     **/
    public HelloWingS() {
	// create a frame
	SFrame frame = new SFrame("Hello WingS");

	// get the content pane to put components in
	SContainer pane = frame.getContentPane();

	// create and add a label to the pane
	SLabel label = new SLabel ( "Hello WingS!" );
	pane.addComponent( label );

	// note: no frame.pack() or frame.setSize(..) needed
	frame.show();
    }
    
}// HelloWingS
