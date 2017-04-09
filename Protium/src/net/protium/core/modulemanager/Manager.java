/*
 * Copyright (C) 2017 Ruslan Jankurazov, Dmitry Ussoltsev - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.core.modulemanager;


import net.protium.api.agents.CoreAgent;
import net.protium.api.agents.ModuleManager;
import net.protium.api.module.Module;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Manager implements ModuleManager {

    private Map<String, Module> Modules;

    Manager() {
        CoreAgent.setModuleManager(this);
        Modules = new HashMap<>();
        loadNotExistingModules();
    }

    public void loadNotExistingModules() {

    }

    public void unloadModule(String name) {

    }

    public void loadModule(String name) {

    }

    public void reloadModule(String name) {

    }

    @Override
    public Module getModule(String name) {
        return Modules.get(name);
    }
}
