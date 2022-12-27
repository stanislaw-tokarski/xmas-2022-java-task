package pl.codewise.xmas.task;

import java.time.Instant;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Stream;

import static java.time.temporal.ChronoUnit.MINUTES;

public class CookieQueueImpl implements CookieQueue {

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final Cookie[] cookies;
    private final int queueSize;
    private int queueIndex;

    public CookieQueueImpl(int queueSize) {
        this.queueSize = queueSize;
        this.queueIndex = 0;
        this.cookies = new Cookie[queueSize];
    }

    @Override
    public void add(Cookie message) {
        try {
            lock.writeLock().lock();
            queueIndex++;
            if (queueIndex == queueSize) {
                queueIndex = 0;
            }
            cookies[queueIndex] = message;
//            System.out.println(Thread.currentThread().getName() + " added " + message.getLabel() + " cookie");
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Report getReport() {
        try {
            lock.readLock().lock();
            var fiveMinutesAgo = Instant.now().minus(5, MINUTES);
            var snapshot = Stream.of(cookies)
                    .filter(cookie -> cookie.wasCreatedAfter(fiveMinutesAgo))
                    .toList();
//            System.out.println(Thread.currentThread().getName() + " reporting " + snapshot.size() + " cookies");
            return () -> snapshot;
        } finally {
            lock.readLock().unlock();
        }
    }
}
