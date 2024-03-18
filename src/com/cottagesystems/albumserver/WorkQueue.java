package com.cottagesystems.albumserver;

import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

// from http://www.ibm.com/developerworks/library/j-jtp0730/index.html "work queues"

public class WorkQueue {
    private final static Logger logger= Logger.getLogger("albumServer");
    
    private final PoolWorker[] threads;
    private final LinkedList queue;

    public WorkQueue(int nThreads) {
        queue = new LinkedList();
        threads = new PoolWorker[nThreads];

        for (int i=0; i<nThreads; i++) {
            threads[i] = new PoolWorker();
            threads[i].start();
        }
    }

    /**
     * execute the job.
     * @param r 
     */
    public void execute(Runnable r) {
        synchronized(queue) {
            queue.addLast(r);
            queue.notify();
        }
    }
    
    /**
     * return the number of items in the queue.
     * @return 
     */
    public int size() {
        return queue.size();
    }

    private class PoolWorker extends Thread {
        public void run() {
            Runnable r;

            while (true) {
                synchronized(queue) {
                    while (queue.isEmpty()) {
                        try
                        {
                            queue.wait();
                        }
                        catch (InterruptedException ignored)
                        {
                        }
                    }

                    r = (Runnable) queue.removeFirst();
                    logger.log(Level.INFO, "workQueue depth is {0}", queue.size());
                }

                // If we don't catch RuntimeException,
                // the pool could leak threads
                try {
                    r.run();
                }
                catch (RuntimeException e) {
                    // You might want to log something here
                }
            }
        }
    }

    private static WorkQueue instance= null;

    public synchronized static final WorkQueue getInstance() {
        if ( instance==null ) {
            instance= new WorkQueue( Configuration.getThreadCount() );
        }
        return instance;
    }
}
