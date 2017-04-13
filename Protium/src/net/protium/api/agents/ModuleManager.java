/*
 * Copyright (C) 2017 Ruslan Jankurazov, Dmitry Ussoltsev - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.api.agents;

import net.protium.api.exceptions.NotFoundException;
import net.protium.api.module.Module;
import org.omg.CosNaming.NamingContextPackage.NotFound;

public interface ModuleManager {
    Module getModule(String name) throws NotFoundException;

    void reloadModules();

    boolean getStatus(String name);

    void enableModule(String name) throws NotFoundException;

    void disableModule(String name) throws NotFoundException;

}
