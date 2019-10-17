package nachos.threads;

public class ThreadExchangeValue {

    private KThread thread;
    private int value;

    public ThreadExchangeValue(KThread thread, int value) {
        this.thread = thread;
        this.value = value;
    }

    public KThread getThread() {
        return this.thread;
    }

    public int getValue() {
        return this.value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
