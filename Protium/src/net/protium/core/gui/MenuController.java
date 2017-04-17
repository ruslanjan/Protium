/*
 * Copyright (C) 2017 Ruslan Jankurazov, Dmitry Ussoltsev - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.core.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import net.protium.Protium;
import net.protium.api.module.Module;
import net.protium.api.utils.Pair;

import java.util.Collection;

public final class MenuController {

    @FXML
    private TableView<ModuleView> moduleTableView;

    @FXML
    private TableColumn<ModuleView, String> nameColumn;

    @FXML
    private TableColumn<ModuleView, String> statusColumn;

    @FXML
    private Label nameLable;

    @FXML
    private Label versionLable;

    @FXML
    private Label authorLable;

    @FXML
    private TextArea description;


    private ObservableList<ModuleView> list = FXCollections.observableArrayList();

    private MainApp mainApp;

    @FXML
    private void initialize() {
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());

        showModuleDetails(null);

        moduleTableView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> showModuleDetails(newValue));
    }

    void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        reloadModuleList();
    }

    public void reloadModuleList() {
        Collection<Pair<String, String>> collection = Protium.manager.getModulesAsString();
        list = FXCollections.observableArrayList();
        for (Pair<String, String> pr:collection) {
            list.add(new ModuleView(pr.getLeft(), pr.getRight()));
        }
        moduleTableView.setItems(list);
    }


    private void showModuleDetails(ModuleView module) {
        if (module != null) {
            nameLable.setText(module.getName());
            versionLable.setText(module.getVersion());
            authorLable.setText(module.getAuthor());
            description.setText(module.getDescription());
        } else {
            nameLable.setText("null");
            versionLable.setText("null");
            authorLable.setText("null");
            description.setText("null");
        }
    }
}
