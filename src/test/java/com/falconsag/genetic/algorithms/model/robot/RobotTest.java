package com.falconsag.genetic.algorithms.model.robot;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import org.junit.Test;


public class RobotTest {

    @Test
    public void getSensorBitmap() {
        //RIGHT
        Coord rPos = Coord.of(3, 3);
        Coord rDir = Coord.of(1, 0);

        Coord fPos = Coord.of(4, 2);
        Robot r = new Robot(100, rPos, rDir);
        int sensorBitmap = r.getSensorBitmap(fPos);
        assertThat(sensorBitmap, is(15));

        fPos = Coord.of(5, 3);
        r = new Robot(100, rPos, rDir);
        sensorBitmap = r.getSensorBitmap(fPos);
        assertThat(sensorBitmap, is(16));

        fPos = Coord.of(4, 4);
        r = new Robot(100, rPos, rDir);
        sensorBitmap = r.getSensorBitmap(fPos);
        assertThat(sensorBitmap, is(9));

        fPos = Coord.of(2, 3);
        r = new Robot(100, rPos, rDir);
        sensorBitmap = r.getSensorBitmap(fPos);
        assertThat(sensorBitmap, is(0));

        //DOWN
        rPos = Coord.of(3, 3);
        rDir = Coord.of(0, 1);

        fPos = Coord.of(4, 4);
        r = new Robot(100, rPos, rDir);
        sensorBitmap = r.getSensorBitmap(fPos);
        assertThat(sensorBitmap, is(15));

        fPos = Coord.of(3, 5);
        r = new Robot(100, rPos, rDir);
        sensorBitmap = r.getSensorBitmap(fPos);
        assertThat(sensorBitmap, is(16));

        fPos = Coord.of(2, 4);
        r = new Robot(100, rPos, rDir);
        sensorBitmap = r.getSensorBitmap(fPos);
        assertThat(sensorBitmap, is(9));

        //can't see
        fPos = Coord.of(3, 2);
        r = new Robot(100, rPos, rDir);
        sensorBitmap = r.getSensorBitmap(fPos);
        assertThat(sensorBitmap, is(0));


        //LEFT
        rPos = Coord.of(3, 3);
        rDir = Coord.of(-1, 0);

        fPos = Coord.of(2, 4);
        r = new Robot(100, rPos, rDir);
        sensorBitmap = r.getSensorBitmap(fPos);
        assertThat(sensorBitmap, is(15));

        fPos = Coord.of(1, 3);
        r = new Robot(100, rPos, rDir);
        sensorBitmap = r.getSensorBitmap(fPos);
        assertThat(sensorBitmap, is(16));

        fPos = Coord.of(2, 2);
        r = new Robot(100, rPos, rDir);
        sensorBitmap = r.getSensorBitmap(fPos);
        assertThat(sensorBitmap, is(9));

        //can't see
        fPos = Coord.of(4, 3);
        r = new Robot(100, rPos, rDir);
        sensorBitmap = r.getSensorBitmap(fPos);
        assertThat(sensorBitmap, is(0));

        //UP
        rPos = Coord.of(3, 3);
        rDir = Coord.of(0, -1);

        fPos = Coord.of(2, 2);
        r = new Robot(100, rPos, rDir);
        sensorBitmap = r.getSensorBitmap(fPos);
        assertThat(sensorBitmap, is(15));

        fPos = Coord.of(3, 1);
        r = new Robot(100, rPos, rDir);
        sensorBitmap = r.getSensorBitmap(fPos);
        assertThat(sensorBitmap, is(16));

        fPos = Coord.of(4, 2);
        r = new Robot(100, rPos, rDir);
        sensorBitmap = r.getSensorBitmap(fPos);
        assertThat(sensorBitmap, is(9));

        //can't see
        fPos = Coord.of(3, 4);
        r = new Robot(100, rPos, rDir);
        sensorBitmap = r.getSensorBitmap(fPos);
        assertThat(sensorBitmap, is(0));

    }

    @Test
    public void testEncode3Bits() {
        assertThat(Robot.encodeOnThreeBits(0), is(0));
        assertThat(Robot.encodeOnThreeBits(1), is(1));
        assertThat(Robot.encodeOnThreeBits(2), is(2));
        assertThat(Robot.encodeOnThreeBits(3), is(3));
        assertThat(Robot.encodeOnThreeBits(-3), is(5));
        assertThat(Robot.encodeOnThreeBits(-2), is(6));
        assertThat(Robot.encodeOnThreeBits(-1), is(7));
    }
}