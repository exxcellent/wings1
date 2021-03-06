/*
 * $Id$
 * (c) Copyright 2000 wingS development team.
 *
 * This file is part of the wingS demo (http://j-wings.org).
 *
 * The wingS demo is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * Please see COPYING for the complete licence.
 */

package wingset;



import java.io.FileWriter;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Timer;
import java.util.TimerTask;
import org.wings.ClasspathResource;
import org.wings.SBorderLayout;
import org.wings.SContainer;
import org.wings.SFrame;
import org.wings.SIcon;
import org.wings.SLabel;
import org.wings.SResourceIcon;
import org.wings.SRootLayout;
import org.wings.STabbedPane;
import org.wings.header.Link;
import org.wings.session.SessionManager;
import org.wings.session.WingsStatistics;
import org.wings.util.TimeMeasure;

/**
 * TODO: documentation
 *
 * @author <a href="mailto:haaf@mercatis.de">Armin Haaf</a>
 * @version $Revision$
 */
public class WingSet
{
    static final boolean SHOW_STATISTICS = false;

    static final ClassLoader cl = WingSet.class.getClassLoader();
    private final static SIcon brushedMetal = 
        new SResourceIcon(cl, "wingset/icons/brushedMetal.gif");

    private final static SIcon JAVA_CUP_ICON = 
        new SResourceIcon("org/wings/icons/JavaCup.gif");

    private final static SIcon SMALL_COW_ICON = 
        new SResourceIcon("wingset/icons/cowSmall.gif");


    static final long birthday = System.currentTimeMillis();
    
    static FileWriter infoWriter;

    static final Timer timer = new Timer();

    static long oldRequestCount = 0;
    static long oldSessionCount = 0;

    static final TimerTask infoTask = new TimerTask() {
            public void run() {
                StringBuffer result = new StringBuffer();
                long totalmem = Runtime.getRuntime().totalMemory();
                long freemem = Runtime.getRuntime().freeMemory();

                WingsStatistics stats = WingsStatistics.getStatistics();

                result.append(System.currentTimeMillis()).append(' ')
                    .append(stats.getUptime()).append(' ')
                    .append(stats.getOverallSessionCount()).append(' ')
                    .append(stats.getOverallSessionCount()-oldSessionCount).append(' ')
                    .append(stats.getActiveSessionCount()).append(' ')
                    .append(stats.getAllocatedSessionCount()).append(' ')
                    .append(stats.getRequestCount()).append(' ')
                    .append(stats.getRequestCount()-oldRequestCount).append(' ')
                    .append(totalmem).append(' ')
                    .append(freemem).append(' ')
                    .append(totalmem-freemem).append('\n');

                oldRequestCount = stats.getRequestCount();
                oldSessionCount = stats.getOverallSessionCount();

                try {
                    infoWriter.write(result.toString());
                    infoWriter.flush();
                } catch ( Exception ex) {
                    ex.printStackTrace();
                } // end of try-catch
                
            }
        };

    static {
        
        if ( SHOW_STATISTICS ) {

        try {
            infoWriter = new FileWriter("/tmp/wingsmemory", false);

            StringBuffer result = new StringBuffer();
            result.append("timestamp").append(' ')
                .append("uptime").append(' ')
                .append("overall_sessions").append(' ')
                .append("new_sessions").append(' ')
                .append("active_sessions").append(' ')
                .append("allocated_sessions").append(' ')
                .append("overall_processed requests").append(' ')
                .append("processed_requests").append(' ')
                .append("total_memory").append(' ')
                .append("free_memory").append(' ')
                .append("used_memory").append('\n');
            infoWriter.write(result.toString());
            infoWriter.flush();
        } catch ( Exception ex) {
            ex.printStackTrace();
        } // end of try-catch
            
        timer.scheduleAtFixedRate(infoTask,
                                  0,
                                  10*1000);
        }
    }



    private SFrame frame;
    /*
     * time measurement (a little hacky)
     */
    private SLabel timeMeasure;
    private final TimeMeasure stopWatch;

    public WingSet() {
        frame = new SFrame("WingSet");
        frame.setTitle("WingSet Demo");
        SessionManager.getSession().setCharacterEncoding("UTF-8");

        // import additional templates
        frame.addHeader(new Link("stylesheet", null, "text/css", null, new ClasspathResource("/wingset/css/myapp.css", "text/css")));
       
        System.out.println("new WingSet");
        stopWatch = new TimeMeasure(new MessageFormat("<html><b>{0}</b>: {1} (<i>x {2}</i>)<br/>"));

        timeMeasure = new SLabel();

        SContainer contentPane = frame.getContentPane();
        try {
            URL templateURL = getClass().getResource("/wingset/templates/ExampleFrame.thtml");
            if (templateURL != null) {
                SRootLayout layout = new SRootLayout(templateURL);
                frame.setLayout(layout);
            }
        }
        catch ( java.io.IOException except ) {
            except.printStackTrace();
        }
        
        //SForm form = new SForm();
        final STabbedPane tab = new STabbedPane();
        tab.setName("examples");
        // tab.setMaxTabsPerLine(9);
        tab.setTabPlacement(STabbedPane.TOP);
		//tab.setBackgroundImage(brushedMetal);

        tab.add(new WingsImage(), "wingS!");
        tab.add(new LabelExample(), "Label");
        tab.add(new BorderExample(), "Border");
        tab.add(new TextComponentExample(), "Text Component");
         // a Tab with icon..
        tab.addTab("Tree", JAVA_CUP_ICON, 
                   new TreeExample(), "Tree Tool Tip");
        tab.add(new OptionPaneExample(frame), "OptionPane");
        tab.add(new TableExample(), "Table");
        tab.add(new ListExample(), "List");
        tab.add(new ButtonExample(), "Button");
        tab.add(new ToggleButtonExample(), "ToggleButton");
        tab.add(new CheckBoxExample(), "CheckBox");
        tab.add(new RadioButtonExample(), "RadioButton");
        tab.add(new Faces(), "Faces");
        tab.add(new FileChooserExample(), "FileChooser");
        tab.add(new ScrollPaneExample(), "ScrollPane");
        tab.add(new PageScrollerExample(), "PageScroller");
        tab.add(new MenuExample(), "Menu");
        tab.add(new TabbedPaneExample(), "Tabbed Pane");
        //tab.add(new LayoutExample(), "Simple Layout");
        tab.addTab("Template Layout", 
                   SMALL_COW_ICON, 
                   new TemplateExample(), "Template Layout Manager");
        tab.add(new InteractiveTemplateExample(), "Interactive Template");
        tab.add(new ProgressBarExample(), "ProgressBar");
        tab.add(new DateChooserExample(), "DateChooser");
        tab.add(new MemUsageExample(), "Memory Usage");
        tab.add(new JavaScriptListenerExample(), "Script Listener");
        tab.add(new DesktopPaneExample(), "Desktop Pane");

        //tab.add(new DateChooserExample(), "DateChooser");
        //form.add(tab);
        // contentPane.add(form, "WingSetApp");
        contentPane.add(tab, SBorderLayout.CENTER);
        contentPane.add(timeMeasure, SBorderLayout.SOUTH);


        frame.show();

        /*  for testing purpose, to get information what events are requested

        final SLabel paramLabel = new SLabel();
        contentPane.add(paramLabel, "ParamLabel");

        SessionManager.getSession().addRequestListener(new SRequestListener() {
                public void processRequest(SRequestEvent e) {
                    if ( e.getType()==SRequestEvent.DISPATCH_START ) {
                        StringBuffer label = new StringBuffer();

                        HttpServletRequest req = 
                            SessionManager.getSession().getServletRequest();
                        
                        for (Enumeration en = req.getParameterNames(); en.hasMoreElements();) {
                            String paramName = (String)en.nextElement();
                            String[] value = req.getParameterValues(paramName);

                            label.append(paramName).append(": ").
                                append(value[0]).append("<br>");
                        }

                        paramLabel.setText(label.toString());
                    }
                }
            });
        */

    }    
}

/*
 * Local variables:
 * c-basic-offset: 4
 * indent-tabs-mode: nil
 * compile-command: "ant -emacs -find build.xml"
 * End:
 */
