package com.kruskal.util;

import com.kruskal.model.Edge;
import com.kruskal.model.Node;
import javafx.application.Platform;
import javafx.scene.control.TextArea;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import com.kruskal.util.JavaFXTestUtil;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class LoggerTest {
    @BeforeAll
    static void initJavaFX() {
        JavaFXTestUtil.init();
    }
    private Logger createLogger(TextArea area) {
        return new Logger(area);
    }
    private Edge createEdge(int id1, int id2, int weight) {
        Node n1 =
                new Node(id1, 100, 100);
        Node n2 =
                new Node(id2, 200, 200);
        return new Edge(
                n1,
                n2,
                weight
        );
    }

    @Test
    void testLogAddsMessage() {
        TextArea area =
                new TextArea();
        Logger logger =
                createLogger(area);
        logger.log("Тестовое сообщение");
        assertTrue(
                area.getText()
                        .contains("Тестовое сообщение")
        );
    }

    @Test
    void testClearSetsWelcomeMessage() {
        TextArea area =
                new TextArea();
        area.setText("старый текст");
        Logger logger =
                createLogger(area);
        logger.clear();
        assertEquals(
                "Добро пожаловать! Загрузите граф или создайте его вручную.\n",
                area.getText()
        );
    }

    @Test
    void testLogSortedEdges() {

        TextArea area =
                new TextArea();


        Logger logger =
                createLogger(area);


        List<Edge> edges =
                List.of(
                        createEdge(0,1,5),
                        createEdge(1,2,10)
                );


        logger.logSortedEdges(edges);


        String text =
                area.getText();


        assertTrue(
                text.contains("Рёбра отсортированы по весу")
        );


        assertTrue(
                text.contains("0 - 1 (вес 5)")
        );


        assertTrue(
                text.contains("1 - 2 (вес 10)")
        );
    }



    @Test
    void testLogResultForConnectedGraph() {

        TextArea area =
                new TextArea();


        Logger logger =
                createLogger(area);


        List<Edge> mst =
                List.of(
                        createEdge(0,1,3),
                        createEdge(1,2,4)
                );


        logger.logResult(
                7,
                mst,
                3
        );


        String text =
                area.getText();


        assertTrue(
                text.contains(
                        "Алгоритм Краскала завершён"
                )
        );


        assertTrue(
                text.contains(
                        "Общий вес дерева: 7"
                )
        );


        assertTrue(
                text.contains(
                        "Граф связный"
                )
        );
    }



    @Test
    void testLogResultForDisconnectedGraph() {

        TextArea area =
                new TextArea();


        Logger logger =
                createLogger(area);


        List<Edge> mst =
                List.of(
                        createEdge(0,1,3)
                );


        logger.logResult(
                3,
                mst,
                5
        );


        assertTrue(
                area.getText()
                        .contains(
                                "Граф несвязный"
                        )
        );
    }



    @Test
    void testLogGraphGenerated() {

        TextArea area =
                new TextArea();


        Logger logger =
                createLogger(area);


        logger.logGraphGenerated(
                5,
                7
        );


        String text =
                area.getText();


        assertTrue(
                text.contains(
                        "Сгенерирован случайный граф"
                )
        );


        assertTrue(
                text.contains(
                        "Количество вершин: 5, рёбер: 7"
                )
        );
    }



    @Test
    void testLogGraphLoaded() {

        TextArea area =
                new TextArea();


        Logger logger =
                createLogger(area);


        logger.logGraphLoaded(
                "graph.txt",
                4,
                5
        );


        String text =
                area.getText();


        assertTrue(
                text.contains(
                        "Граф загружен из файла graph.txt"
                )
        );


        assertTrue(
                text.contains(
                        "Количество вершин: 4, рёбер: 5"
                )
        );
    }



    @Test
    void testLogGraphSaved() {

        TextArea area =
                new TextArea();


        Logger logger =
                createLogger(area);


        logger.logGraphSaved(
                "test.txt"
        );


        assertTrue(
                area.getText()
                        .contains(
                                "Граф успешно сохранён: test.txt"
                        )
        );
    }



    @Test
    void testLogAlgorithmStarted() {

        TextArea area =
                new TextArea();


        Logger logger =
                createLogger(area);


        logger.logAlgorithmStarted();


        assertTrue(
                area.getText()
                        .contains(
                                "Запуск алгоритма Краскала"
                        )
        );
    }



    @Test
    void testLogError() {

        TextArea area =
                new TextArea();


        Logger logger =
                createLogger(area);


        logger.logError(
                "Ошибка тест"
        );


        assertTrue(
                area.getText()
                        .contains(
                                "Ошибка: Ошибка тест"
                        )
        );
    }



    @Test
    void testLogCleared() {

        TextArea area =
                new TextArea();


        Logger logger =
                createLogger(area);


        logger.logCleared();


        assertTrue(
                area.getText()
                        .contains(
                                "Рабочая область очищена"
                        )
        );
    }
}