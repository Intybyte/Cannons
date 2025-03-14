package at.pavlov.internal;

public interface Exchanger<Subject> {
    void take(Subject subject);
    void give(Subject subject);
}
