/*
 * Copyright (C) 2017 Ruslan Jankurazov, Dmitry Ussoltsev - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.core;


import net.protium.core.modulemanager.Manager;

import java.io.IOException;

public class Protium {
    public static void main(String[] args) {
        Manager mn = new Manager();
        try {
            mn.loadModule("testModule");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }
}
