/*
 * Copyright (C) 2017 Protium - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.api.agents;

import net.protium.api.module.Module;
import net.protium.api.utils.Pair;

import java.net.URL;
import java.util.Collection;

public interface ModuleManager {
    String getExtendedStatus(String name);

    Module getModule(String name);

    void reloadModules();

    boolean getStatus(String name);

    void enableModule(String name);

    void disableModule(String name);

    URL getModuleResourceURL(String name, String path);

    void setModuleExtendedStatus(String name, String status);

    Collection<Pair<String, String>> getModulesAsString();
}
