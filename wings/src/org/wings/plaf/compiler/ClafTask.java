package org.wings.plaf.compiler;

import org.apache.tools.ant.*;
import org.apache.tools.ant.types.*;

import java.io.*;
import java.util.*;
import java.util.jar.*;

public class ClafTask extends Task
{
    private File file;
    private List filesets = new LinkedList();
    private File destdir;
    private boolean verbose = false;
    private String packageName;

    public void execute() throws BuildException {
	try {
            // check all parameters
            if (file == null && filesets.size() == 0) {
                throw new BuildException("Specify at least one source - a file or a fileset.");
            }
            if (file != null && file.exists() && file.isDirectory()) {
                throw new BuildException("Use a fileset to process directories.");
            }
            if (destdir == null || destdir.isFile()) {
                throw new BuildException("You must specify a destination directory (destdir).");
            }

	    PlafCompiler compiler = new PlafCompiler();
	    compiler.setBaseDir(project.getBaseDir());
	    compiler.setPackageName(packageName);
	    compiler.setVerbose(verbose);
	    compiler.setDestinationDirectory(destdir);

	    Iterator it = filesets.iterator();
        while (it.hasNext()) {
            FileSet fs = (FileSet)it.next();
            DirectoryScanner ds = fs.getDirectoryScanner(project);
            File fromDir = fs.getDir(project);
            String[] srcFiles = ds.getIncludedFiles();

            for (int i=0; i < srcFiles.length; i++)
                srcFiles[i] = new File(fromDir, srcFiles[i]).getCanonicalPath();
            for (int i=0; i < srcFiles.length; i++)
                System.err.println("src: " + srcFiles[i]);
            compiler.setFiles(srcFiles);
            compiler.run();
        }

        if (file != null && file.exists()) {
            compiler.setFiles(new String[] { file.getCanonicalPath() });
            compiler.run();
        }
    }
    catch (Exception e) {
        System.err.println(e.getMessage());
        e.printStackTrace(System.err);
        throw new BuildException(e.getMessage());
	}
    }

    // The setter for the "file" attribute
    public void setFile(File file) {
        this.file = file;
    }

    // Adds a set of ejb-jars (nested fileset attribute).
    public void addFileset(FileSet set) {
        filesets.add(set);
    }

    // The setter for the "destfile" attribute
    public void setDestdir(File destdir) {
        this.destdir = destdir;
    }

    // The setter for the "verbose" attribute
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    // The setter for the "packageName" attribute
    public void setPackage(String packageName) {
        this.packageName = packageName;
    }
}
