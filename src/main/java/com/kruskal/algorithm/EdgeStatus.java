package com.kruskal.algorithm;

/**
 * Статус ребра на текущем шаге визуализации.
 */
public enum EdgeStatus {
    UNPROCESSED,   // ещё не рассмотрено (чёрное)
    CURRENT,       // сейчас рассматривается (подсвечено)
    ADDED,         // добавлено в MST (зелёное)
    REJECTED       // отклонено (красное)
}