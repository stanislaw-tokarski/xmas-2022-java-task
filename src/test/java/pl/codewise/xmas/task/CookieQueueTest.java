package pl.codewise.xmas.task;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.concurrent.CompletableFuture.runAsync;
import static java.util.stream.Collectors.toList;
import static pl.codewise.xmas.task.Cookie.CookieType.CHRISTMAS_TREE;

public class CookieQueueTest {

    @Test
    public void shouldReportAllCookies() {
        // given
        var queue = new CookieQueueImpl(100);
        var cookies = createCookies(100);
        // when
        cookies.forEach(queue::add);
        var report = queue.getReport();

        // then
        Assertions.assertEquals(100, report.getCookies().size());
    }

    @Test
    public void shouldReportNumberOfCookiesEqualToQueueSize() {
        // given
        var queue = new CookieQueueImpl(100);
        var cookies = createCookies(200);
        // when
        cookies.forEach(queue::add);
        var report = queue.getReport();

        // then
        Assertions.assertEquals(100, report.getCookies().size());
    }

    @Test
    public void shouldReportOnlyCookiesCreatedWithinLastFiveMinutes() {
        // given
        var queue = new CookieQueueImpl(100);
        var cookies = IntStream.range(0, 99)
                .boxed()
                .map(i -> Cookie.withLabel("Cookie-" + i))
                .collect(toList());
        var oldCookie = new Cookie("Cookie-99", CHRISTMAS_TREE, Instant.now().minus(5, MINUTES).minus(1, SECONDS));
        cookies.add(oldCookie);

        // when
        cookies.forEach(queue::add);
        var report = queue.getReport();

        // then
        Assertions.assertEquals(99, report.getCookies().size());
        Assertions.assertTrue(report.getCookies().stream().noneMatch(cookie -> cookie.getLabel().equals("Cookie-99")));
    }

    @Test
    public void shouldNotExceedQueueSizeInMultipleWritersScenario() {
        // given
        var queue = new CookieQueueImpl(10);
        var cookies = createCookies(10);
        cookies.forEach(queue::add);
        List<Report> reports = new ArrayList<>();

        var writersThreadPool = Executors.newFixedThreadPool(10);
        var readersThreadPool = Executors.newFixedThreadPool(2);
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        // when
        for (int i = 0; i < 10; i++) {
            futures.add(runAsync(() -> createCookies(100).forEach(queue::add), writersThreadPool));
        }

        // then
        for (int j = 0; j < 100; j++) {
            futures.add(runAsync(() -> reports.add(queue.getReport()), readersThreadPool));
        }
        futures.forEach(CompletableFuture::join);
        Assertions.assertTrue(reports.stream().allMatch(report -> report.getCookies().size() == 10));
    }

    private List<Cookie> createCookies(int amount) {
        return IntStream.range(0, amount)
                .boxed()
                .map(i -> Cookie.withLabel("Cookie-" + i))
                .toList();
    }
}
