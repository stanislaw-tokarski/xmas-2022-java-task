package pl.codewise.xmas.task;

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

    public Cookie(String label, CookieType cookieType) {
        this.label = label;
        this.cookieType = cookieType;
    }

    public String getLabel() {
        return label;
    }

    public CookieType getCookieType() {
        return cookieType;
    }
}
