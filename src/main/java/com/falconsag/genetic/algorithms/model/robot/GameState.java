package com.falconsag.genetic.algorithms.model.robot;

import java.util.ArrayList;
import java.util.List;

public class GameState {

    private List<Food> foods = new ArrayList<>();
    private Robot robot = null;
    private boolean seesFood = false;
    private int ahead = 0;
    private int rHP;
    private int fHP;

    public GameState() {
    }

    public void setRobot(Robot robot) {
        this.robot = robot;
    }

    public void pushFood(Food food) {
        foods.add(food);
    }

    public void setSingleFood(Food food) {
        foods.clear();
        pushFood(food);
    }

    public List<Food> getFoods() {
        return foods;
    }

    public Robot getRobot() {
        return robot;
    }

    public boolean isSeesFood() {
        return seesFood;
    }

    public void setSeesFood(boolean seesFood) {
        this.seesFood = seesFood;
    }

    public void setAhead(int ahead) {
        this.ahead = ahead;
    }

    public int getAhead() {
        return ahead;
    }

    public int getfHP() {
        return fHP;
    }

    public void setfHP(int fHP) {
        this.fHP = fHP;
    }

    public int getrHP() {
        return rHP;
    }

    public void setrHP(int rHP) {
        this.rHP = rHP;
    }
}
