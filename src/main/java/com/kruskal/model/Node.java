package com.kruskal.model;

import java.util.Objects;

/**
 * Класс - вершина графа.
 * Поля:
 *  int id - id вершины
 *  double x - координата x
 *  double y - координата y
 *
 * Содержит геттеры и сеттеры для полей, конструктор;
 * переопределяет методы сравнения, получения хэша, возврата строкового
 * представления
 */

public class Node {

    private final int id;
    private double x;
    private double y;

    // Конструктор класса
    public Node(int id, double x, double y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    // Геттеры и сеттеры для полей
    public int getId() {
        return id;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return id == node.id;
    }

    @Override
    public int hashCode(){
        return Objects.hash(id);
    }

    @Override
    public String toString(){
        return "Node {id = " + id + ", x=" + x + ", y=" + y + "}";
    }
}
