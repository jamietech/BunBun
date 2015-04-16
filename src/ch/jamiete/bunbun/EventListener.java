package ch.jamiete.bunbun;

public abstract class EventListener {
    private final BunBun bun;

    public EventListener(final BunBun bun) {
        this.bun = bun;
        this.getBun().getClient().getEventManager().registerEventListener(this);
    }

    public BunBun getBun() {
        return this.bun;
    }

}
