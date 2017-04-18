/*
 * Copyright (C) 2017 Protium - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.core.gui;

public class GUIThread implements Runnable {
    @Override
    public void run() {
        javafx.application.Application.launch(MainApp.class);
    }
}
