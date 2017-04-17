/*
 * Copyright (C) 2017 Ruslan Jankurazov, Dmitry Ussoltsev - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.core.gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import net.protium.Protium;
import net.protium.api.exceptions.NotFoundException;
import net.protium.api.utils.Constant;
import net.protium.api.utils.JSONParser;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

class ModuleView {
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty status = new SimpleStringProperty();
    private static Logger logger = null;

    ModuleView(String name, String status) {
        if (logger == null) {
            logger = Logger.getLogger(ModuleView.class.getName());
            try {
                logger.addHandler(new FileHandler(Constant.LOG_DIR + getClass() + Constant.LOG_EXT));
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Failed to create FileHandler", e);
            }
        }
        this.name.setValue(name);
        this.status.setValue(status);
    }

    public StringProperty statusProperty() {
        return status;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getName() {
        JSONParser config = Protium.manager.getModuleConfig(name.getValue());
        if (config == null) {
            return "null";
        }
        if (config.checkPath("name")) {
            return config.getString("name");
        } else {
            return "null";
        }
    }

    public String getVersion() {
        JSONParser config = Protium.manager.getModuleConfig(name.getValue());
        if (config == null) {
            return "null";
        }
        if (config.checkPath("version")) {
            return config.getString("version");
        } else {
            return "null";
        }
    }

    public String getAuthor() {
        JSONParser config = Protium.manager.getModuleConfig(name.getValue());
        if (config == null) {
            return "null";
        }
        if (config.checkPath("author")) {
            return config.getString("author");
        } else {
            return "null";
        }
    }

    public String getDescription() {
        JSONParser config = Protium.manager.getModuleConfig(name.getValue());
        if (config == null) {
            return "null";
        }
        if (config.checkPath("description")) {
            return config.getString("description");
        } else {
            return "null";
        }
    }

    public boolean getStatus() {
        return Protium.manager.getStatus(name.getValue());
    }
}
