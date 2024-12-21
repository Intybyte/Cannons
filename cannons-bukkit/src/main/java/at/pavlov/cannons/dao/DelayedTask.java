package at.pavlov.cannons.dao;

public abstract class DelayedTask<Wrapped> implements Runnable {
	private final Wrapped wrapper;

    public DelayedTask(Wrapped wrapper) {
        this.wrapper = wrapper;
    }
   
    @Override
    public final void run() {
        run(wrapper);
    }
   
    public abstract void run(Wrapped wrapper2);
}
