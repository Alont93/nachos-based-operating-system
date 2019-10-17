package nachos.threads;

public class ConditionandLockPair {

    private Condition condition;
    private Lock lock;

    public ConditionandLockPair(Lock lock, Condition condition) {
        this.lock = lock;
        this.condition = condition;
    }

    public Condition getCondition() {
        return this.condition;
    }

    public Lock getLock() {
        return this.lock;
    }
}
