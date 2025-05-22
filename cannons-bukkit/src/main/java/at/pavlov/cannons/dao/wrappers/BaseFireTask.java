package at.pavlov.cannons.dao.wrappers;

/**
 * What to execute when a cannon is fired
 */
@FunctionalInterface
public interface BaseFireTask {
    void fireTask();
}
