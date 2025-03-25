package at.pavlov.internal;

import org.jetbrains.annotations.NotNull;

public interface Exchanger<Subject, Cannon> {

    enum Type { WITHDRAW, DEPOSIT, UNDEFINED }

    boolean execute(Subject subject, Cannon cannon);
    @NotNull String formatted();
    @NotNull Type type();
}
