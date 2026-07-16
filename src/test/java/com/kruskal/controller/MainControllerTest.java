package com.kruskal.controller;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class MainControllerTest {

    private Object getField(Object object, String fieldName) throws Exception {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(object);
    }


    @Test
    void controllerShouldBeCreated() {
        MainController controller = new MainController();

        assertNotNull(controller);
    }


    @Test
    void fieldsShouldBeNullBeforeInitialize() throws Exception {
        MainController controller = new MainController();

        assertNull(
                getField(controller, "graphManager")
        );

        assertNull(
                getField(controller, "logger")
        );

        assertNull(
                getField(controller, "autoPlayer")
        );

        assertNull(
                getField(controller, "uiStateManager")
        );

        assertNull(
                getField(controller, "playbackCoordinator")
        );
    }


    @Test
    void controllerShouldHaveRequiredFields() {

        assertDoesNotThrow(() -> {

            MainController.class.getDeclaredField("graphManager");
            MainController.class.getDeclaredField("logger");
            MainController.class.getDeclaredField("autoPlayer");
            MainController.class.getDeclaredField("uiStateManager");
            MainController.class.getDeclaredField("playbackCoordinator");

        });

    }


    @Test
    void controllerShouldNotContainOldFields() {

        assertThrows(
                NoSuchFieldException.class,
                () -> MainController.class.getDeclaredField("currentGraph")
        );

        assertThrows(
                NoSuchFieldException.class,
                () -> MainController.class.getDeclaredField("steps")
        );

        assertThrows(
                NoSuchFieldException.class,
                () -> MainController.class.getDeclaredField("currentStepIndex")
        );

    }
}