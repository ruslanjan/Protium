/*
 * Copyright (C) 2017 Protium - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.api.agents;

import net.protium.api.agents.Config;

public final class CoreAgent {
    private CoreAgent() {}

    private static ModuleManager manager = null;

    public static ModuleManager getModuleManager() {
        return manager;
    }

    /**
     * do not run this method!!!
     * @param mg
     */
    public static void setModuleManager(ModuleManager mg) {
        if (manager == null)
            manager = mg;
    }
}
