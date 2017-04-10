/*
 * Copyright (C) 2017 Ruslan Jankurazov, Dmitry Ussoltsev - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.api.agents;

public final class CoreAgent {
    private CoreAgent() {}

    private static ModuleManager manager = null;

    public static ModuleManager getModuleManager() {
        return manager;
    }

    public static void setModuleManager(ModuleManager mg) {
        if (manager == null)
            manager = mg;
    }
}
