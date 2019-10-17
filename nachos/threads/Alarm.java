package nachos.threads;

import nachos.machine.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Uses the hardware timer to provide preemption, and to allow threads to sleep
 * until a certain time.
 */
public class Alarm {

	private Map<KThread, Long> waitingThreads;

	/**
	 * Allocate a new Alarm. Set the machine's timer interrupt handler to this
	 * alarm's callback.
	 * 
	 * <p>
	 * <b>Note</b>: Nachos will not function correctly with more than one alarm.
	 */
	public Alarm() {
		Machine.timer().setInterruptHandler(new Runnable() {
			public void run() {
				timerInterrupt();
			}
		});

		this.waitingThreads = new HashMap<KThread, Long>();
	}

	/**
	 * The timer interrupt handler. This is called by the machine's timer
	 * periodically (approximately every 500 clock ticks). Causes the current
	 * thread to yield, forcing a context switch if there is another thread that
	 * should be run.
	 */
	public void timerInterrupt() {

		Machine.interrupt().disable();
		LinkedList<KThread> finishedThreads = new LinkedList<>();

		for (KThread thread : this.waitingThreads.keySet()) {

			long currentThreadWakeTime = this.waitingThreads.get(thread);

			if (currentThreadWakeTime < Machine.timer().getTime()) {
				finishedThreads.add(thread);
				thread.ready();
			}
		}

		for (KThread thread : finishedThreads) {
			this.waitingThreads.remove(thread);
		}

		Machine.interrupt().enable();

		KThread.currentThread().yield();
	}

	/**
	 * Put the current thread to sleep for at least <i>x</i> ticks, waking it up
	 * in the timer interrupt handler. The thread must be woken up (placed in
	 * the scheduler ready set) during the first timer interrupt where
	 * 
	 * <p>
	 * <blockquote> (current time) >= (WaitUntil called time)+(x) </blockquote>
	 * 
	 * @param x the minimum number of clock ticks to wait.
	 * 
	 * @see nachos.machine.Timer#getTime()
	 */
	public void waitUntil(long x) {
		long wakeTime = Machine.timer().getTime() + x;

		if (wakeTime <= Machine.timer().getTime()) {
			return;
		}

        KThread currentThread = KThread.currentThread();

		Machine.interrupt().disable();
		updateThreadWaitTime(currentThread, wakeTime);
		currentThread.sleep();
		Machine.interrupt().enable();
	}

	private void updateThreadWaitTime(KThread threadToUpdate, long newWakeTime) {

		if (this.waitingThreads.containsKey(threadToUpdate)) {
			if (newWakeTime > this.waitingThreads.get(threadToUpdate)) {
				this.waitingThreads.replace(threadToUpdate, newWakeTime);
			}
			return;
		}

		this.waitingThreads.put(threadToUpdate, newWakeTime);
	}

	/**
 * Cancel any timer set by <i>thread</i>, effectively waking
 * up the thread immediately (placing it in the scheduler
 * ready set) and returning true.  If <i>thread</i> has no
 * timer set, return false.
 *
 * <p>
 * @param thread the thread whose timer should be cancelled.
 */
	public boolean cancel(KThread thread) {

        Machine.interrupt().disable();
        boolean isThreadWaiting = this.waitingThreads.containsKey(thread);

        if (isThreadWaiting) {
            thread.ready();
            this.waitingThreads.remove(thread);
        }

        Machine.interrupt().enable();
        return isThreadWaiting;
    }

	// Add Alarm testing code to the Alarm class

	public static void alarmTest1() {
		int durations[] = {1000, 10*1000, 100*1000};
		long t0, t1;

		for (int d : durations) {
			t0 = Machine.timer().getTime();
			ThreadedKernel.alarm.waitUntil (d);
			t1 = Machine.timer().getTime();
			System.out.println ("alarmTest1: waited for " + (t1 - t0) + " ticks");
		}
	}

	// Implement more test methods here ...

	// Invoke Alarm.selfTest() from ThreadedKernel.selfTest()
	public static void selfTest() {
		alarmTest1();

		// Invoke your other test methods here ...
	}
}
