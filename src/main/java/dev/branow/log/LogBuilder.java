package dev.branow.log;

public interface LogBuilder {
    void started();
    void finished();
    void failed(Throwable throwable);
}
