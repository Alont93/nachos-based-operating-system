package nachos.threads;

public class ThreadPriority {

    private KThread thread;
    private long wakeTime;

    ThreadPriority(KThread thread,long wakeTime){
        this.thread = thread;
        this.wakeTime = wakeTime;
    }

    public KThread getThread() {
        return this.thread;
    }

    public long getWakeTime() {
        return wakeTime;
    }

}