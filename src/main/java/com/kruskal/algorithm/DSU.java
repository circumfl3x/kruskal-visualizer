package com.kruskal.algorithm;

import com.kruskal.model.Graph;
import com.kruskal.model.Node;
import com.kruskal.util.ColorGenerator;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Map;

/**
 * Система непересекающихся множеств.
 * Используется для проверки, образует ли ребро цикл.
 */

public class DSU {

    private final int[] parent;
    private final int[] rank;
    private final Color[] rootColors;

    // Создает систему множеств для всех n элементов; изначально у каждого эл-та свое множество.
    public DSU(int n) {
        parent = new int[n];
        rank = new int[n];
        rootColors = new Color[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            rank[i] = 0;
            rootColors[i] = ColorGenerator.getColorForIndex(i);
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
        if (rank[rootX] < rank[rootY]) {
            parent[rootX] = rootY;
            rootColors[rootY] = rootColors[rootX]; // цвет нового корня = цвет старого
        } else if (rank[rootX] > rank[rootY]) {
            parent[rootY] = rootX;
            rootColors[rootX] = rootColors[rootY];
        } else {
            parent[rootY] = rootX;
            rank[rootX]++;
            rootColors[rootX] = rootColors[rootY];
        }
        return true;
    }

    public Map<Node, Color> getNodeColors(Graph graph) {
        Map<Node, Color> map = new HashMap<>();
        int index = 0;
        for (Node node : graph.getNodes()) {
            int root = find(index);
            map.put(node, rootColors[root]);
            index++;
        }
        return map;
    }

    // Проверка принадлежности x и y одному множеству
    public boolean connected(int x, int y) {
        return find(x) == find(y);
    }
}
