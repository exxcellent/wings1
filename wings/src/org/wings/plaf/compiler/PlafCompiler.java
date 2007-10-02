/*
 * $Id$
 * (c) Copyright 2000 wingS development team.
 *
 * This file is part of wingS (http://j-wings.org).
 *
 * wingS is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * Please see COPYING for the complete licence.
 *
 * Plaf Compiler
 */
package org.wings.plaf.compiler;

import java.io.File;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;
import org.wings.template.parser.SGMLTag;

public class PlafCompiler {
    public final static String VAR_PREFIX = "__";
    
    protected String  varPrefix   = VAR_PREFIX;
    protected String  packageName = null;
    protected boolean verbose     = false;
    protected String[] files      = null;
    protected File baseDir;
    protected File destDir;

    /**
     * create a new plaf compiler. This virgin compiler has to be
     * parametrized with the set*() methods before calling run()
     */
    public PlafCompiler() { 
        try {
            setBaseDir((new File(".")).getCanonicalFile());
        }
        catch (IOException ignore_me) {}
        setDestinationDirectory(getBaseDir());
    }

    /**
     * set the variable prefix for the generated variables containing
     * template snippets. Default is <code>__</code>
     */
    public void setVarPrefix(String v) { varPrefix = v; }
    
    /**
     * returns the var prefix.
     * @see #setVarPrefix(String)
     */
    public String getVarPrefix() { return varPrefix; }
    
    /**
     * sets the package name, the generated java classes are in.
     * Something like 'foo.bar.baz'.
     */
    public void setPackageName(String p) { packageName = p; }

    /**
     * returns the package name.
     * @see #setPackageName(String)
     */
    public String getPackageName() { return packageName; }

    /**
     * sets, whether the compiler should be verbose.
     * Default: false
     */
    public void setVerbose(boolean v) { verbose = v; }

    /**
     * returns, if the compiler is verbose.
     * @see #setVerbose(boolean)
     */
    public boolean isVerbose() { return verbose; }

    /**
     * sets the list of files to be compiled.
     */
    public void setFiles(String[] fs) { files = fs; }

    /**
     * returns the list of files to be compiled.
     * @see #setFiles(String[])
     */
    public String[] getFiles() { return files; }
    
    /**
     * Sets the base directory for error reporting. Filenames in errormessages
     * are reported relative to this working directory. 
     * DEFAULT: current working diretory.
     */
    public void setBaseDir(File basedir) {  this.baseDir = basedir; }

    /**
     * returns the current working diretory.
     * @see #setBaseDir
     */
    public File getBaseDir() { return baseDir; }
    
    /**
     * sets the directory, the generated files are to be written to.
     * DEFAULT: current working directory.
     */
    public void setDestinationDirectory(File dest) { destDir = dest; }

    /**
     * returns the destination directory.
     */
    public File getDestinationDirectory() { return destDir; }

    /**
     * runs the compiler. This takes no parametrs, but the compiler has to 
     * be configured with the setters.
     * @return boolean, indicating, if we has success.
     */
    public void run() throws Exception {
        if (files == null) {
            System.err.println("nothing to do: no files given.");
            return;
        }

        Vector properties = new Vector();
	for (int i=0; i < files.length; ++i) {
            TemplateParser parser = null;
            String file = files[i];
            PlafReader input = new PlafReader(baseDir, file);
            // fixme: move to template parser.
            SGMLTag tag = new SGMLTag(input);
            while (!tag.finished()) {
                String name = tag.getAttribute("NAME", null);
                String forClass = tag.getAttribute("FOR", null);
                if (name == null || forClass == null) {
                    throw new IOException (file +": 'name' and 'for' as template attributes expected");
                }
                String extendsClass = tag.getAttribute("EXTENDS", null);
                if (verbose) {
                    System.err.println ("template for " + name);
                }
                parser = new TemplateParser(name, baseDir, new File(file),
                                            packageName, forClass,
                                            extendsClass);
                parser.parse(input);
                parser.generate(destDir, properties);
                tag = new SGMLTag(input);
            }
        }
        
        File propertyFile = new File(destDir, "default.properties");
        PrintWriter writer = new PrintWriter(new FileWriter(propertyFile));
        writer.println("# default.properties automatically generated.");
        writer.println();
        Iterator propIt = properties.iterator();
        while (propIt.hasNext()) {
            String p = (String) propIt.next();
            writer.println(p);
        }
        writer.close();
    }
    
    /*
     * main() for standalone app.
     */
    public static void main(String argv[]) throws Exception {
        boolean verbose = true;

	if (argv.length == 0) {
	    usage();
	    return;
	}
        
        PlafCompiler compiler = new PlafCompiler();
        for (int i = 0; i < argv.length; ++i) {
            String option = argv[i];
            String param = null;

            // no dash: the rest are files.
            if (! option.startsWith("-")) {
                if (i == 0) {
                    compiler.setFiles(argv);
                }
                else {
                    String[] files = new String [ argv.length - i ];
                    System.arraycopy(argv, i, files, 0, argv.length - i);
                    compiler.setFiles(files);
                }
                break;  // we consumed the rest of the arguments.
            }

            // we do not understand combined options likes -vp
            if (option.length() != 2) {
                System.err.println("invalid option: '" + option + "'");
                usage();
                return;
            }

            // options ..
            switch (option.charAt(1)) {
            case 'h':                  // help
                usage();
                return;
            case 'v':                  // verbose
                compiler.setVerbose(true); 
                break;
            case 'p':                  // package
                param = getOptionParam(argv, ++i);
                if (param == null) return;
                compiler.setPackageName(param);
                break;
            case 'd':                  // directory
                param = getOptionParam(argv, ++i);
                if (param == null) return;
                compiler.setDestinationDirectory(new File(param));
                break;
            }
        }
        try {
            compiler.run();
        }
        catch (Exception e) {
            System.exit(-1);
        }
    }
    
    /**
     * tries to get the optional parameter from the array. If we run out
     * of arguments then report this as an error.
     */
    private static String getOptionParam(String[] argv, int pos) {
        if (pos >= argv.length) {
            System.err.println("parameter expected.");
            usage();
            return null;
        }
        return argv[pos];
    }

    /**
     * print the usage, if used as standalone application.
     */
    public static void usage() {
	System.err.println ("usage: PlafCompiler [options] <plaf-source> [<plaf-source>..]");
        System.err.println("OPTIONS:");
        System.err.println("\t-h               : this help.");
        System.err.println("\t-v               : verbose.");
        System.err.println("\t-p <packagename> : package of generated java-files");
        System.err.println("\t-d <directory>   : output directory");
    }
}

/*
 * Local variables:
 * c-basic-offset: 4
 * indent-tabs-mode: nil
 * compile-command: "ant -emacs -find build.xml"
 * End:
 */
