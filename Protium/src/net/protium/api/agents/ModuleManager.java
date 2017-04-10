/*
 * Copyright (C) 2017 Ruslan Jankurazov, Dmitry Ussoltsev - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.api.agents;

import net.protium.api.exceptions.AlreadyLoadedException;
import net.protium.api.exceptions.NotLoadedException;
import net.protium.api.module.Module;

public interface ModuleManager {
    Module getModule(String name);

    void unloadModule(String name);

    void loadModule(String name);

    void reloadModule(String name);

    void enableModule(String name);

    void disableModule(String name);

    void restartModule(String name);
}
