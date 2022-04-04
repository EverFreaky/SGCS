package com.everfreaky.sgcs;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class Bot {
    // bot position
    private double x;
    private double y;
    // home coordinates
    private final double hx;
    private final double hy;
    // movement speed
    private final double speed;
    // number of directions to consider
    private final int consideredPositions;
    // pheromone map
    private final Set<Pheromone> pheromones;
    private int ticks;
    // random generator
    private final Random rand;
    public Bot(double x, double y, double hx, double hy, double speed, int consideredPositions) {
        // set parameters
        this.x = x;
        this.y = y;
        this.hx = hx;
        this.hy = hy;
        this.speed = speed;
        this.consideredPositions = consideredPositions;
        this.ticks = 0;
        // initialize pheromone map
        pheromones = new HashSet<>();
        // initialize random generator
        this.rand = new Random(System.nanoTime());
    }
    // Find the distance between two points
    private double dist(double x1, double y1, double x2, double y2) {
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }
    // move the bot
    public void move(long timeStep) {
        double bestDesirabiilty = Double.NaN, bestAng = 0, desirability, ang;
        // possible future positions
        for (int i = 0; i < consideredPositions; ++ i) {
            // generate random angle for direction
            ang = 360 * rand.nextDouble();
            // calculate the coordinates of the future positions from the angle
            double fx = x + timeStep * speed * Math.cos(Math.toRadians(ang));
            double fy = y + timeStep * speed * Math.sin(Math.toRadians(ang));
            desirability = 0;
            // calculate the distance to each pheromone and add it to the desirability
            // the farther the positions is from all pheromones, the more desirable it is
            for (Pheromone p : pheromones) {
                double d = dist(fx, fy, p.getX(), p.getY());
                desirability += d;
            }
            // the farther the position is from home, the less desirable it is
            desirability -= dist(fx, fy, hx, hy) * dist(fx, fy, hx, hy);
            // find the best desirability and angle
            if (Double.isNaN(bestDesirabiilty) || bestDesirabiilty < desirability) {
                bestDesirabiilty = desirability;
                bestAng = ang;
            }
        }
        // drop new pheromone every 60 ticks
        if (ticks == 59) {
            pheromones.add(new Pheromone(x, y));
        }
        ticks = (ticks + 1) % 60;
        // move the bot in the chosen direction
        x += timeStep * speed * Math.cos(Math.toRadians(bestAng));
        y += timeStep * speed * Math.sin(Math.toRadians(bestAng));
    }
    // getters
    public double getX() { return x; }
    public double getY() { return y; }
    public Set<Pheromone> getPheromones() { return pheromones; }
    // add pheromones to the pheromone map
    public void updatePheromones(Set<Pheromone> newPheromones) {
        pheromones.addAll(newPheromones.stream().map(Pheromone::copy).collect(Collectors.toSet()));
    }
    // draw the robot
    public void draw(GraphicsContext ctx) {
        ctx.setFill(Color.BLACK);
        ctx.fillOval(x, y, 10, 10);
    }
}
