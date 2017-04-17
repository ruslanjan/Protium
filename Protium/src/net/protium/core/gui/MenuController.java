/*
 * Copyright (C) 2017 Ruslan Jankurazov, Dmitry Ussoltsev - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.core.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import net.protium.Protium;
import net.protium.api.exceptions.NotFoundException;
import net.protium.api.module.Module;
import net.protium.api.utils.Constant;
import net.protium.api.utils.Pair;
import org.apache.tools.ant.taskdefs.condition.Not;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class MenuController {

    private static Logger logger = null;

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

    @FXML
    private Button enableButton;

    @FXML
    private Button disableButton;

    private ModuleView curModule;

    private ObservableList<ModuleView> list = FXCollections.observableArrayList();

    private MainApp mainApp;

    private Map<String, ModuleView> moduleViewMap;

    @FXML
    private void initialize() {
        if (logger == null) {
            logger = Logger.getLogger(ModuleView.class.getName());
            try {
                logger.addHandler(new FileHandler(Constant.LOG_DIR + getClass() + Constant.LOG_EXT));
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Failed to create FileHandler", e);
            }
        }

        moduleViewMap = new HashMap<>();

        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());

        showModuleDetails(null);

        moduleTableView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> showModuleDetails(newValue));
        initializeButtons();
    }

    private void initializeButtons() {
        enableButton.setOnAction(this::onEnable);
        disableButton.setOnAction(this::onDisable);
    }

    private void onEnable(ActionEvent event) {
        enableButton.setDisable(true);
        disableButton.setDisable(true);
        try {
            Protium.manager.enableModule(curModule.nameProperty().getValue());
        } catch (NotFoundException e) {
            logger.log(Level.SEVERE, "Failed to enable Module", e);
            enableButton.setDisable(false);
            disableButton.setDisable(true);
            return;
        }
        disableButton.setDisable(false);
    }

    private void onDisable(ActionEvent event) {
        enableButton.setDisable(true);
        disableButton.setDisable(true);
        try {
            Protium.manager.disableModule(curModule.nameProperty().getValue());
        } catch (NotFoundException e) {
            logger.log(Level.SEVERE, "Failed to enable Module", e);
            enableButton.setDisable(true);
            disableButton.setDisable(false);
            return;
        }
        enableButton.setDisable(false);
    }

    void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        reloadModuleList();
    }

    public void reloadModuleList() {
        moduleViewMap.clear();
        Collection<Pair<String, String>> collection = Protium.manager.getModulesAsString();
        list = FXCollections.observableArrayList();
        ModuleView moduleView = null;
        for (Pair<String, String> pr:collection) {
            moduleView = new ModuleView(pr.getLeft(), pr.getRight());
            moduleViewMap.put(pr.getLeft(), moduleView);
            list.add(moduleView);
        }
        moduleTableView.setItems(list);
    }

    public void setModuleViewStatus(String name, String status) throws NotFoundException {
        if (!moduleViewMap.containsKey(name)) {
            throw new NotFoundException();
        }
        moduleViewMap.get(name).setStatus(status);
    }

    private void showModuleDetails(ModuleView module) {
        if (module != null) {
            nameLable.setText(module.getName());
            versionLable.setText(module.getVersion());
            authorLable.setText(module.getAuthor());
            description.setText(module.getDescription());
            enableButton.setDisable(module.getStatus());
            disableButton.setDisable(!module.getStatus());
            curModule = module;
        } else {
            nameLable.setText("null");
            versionLable.setText("null");
            authorLable.setText("null");
            description.setText("null");
            enableButton.setDisable(true);
            disableButton.setDisable(true);
        }
    }
}
