package com.kruskal.algorithm;

/**
 * Система непересекающихся множеств.
 * Используется для проверки, образует ли ребро цикл.
 */

public class DSU {

    private final int[] parent;
    private final int[] rank;

    // Создает систему множеств для всех n элементов; изначально у каждого эл-та свое множество.
    public DSU(int n) {
        parent = new int[n];
        rank = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            rank[i] = 0;
        }
    }

    /**
     * Поиск корня множества элемента x == "значение" множества,
     * которому принадлежит x.
     * Используем рекурсию, чтобы пройтись вглубь дерева до корня и вернуться
     * назад, переписывая родителя вершины на корень.
     */
    public int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]);
        }
        return parent[x];
    }

    /**
     * Объединение множеств, содержащих x и y.
     * Возвращает true, если x и y были в разных множествах и произошло объединение,
     * false, если x и y уже были в одном множестве.
     */
    public boolean union(int x, int y){
        int rootX = find(x);
        int rootY = find(y);

        if (rootX == rootY) {
            return false;
        }

        // объединение по рангу
        if (rank[rootX] < rank[rootY]) {parent[rootX] = rootY;}
        else if (rank[rootX] > rank[rootY]) {parent[rootY] = rootX;}
        else {
            parent[rootY] = rootX;
            rank[rootX]++;
        }
        return true;
    }

    // Проверка принадлежности x и y одному множеству
    public boolean connected(int x, int y) {
        return find(x) == find(y);
    }
}
