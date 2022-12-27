package pl.codewise.xmas.task;

import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;

import static pl.codewise.xmas.task.Cookie.CookieType.CHRISTMAS_TREE;
import static pl.codewise.xmas.task.Cookie.CookieType.SANTA_CLAUS_HAT;

/**
 * You can modify this class
 */
public class Cookie {

    public enum CookieType {
        CHRISTMAS_TREE,
        SANTA_CLAUS_HAT,
        ERROR
    }

    private final String label;
    private final CookieType cookieType;
    private final Instant created;

    public Cookie(String label, CookieType cookieType, Instant created) {
        this.label = label;
        this.cookieType = cookieType;
        this.created = created;
    }

    static Cookie withLabel(String label) {
        return new Cookie(label,
                ThreadLocalRandom.current().nextBoolean()
                        ? CHRISTMAS_TREE
                        : SANTA_CLAUS_HAT,
                Instant.now());
    }

    public String getLabel() {
        return label;
    }

    public CookieType getCookieType() {
        return cookieType;
    }

    public Instant getCreated() {
        return created;
    }

    public boolean wasCreatedAfter(Instant someTime) {
        return getCreated().isAfter(someTime);
    }
}
