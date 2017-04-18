/*
 * Copyright (C) 2017 Protium - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.api.agents;

import net.protium.api.exceptions.NotFoundException;
import net.protium.api.module.IModule;
import net.protium.api.utils.Pair;

import java.net.URL;
import java.util.Collection;

public interface ModuleManager {
    String getExtendedStatus(String name);

    IModule getModule(String name) throws NotFoundException;

    void reloadModules();

    boolean getStatus(String name);

    void enableModule(String name) throws NotFoundException;

    void disableModule(String name) throws NotFoundException;

    URL getModuleResourceURL(String name, String path) throws NotFoundException;

    void setModuleViewStatus(String name, String status) throws NotFoundException;

    Collection<Pair<String, String>> getModulesAsString();
}
