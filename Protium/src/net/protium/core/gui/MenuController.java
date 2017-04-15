/*
 * Copyright (C) 2017 Ruslan Jankurazov, Dmitry Ussoltsev - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.core.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import net.protium.Protium;
import net.protium.core.utils.Pair;

import java.util.Collection;

public final class MenuController {

    @FXML
    private TableView<ModuleView> moduleTableView;

    @FXML
    private TableColumn<ModuleView, String> nameColumn;

    @FXML
    private TableColumn<ModuleView, String> statusColumn;

    private ObservableList<ModuleView> list = FXCollections.observableArrayList();

    private MainApp mainApp;

    @FXML
    private void initialize() {
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
    }

    void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        reloadModuleList();
    }

    public void reloadModuleList() {
        Collection<Pair<String, Boolean>> collection = Protium.manager.getModulesAsString();
        list = FXCollections.observableArrayList();
        for (Pair<String, Boolean> pr:collection) {
            list.add(new ModuleView(pr.getLeft(), pr.getRight()));
        }
        moduleTableView.setItems(list);
    }
}
