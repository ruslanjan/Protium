/*
 * Copyright (C) 2017 Protium - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium;

import net.protium.core.gui.MainApp;
import net.protium.core.modules.management.Manager;

import java.net.*;

public class TestJava {
    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException, MalformedURLException {
        Manager manager = new Manager();
        Protium.manager = manager;
        javafx.application.Application.launch(MainApp.class);
    }
}
