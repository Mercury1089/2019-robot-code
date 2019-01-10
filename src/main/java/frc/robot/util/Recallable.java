package frc.robot.util;

/**
 * Interface to implement for objects that can recall commands.
 */
public interface Recallable<T> {
    public enum CommandType {
        ROTATION,
        DISTANCE
    }

    public enum RecallMethod {
        REVERSE,
        REPEAT
    }

    public T recall();

    public CommandType getType();
}