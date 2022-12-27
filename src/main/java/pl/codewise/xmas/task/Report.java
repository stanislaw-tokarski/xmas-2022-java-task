package pl.codewise.xmas.task;

import java.util.Collection;

@FunctionalInterface
public interface Report {

    Collection<Cookie> getCookies();
}
