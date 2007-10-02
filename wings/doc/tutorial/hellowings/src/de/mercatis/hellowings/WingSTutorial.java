package de.mercatis.hellowings;

import org.wings.SLabel;
import org.wings.STemplateLayout;
import org.wings.SPanel;
import org.wings.SButton;
import org.wings.SForm;
import org.wings.SCardLayout;
import org.wings.STabbedPane;
import org.wings.SFrame;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.File;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Properties;

/**
 * HelloWingsSessionServlet.java
 *
 *
 * Created: Wed Feb 20 17:01:45 2002
 *
 * @author Joachim Karrer
 * @version $Revision$
 */
public class WingSTutorial {


  final protected STabbedPane tab = new STabbedPane ();
  final protected SButton prevpage = new SButton ( "previous step" );
  final protected SButton nextpage = new SButton ( "next step" );

  protected SFrame frame = null;

  private long configFileDate = 0;

  private String configFileName = null;

  public WingSTutorial ( ) {
    super ( );
    System.out.println("new WingSetSession");
    initGUI ();
  }

  private void initGUI ( ) {
    frame = new SFrame ( );
    ServletContext sctx = frame.getSession().getServletContext();

    // String imagefile = sctx.getInitParameter ( "slides.bgimage" );
    String imagefile = 
	(String)frame.getSession().getProperty ( "slides.bgimage" );
    log ( "slides.bgimage= "+imagefile);
    String mainimagefile =
      sctx.getRealPath( imagefile );
    log ( "Template file is '"+mainimagefile+"'");
    try {
	frame.setBackgroundImage ( new org.wings.FileImageIcon( mainimagefile ) );
    } catch ( Exception  ex ) {
	log ( "seems we don't have a background image (Error: '"+
	      ex.getMessage()+"')");
    }

    // String templatefile = sctx.getInitParameter ( "slides.template" );
    String templatefile = (String)frame.getSession().getProperty ( "slides.template" );
    log ( "slides.template= "+templatefile);
    String mainframetemplatefile =
      sctx.getRealPath( templatefile );
    log ( "Template file is '"+mainframetemplatefile+"'");

    // String slidesconfig = sctx.getInitParameter ( "slides.config" );
    String slidesconfig = (String)frame.getSession().getProperty ( "slides.config" );
    String slidesconfigfile =
      sctx.getRealPath( slidesconfig );
    log ( "Slides configuration file is '"+slidesconfigfile+"'");
    configFileName = slidesconfigfile;

    SPanel p = new SPanel (  );
    try {
      STemplateLayout framelayout = 
	new STemplateLayout ( mainframetemplatefile );
      p.setLayout (framelayout);
      log ( "Template '"+mainframetemplatefile+"' loaded" );
    } catch ( IOException ioex ) {
      log ( "Template couldn't be loaded", ioex );
    }

    p.setBackground ( new java.awt.Color (0x2020FF) );

    SPanel menu = new SPanel (  );
    
    p.add ( menu, "menupanel" );

    nextpage.addActionListener ( 
				new ActionListener () {
				    public void actionPerformed ( ActionEvent ae ) {
				      if ( tab.getSelectedIndex()+1 < tab.getTabCount() )
					tab.setSelectedIndex ( tab.getSelectedIndex()+1 );
				    }
				  } );
    prevpage.addActionListener ( 
				new ActionListener () {
				    public void actionPerformed ( ActionEvent ae ) {
				      if ( tab.getSelectedIndex()-1 >= 0 )
					tab.setSelectedIndex ( tab.getSelectedIndex()-1 );
				    }
				  } 
				);
	
    initSlides( );

    p.add ( tab, "textpanel" );
    p.add ( nextpage, "nextstep" );
    p.add ( prevpage, "prevstep" );

    frame.getContentPane().add ( p  );
    frame.setBackground ( new java.awt.Color ( 192, 192, 255 ) );
    frame.setTitle ("WingS Tutorial" );
    frame.show();
  }

  private void initSlides ( ) {
    log ( "InitSlides start" );
        
    Properties slides = new Properties ();
    try {
      File configFile = new File ( configFileName );
      slides.load ( new java.io.FileInputStream( configFile ));
      configFileDate = configFile.lastModified();
      log ( "ConfigFileDate is :"+configFileDate );
    } catch ( IOException ioex ) {
      log ( "Unable to load '"+configFileName+"'", ioex );
    }
    log ( "Removing tabs" );
    // tab.removeAll ();

    int i = 1;
    String heading = null;
    while ( ( heading = slides.getProperty ( "slide."+i+".heading" ) ) != null ) {
      String template = slides.getProperty ( "slide."+i+".filename" );
      if ( template == null ) 
	log ( "TemplateFile '"+heading+"' is null" );
      else {
	File templ =
	  new File ( frame.getSession().getServletContext().getRealPath ( template ) );
	try {
	  i++;
	  log ( "trying to add '"+templ.getAbsolutePath()+"' to tabbedpane");
	  STemplateLayout textlayout = new STemplateLayout ( templ );
	  SPanel textt = new SPanel (  );
	  textt.setLayout ( textlayout );
	  textt.add ( nextpage, "nextstep" );
	  textt.add ( prevpage, "prevstep" );
	  tab.add ( heading , textt );
	} catch ( IOException ioex ) {
	  log ( "Unable to add '"+templ.getAbsolutePath()+"' to tabbedpane", ioex );
	}
      }
    }

    tab.setSelectedIndex ( 0 );
    log ( "InitSlides end" );

  }    

  private void checkConfig () {
    log ( "Checking config" );
    try {
      File f = new File ( configFileName );
      //	    log ( "ConfigFileDate is :"+configFileDate );
      //	    log ( "configFile date is :"+f.lastModified() );
      if ( f.lastModified()> configFileDate ) {
	log ( "new config!" );
	configFileDate = f.lastModified();
	initSlides();
      }
    } catch ( Exception exp ) {
      log ( "Exception in checkConfig()", exp );
    }
  }

  public void log ( String str ) {
    System.out.println ( str );
    frame.getSession().getServletContext().log ( str );
  }

  public void log ( String str, Exception exp ) {
    System.out.println ( str );
    exp.printStackTrace();
    frame.getSession().getServletContext().log ( str, exp );
  }
}// WingSTutorial




