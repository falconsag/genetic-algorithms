package com.falconsag.genetic.algorithms.model.robot;

import java.util.Random;

public class RandomInterval {

    private Random rand = new Random();

    private int lowerBound;
    private int upperBound;

    public RandomInterval(int lowerBound, int upperBound) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    /**
     * includes upperbound
     * @return
     */
    public int getRandom() {
        return rand.nextInt(upperBound - lowerBound + 1) + lowerBound;
    }
}
