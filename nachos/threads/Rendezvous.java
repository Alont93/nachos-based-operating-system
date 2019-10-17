package nachos.threads;

import nachos.machine.*;

import java.util.HashMap;
import java.util.Map;

/**
 * A <i>Rendezvous</i> allows threads to synchronously exchange values.
 */
public class Rendezvous {

    private Map<Integer, Integer> tagsToValue;
    private Map<Integer, ConditionandLockPair> tagsToSyncPair;

    /**
     * Allocate a new Rendezvous.
     */
    public Rendezvous () {
        tagsToValue = new HashMap<>();
        tagsToSyncPair = new HashMap<>();
    }

    /**
     * Synchronously exchange a value with another thread.  The first
     * thread A (with value X) to exhange will block waiting for
     * another thread B (with value Y).  When thread B arrives, it
     * will unblock A and the threads will exchange values: value Y
     * will be returned to thread A, and value X will be returned to
     * thread B.
     *
     * Different integer tags are used as different, parallel
     * synchronization points (i.e., threads synchronizing at
     * different tags do not interact with each other).  The same tag
     * can also be used repeatedly for multiple exchanges.
     *
     * @param tag the synchronization tag.
     * @param value the integer to exchange.
     */
    public int exchange (int tag, int value) {

        if (!this.tagsToSyncPair.containsKey(tag)) {
            Lock tagLock = new Lock();
            Condition tagCondition = new Condition(tagLock);
            this.tagsToSyncPair.put(tag, new ConditionandLockPair(tagLock, tagCondition));
        }
        this.tagsToSyncPair.get(tag).getLock().acquire();

        if (!this.tagsToValue.containsKey(tag)) {
            this.tagsToValue.put(tag, value);
            this.tagsToSyncPair.get(tag).getCondition().sleep();
        }
        else {
            int firstTagValue = this.tagsToValue.get(tag);
            this.tagsToValue.replace(tag, value);
            this.tagsToSyncPair.get(tag).getLock().release();
            this.tagsToSyncPair.get(tag).getCondition().wake();
            return firstTagValue;
        }

        int otherPairValue = tagsToValue.get(tag);
        this.tagsToValue. remove(tag);
        Lock tagLock = this.tagsToSyncPair.get(tag).getLock();
        this.tagsToSyncPair.remove(tag);
        tagLock.release();

        return otherPairValue;
    }




    // Place Rendezvous test code inside of the Rendezvous class.

    public static void rendezTest1() {
        final Rendezvous r = new Rendezvous();

        KThread t1 = new KThread( new Runnable () {
            public void run() {
                int tag = 0;
                int send = -1;

                System.out.println ("Thread " + KThread.currentThread().getName() + " exchanging " + send);
                int recv = r.exchange (tag, send);
                Lib.assertTrue (recv == 1, "Was expecting " + 1 + " but received " + recv);
                System.out.println ("Thread " + KThread.currentThread().getName() + " received " + recv);
            }
        });
        t1.setName("t1");
        KThread t2 = new KThread( new Runnable () {
            public void run() {
                int tag = 0;
                int send = 1;

                System.out.println ("Thread " + KThread.currentThread().getName() + " exchanging " + send);
                int recv = r.exchange (tag, send);
                Lib.assertTrue (recv == -1, "Was expecting " + -1 + " but received " + recv);
                System.out.println ("Thread " + KThread.currentThread().getName() + " received " + recv);
            }
        });
        t2.setName("t2");

        t1.fork(); t2.fork();
        // assumes join is implemented correctly
        t1.join(); t2.join();
    }

    // Invoke Rendezvous.selfTest() from ThreadedKernel.selfTest()

    public static void selfTest() {
        // place calls to your Rendezvous tests that you implement here
        rendezTest1();
    }
}
