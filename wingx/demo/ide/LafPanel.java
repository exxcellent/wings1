package ide;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringBufferInputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Logger;

import org.wings.DynamicCodeResource;
import org.wings.LowLevelEventListener;
import org.wings.SBorderLayout;
import org.wings.SButton;
import org.wings.SButtonGroup;
import org.wings.SComponent;
import org.wings.SFileChooser;
import org.wings.SForm;
import org.wings.SFrame;
import org.wings.SGridLayout;
import org.wings.SLabel;
import org.wings.SPanel;
import org.wings.SRadioButton;
import org.wings.STextArea;
import org.wings.STextField;
import org.wings.plaf.ComponentCG;
import org.wings.script.JavaScriptListener;
import org.wings.script.ScriptListener;
import org.wings.session.PropertyService;
import sun.security.krb5.internal.i;
import sun.security.krb5.internal.crypto.e;

public class LafPanel
    extends SForm
{
    private static String EDITOR_SOURCE = "editor";
    private static String FILE_SOURCE = "file";
    private static String NAME_SOURCE = "name";
	protected final Logger logger = Logger.getLogger("ide.LafPanel");
    Map modules;

    STextArea editor;
    STextField name;
    SFileChooser chooser;
    TestFrame testFrame;
    Class componentClass;
    SComponent component;
    Properties props;

    public LafPanel(Map modules) {
	super(new SBorderLayout());
	this.modules = modules;
	String source = (String)((PropertyService)getSession()).getProperty("plaf.source");
	if (source == null || source.length() == 0 || "editorfilename".indexOf(source) == -1)
	    source = "editor";

	setEncodingType("multipart/form-data");

	SLabel sourceFrom = new SLabel("source from ..");
	final SRadioButton editorSource = new SRadioButton("editor");
	editorSource.setSelected(EDITOR_SOURCE.equals(source));
	final SRadioButton fileSource = new SRadioButton("file");
	fileSource.setSelected(FILE_SOURCE.equals(source));
	final SRadioButton nameSource = new SRadioButton("name");
	nameSource.setSelected(NAME_SOURCE.equals(source));

        SButtonGroup group = new SButtonGroup();
        group.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
		    editor.setVisible(editorSource.isSelected());
		    chooser.setVisible(fileSource.isSelected());
		    name.setVisible(nameSource.isSelected());
                }
            });
        group.add(editorSource);
        group.add(fileSource);
        group.add(nameSource);

	SButton apply = new SButton("apply");

	SPanel north = new SPanel();
        north.add(sourceFrom);
        north.add(editorSource);
        north.add(fileSource);
        north.add(nameSource);
        north.add(apply);
	add(north, "North");

	name = new STextField();
	name.setColumns(80);
	name.setVisible(NAME_SOURCE.equals(source));

	editor = new STextArea();
	editor.setColumns(80);
	editor.setRows(24);
	editor.setVisible(EDITOR_SOURCE.equals(source));

	chooser = new SFileChooser();
	chooser.setFileNameFilter("*.plaf");
	chooser.setVisible(FILE_SOURCE.equals(source));

	SPanel center = new SPanel(new SGridLayout(1));
	center.add(editor);
	center.add(chooser);
	center.add(name);
	add(center, "Center");


	SButton compile = new SButton("compile");
	compile.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent event) {
		    System.out.println("compile");
		    if (compile())
			test();
		}
	    });
	add(compile, "South");
    }

    public boolean compile() {
	ByteArrayOutputStream log = new ByteArrayOutputStream();
	try {
	    if (((PropertyService)getSession()).getProperty("build.dir") == null)
		((PropertyService)getSession()).setProperty("build.dir", "/tmp/ide");

	    // make sure we have a build directory
	    File buildDir = new File((String)((PropertyService)getSession()).getProperty("build.dir"));
	    if (!buildDir.exists())
		buildDir.mkdirs();

	    // make sure, the build.xml file is in place
	    File buildFile = new File(buildDir, "build.xml");
	    if (!buildFile.exists()) {
		InputStream in = getSession().getServletContext().getResourceAsStream("/cg-build.xml");
		OutputStream out = new FileOutputStream(buildFile);
		new StreamPumper(in, out).run();
	    }

	    // build tree
	    Build build = new Build(buildFile);
	    build.executeTarget("prepare");

	    // make sure, all necessary libraries are in place
	    File wingsJar = new File(buildDir, "lib/wings.jar");
	    if (!wingsJar.exists()) {
		InputStream in = getSession().getServletContext().getResourceAsStream("/WEB-INF/lib/wings.jar");
		OutputStream out = new FileOutputStream(wingsJar);
		new StreamPumper(in, out).run();
	    }

	    PrintStream out = new PrintStream(log);
	    build.getLogger().setOutputPrintStream(out);
	    build.getLogger().setErrorPrintStream(out);

	    // cg template
	    props = null;
	    File file = null;
	    if (chooser.isVisible()) {
		file = chooser.getFile();
		if (file == null) {
		    System.err.println("no file uploaded");
		}
		props = scanPlaf(new FileInputStream(file));
		File to = new File(buildDir, "build/cg/" + props.getProperty("name") + ".plaf");
		if (to.exists())
		    to.delete();
		file.renameTo(to);
	    }
	    else if (editor.isVisible()) {
		props = scanPlaf(new StringBufferInputStream(editor.getText()));
		file = new File(buildDir, "build/cg/" + props.getProperty("name") + ".plaf");
		PrintWriter writer = new PrintWriter(new FileWriter(file));
		writer.write(editor.getText());
		writer.flush();
	    }
	    else if (name.isVisible()) {
		file = new File(buildDir, "build/cg/" + name.getText() + ".plaf");
		props = scanPlaf(new FileInputStream(file));
	    }
	    System.err.println("file: " + file.getCanonicalPath());

	    selectComponent(props.getProperty("for"));

	    Vector targets = new Vector(2);
	    targets.add("java");
	    targets.add("class");
	    targets.add("plaf");
	    build.executeTargets(targets);
	}
	catch (Exception e) {
	    LogPanel logPanel = (LogPanel)modules.get("log");
	    logPanel.appendText(e.getMessage());
	    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
	    e.printStackTrace(new PrintStream(buffer));
	    logPanel.appendText(log + "\n" + buffer);
	    return false;
	}
	finally {
	    System.out.println("LOG");
	    System.out.println("" + log);
	    LogPanel logPanel = (LogPanel)modules.get("log");
	    logPanel.clearText();
	    logPanel.appendText("" + log);
	}
	return true;
    }

    public Properties scanPlaf(InputStream in) {
	Properties props = new Properties();
	try {
	    String forValue = null;
	    String nameValue = null;

	    LineNumberReader reader = new LineNumberReader(new InputStreamReader(in));
	    String line = reader.readLine();
	    int pos;

	    boolean done = false;
	    while (line != null && !done) {
		if (line.indexOf("<template") >= 0)
		    done = true;
		else
		    line = reader.readLine();
	    }

	    done = false;
	    while (line != null && !done) {
		if (nameValue == null && (pos = line.indexOf("name")) >= 0) {
		    pos = line.indexOf("=", pos + 4);
		    pos = line.indexOf("\"", pos + 1);
		    int end = line.indexOf("\"", pos + 1);
		    nameValue = line.substring(pos + 1, end);
		}
		if (forValue == null && (pos = line.indexOf("for")) >= 0) {
		    pos = line.indexOf("=", pos + 3);
		    pos = line.indexOf("\"", pos + 1);
		    int end = line.indexOf("\"", pos + 1);
		    forValue = line.substring(pos + 1, end);
		}
		if (nameValue != null && forValue != null)
		    done = true;
		else
		    line = reader.readLine();
	    }

	    props.setProperty("name", nameValue);
	    props.setProperty("for", forValue);
	}
	catch (Exception e) {
	    System.err.println(e.getMessage());
	    e.printStackTrace(System.err);
	}
	return props;
    }

    private void selectComponent(String name) {
	try {
	    componentClass = Class.forName(name);
	}
	catch (ClassNotFoundException e) {
	    System.err.println(e.getMessage());
	}
    }

    public void test() {
	if (((PropertyService)getSession()).getProperty("build.dir") == null)
	    ((PropertyService)getSession()).setProperty("build.dir", "/tmp/ide");

	SPanel builderPanel = (SPanel)modules.get("builder");
	PropertyPanel propertyPanel = (PropertyPanel)modules.get("properties");

	try {
	    if (component == null || !component.getClass().equals(componentClass))
		component = (SComponent)componentClass.newInstance();
	    showTestFrame();
	    testFrame.setComponent(component);

	    String url = "file:" + (String)((PropertyService)getSession()).getProperty("build.dir")
		+ "/dist/plaf/" + "plaf" + "-plaf.jar";
	    System.out.println("url: " + url);
	    ClassLoader loader = new URLClassLoader(new URL[] { new URL(url) },
						    getClass().getClassLoader());
	    Class cgClass = loader.loadClass("plaf." + props.getProperty("name"));
	    ComponentCG cg = (ComponentCG)cgClass.newInstance();
	    
	    Method cgSetter = findCGSetter(componentClass);
	    cgSetter.invoke(component, new Object[] { cg });
	    propertyPanel.setComponent(component);
	}
	catch (Exception e) {
	    LogPanel logPanel = (LogPanel)modules.get("log");
	    logPanel.appendText(e.getMessage());
	    ByteArrayOutputStream log = new ByteArrayOutputStream();
	    e.printStackTrace(new PrintStream(log));
	    logPanel.appendText("" + log);
	}
    }

    public static Method findCGSetter(Class clazz)
	throws Exception
    {
	Method[] methods = clazz.getMethods();
	for (int i=0; i < methods.length; i++) {
	    if ("setCG".equals(methods[i].getName()) &&
		!methods[i].getParameterTypes()[0].equals(ComponentCG.class))
		return methods[i];
	}
	return null;
    }

    TestFrame showTestFrame() {
	final SFrame frame = getParentFrame();

	boolean firstTime = false;
	if (testFrame == null) {
	    testFrame = new TestFrame("test", modules);
	    testFrame.setRequestURL(frame.getRequestURL());
	    DynamicCodeResource codeResource = new DynamicCodeResource(testFrame);
	    testFrame.addDynamicResource(codeResource);

	    testFrame.setTargetResource(codeResource.getId());

	    PropertyPanel propertyPanel = (PropertyPanel)modules.get("properties");
	    // eigentlich sollte man einen property change listener bei der test component
	    // registrieren!
	    propertyPanel.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent event) {
			test();
		    }
		});
	    firstTime = true;
	}

	DynamicCodeResource codeResource = (DynamicCodeResource)testFrame.getDynamicResource(DynamicCodeResource.class);
	String url = codeResource.getURL().toString();
	if (url.indexOf("?") > -1)
	    url = url + "&clear=X";
	else
	    url = url + "?clear=X";
	logger.fine("TEST FRAME URL: " + url);

	final ScriptListener script = new JavaScriptListener("onload", "test=window.open('" + url + "', 'test','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes,width=400,height=300,left=20,top=20');test.focus()");
	frame.addScriptListener(script);

	if (firstTime) {
	    // register a request listener, that handles the named event "clear"
	    getSession().getDispatcher().register(new LowLevelEventListener() {
		    public void processLowLevelEvent(String name, String[] values) {
			logger.info("remove java script");
			frame.removeScriptListener(script);
		    }
		    
		    public String getLowLevelEventId() {
            		return getComponentId();
		    }
		    public final String getEncodedLowLevelEventId() {
		        return getEncodedLowLevelEventId();
		    }


		    public String getName() { return "clear"; }
		    public String getNamePrefix() { return ""; }
		    public void fireIntermediateEvents() {}
		    public void fireFinalEvents() {}
		    
		});
	}
	return testFrame;
    }
}
