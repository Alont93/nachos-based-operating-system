package nachos.threads;

public class ThreadExchangeValue {

    private int value;
    private boolean isSwitched;

    public ThreadExchangeValue(int value, boolean isSwiched) {
        this.value = value;
        this.isSwitched = isSwiched;
    }

    public boolean getIsSwitched() {
        return this.isSwitched;
    }

    public void setIsSwitched(boolean switched) {
        this.isSwitched = switched;
    }

    public int getValue() {
        return this.value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
