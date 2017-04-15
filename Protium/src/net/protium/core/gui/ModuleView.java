/*
 * Copyright (C) 2017 Ruslan Jankurazov, Dmitry Ussoltsev - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.core.gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

class ModuleView {
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty status = new SimpleStringProperty();

    ModuleView(String name, Boolean status) {
        this.name.setValue(name);
        if (status) {
            this.status.setValue("ON");
        } else {
            this.status.setValue("OFF");
        }
    }

    public StringProperty statusProperty() {
        return status;
    }

    public StringProperty nameProperty() {
        return name;
    }
}
