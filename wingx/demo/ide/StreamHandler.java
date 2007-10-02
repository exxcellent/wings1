package ide;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

public class StreamHandler
{
    private Thread inputThread;
    private Thread errorThread;

    private OutputStream out, err;

    public StreamHandler(OutputStream out, OutputStream err) {
        this.out = out;
        this.err = err;
    }

    public StreamHandler(OutputStream outAndErr) {
        this(outAndErr, outAndErr);
    }

    public StreamHandler() {
        this(System.out, System.err);
    }

    public void setProcessOutputStream(InputStream is) {
        inputThread = createPump(is, out);
    }


    public void setProcessErrorStream(InputStream is) {
        errorThread = createPump(is, err);
    }

    public void start() {
        inputThread.start();
        errorThread.start();
    }


    public void stop() {
        try {
            inputThread.join();
        } catch(InterruptedException e) {}
        try {
            errorThread.join();
        } catch(InterruptedException e) {}
        try {
            err.flush();
        } catch (IOException e) {}
        try {
            out.flush();
        } catch (IOException e) {}
    }

    protected OutputStream getErr() {
        return err;
    }

    protected OutputStream getOut() {
        return out;
    }

    /**
     * Creates a stream pumper to copy the given input stream to the given output stream.
     */
    protected Thread createPump(InputStream is, OutputStream os) {
        final Thread result = new Thread(new StreamPumper(is, os));
        result.setDaemon(true);
        return result;
    }
}
