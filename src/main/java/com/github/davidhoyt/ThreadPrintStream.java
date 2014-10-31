package com.github.davidhoyt;

//Courtesy:
//  http://maiaco.com/articles/java/threadOut.php
//Some modifications made to use InheritableThreadLocal instead of just ThreadLocal.

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/** A ThreadPrintStream replaces the normal System.out and ensures
 * that output to System.out goes to a different PrintStream for
 * each thread.  It does this by using ThreadLocal to maintain a
 * PrintStream for each thread. */
public class ThreadPrintStream extends PrintStream {
    // Save the existing System.out
    public static final PrintStream console = System.out;

    /** Changes System.out to a ThreadPrintStream which will
     * send output to a separate file for each thread. */
    public static void replaceSystemOut() {

        // Create a ThreadPrintStream and install it as System.out
        ThreadPrintStream threadOut = new ThreadPrintStream();
        System.setOut(threadOut);

        // Use the original System.out as the current thread's System.out
        threadOut.setThreadOut(console);
    }

    public static PrintStream getThreadLocalSystemOut() {
        return out.get();
    }

    public static void setThreadLocalSystemOut(final PrintStream newSystemOut) {
        out.set(newSystemOut);
    }

    /** Thread specific storage to hold a PrintStream for each thread */
    private static final InheritableThreadLocal<PrintStream> out = new InheritableThreadLocal<PrintStream>() {
        @Override
        protected PrintStream initialValue() {
            return console;
        }
    };

    private ThreadPrintStream() {
        super(new ByteArrayOutputStream(0));
    }

    /** Sets the PrintStream for the currently executing thread. */
    public void setThreadOut(PrintStream out) {
        this.out.set(out);
    }

    /** Returns the PrintStream for the currently executing thread. */
    public PrintStream getThreadOut() {
        return this.out.get();
    }

    @Override public boolean checkError() {
        return getThreadOut().checkError();
    }

    @Override public void write(byte[] buf, int off, int len) {
        getThreadOut().write(buf, off, len);
    }

    @Override public void write(int b) { getThreadOut().write(b); }

    @Override public void flush() { getThreadOut().flush(); }
    @Override public void close() { getThreadOut().close(); }
}