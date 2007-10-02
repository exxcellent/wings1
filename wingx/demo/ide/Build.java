package ide;

import java.io.*;
import java.util.*;

import org.apache.tools.ant.*;

public class Build
{
    Project project;
    BuildLogger logger;

    public Build(File buildFile) {
	project = new Project();
	project.addBuildListener(getLogger());
	project.setUserProperty("ant.file" , buildFile.getAbsolutePath());
	project.init();

	try {
	    Class.forName("javax.xml.parsers.SAXParserFactory");
	    ProjectHelper.configureProject(project, buildFile);
	} catch (NoClassDefFoundError ncdfe) {
	    throw new BuildException("no parser", ncdfe);
	} catch (ClassNotFoundException cnfe) {
	    throw new BuildException("no parser", cnfe);
	} catch (NullPointerException npe) {
	    throw new BuildException("no parser", npe);
	}
    }

    public void setProperty(String name, String value) {
	project.setUserProperty(name, value);
    }

    public void executeTargets(Vector targets)
	throws BuildException
    {
	PrintStream err = System.err;
	PrintStream out = System.out;

	Throwable error = null;
	try {
	    System.setOut(new PrintStream(new DemuxOutputStream(project, false)));
	    System.setErr(new PrintStream(new DemuxOutputStream(project, true)));

	    // make sure that we have a target to execute
	    if (targets.size() == 0) {
		targets.addElement(project.getDefaultTarget());
	    }

	    project.executeTargets(targets);
	}
        catch(RuntimeException e) {
            error = e;
            throw e;
        }
        catch(Error e) {
            error = e;
            throw e;
        }
	finally {
	    System.setOut(out);
	    System.setErr(err);
	}
    }


    public void executeTarget(String target)
	throws BuildException
    {
	PrintStream err = System.err;
	PrintStream out = System.out;

	Throwable error = null;
	try {
	    System.setOut(new PrintStream(new DemuxOutputStream(project, false)));
	    System.setErr(new PrintStream(new DemuxOutputStream(project, true)));

	    project.executeTarget(target);
	}
        catch(RuntimeException e) {
            error = e;
            throw e;
        }
        catch (Error e) {
            error = e;
            throw e;
        }
	finally {
	    System.setOut(out);
	    System.setErr(err);
	}
    }

    public BuildLogger getLogger() {
	if (logger == null)
	    logger = new DefaultLogger();
        return logger;
    }

    public static void main(String args[]) {
	String buildFile = args[0];
	Build build = new Build(new File(buildFile));
    }
}
