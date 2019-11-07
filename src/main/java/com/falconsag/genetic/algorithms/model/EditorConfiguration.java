package com.falconsag.genetic.algorithms.model;

import java.util.List;

public class EditorConfiguration {
    private int mazeSize;
    private List<Integer> mapArr;
    private RobotConfig robot;

    public int getMazeSize() {
        return mazeSize;
    }

    public void setMazeSize(int mazeSize) {
        this.mazeSize = mazeSize;
    }

    public List<Integer> getMapArr() {
        return mapArr;
    }

    public void setMapArr(List<Integer> mapArr) {
        this.mapArr = mapArr;
    }

    public RobotConfig getRobot() {
        return robot;
    }

    public void setRobot(RobotConfig robot) {
        this.robot = robot;
    }
}
