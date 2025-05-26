package at.pavlov.internal;

import java.util.Random;

public interface MaxMinRandom {
    Random random = new Random();

    int getMinAmount();
    int getMaxAmount();

    default int getRandomAmount() {
        int max = getMaxAmount();
        int min = getMinAmount();
        return random.nextInt( max + 1 - min) + min;
    }
}
