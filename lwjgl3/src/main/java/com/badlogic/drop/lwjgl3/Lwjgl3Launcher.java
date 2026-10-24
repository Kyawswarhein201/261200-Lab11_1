
package com.badlogic.drop.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.drop.Main;

public class Lwjgl3Launcher {

    public static void main(String[] args) {
        createApplication();
    }

    private static Lwjgl3Application createApplication() {
        return new Lwjgl3Application(
            new Main(),
            getDefaultConfiguration()
        );
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {

        Lwjgl3ApplicationConfiguration configuration =
            new Lwjgl3ApplicationConfiguration();

        // Window title
        configuration.setTitle("Drop");

        // Window size: 800 x 500
        configuration.setWindowedMode(800, 500);

        // Enable vertical synchronization
        configuration.useVsync(true);

        // Set foreground frame rate
        configuration.setForegroundFPS(60);

        return configuration;
    }
}
