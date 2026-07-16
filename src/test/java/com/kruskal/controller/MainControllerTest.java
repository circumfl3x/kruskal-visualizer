package com.kruskal.controller;

import com.kruskal.model.Graph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;


class MainControllerTest {
    private MainController controller;

    @BeforeEach
    void setUp() throws Exception {
        controller = new MainController();
        setField(
                "currentGraph",
                new Graph(
                        new ArrayList<>(),
                        new ArrayList<>()
                )
        );
        setField(
                "steps",
                new ArrayList<>()
        );
        setField(
                "currentStepIndex",
                -1
        );
    }

    private void setField(
            String name,
            Object value
    ) throws Exception {
        Field field =
                MainController.class
                        .getDeclaredField(name);
        field.setAccessible(true);
        field.set(controller,value);
    }

    private Object getField(
            String name
    ) throws Exception {
        Field field =
                MainController.class
                        .getDeclaredField(name);
        field.setAccessible(true);
        return field.get(controller);
    }

    @Test
    void controllerShouldBeCreated(){

        assertNotNull(controller);

    }

    @Test
    void emptyGraphShouldBeCreated(){
        assertDoesNotThrow(() -> {
            Graph graph =
                    (Graph)getField(
                            "currentGraph"
                    );
            assertNotNull(graph);
        });
    }

    @Test
    void initialStepsShouldBeEmpty()
            throws Exception {
        ArrayList<?> steps =
                (ArrayList<?>)getField(
                        "steps"
                );
        assertTrue(
                steps.isEmpty()
        );
    }

    @Test
    void initialStepIndexShouldBeMinusOne()
            throws Exception {
        int index =
                (int)getField(
                        "currentStepIndex"
                );
        assertEquals(
                -1,
                index
        );
    }
}