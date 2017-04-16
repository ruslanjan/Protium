/*
 * Copyright (C) 2017 Ruslan Jankurazov, Dmitry Ussoltsev - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.core.storage

import net.protium.core.utils.Constant

import java.nio.file.Files
import java.nio.file.Paths

/**
 * From: protium
 * Pkg: net.protium.core.storage
 * At: 13.04.17
 */
@SuppressWarnings("GroovyUnusedDeclaration")
final class StorageManager {

    static Resource getResource(String name) { new Resource(name) }

    static void createResource(String name, byte[] data) {
        String path = (Constant.RES_DIR + name)
        if (!Files.exists(Paths.get(path))) {
            Files.createFile(Paths.get(path))
        }
        File file = new File(path)
        file.write(new String(data))
    }

    static void createResource(String name, String data) {
        String path = (Constant.RES_DIR + name)
        if (!Files.exists(Paths.get(path))) {
            Files.createFile(Paths.get(path))
        }
        File file = new File(path)
        file.setWritable(true)
        file.write(data)
    }

    static void createResource(String name, File data) {
        String path = (Constant.RES_DIR + name)
        if (!Files.exists(Paths.get(path))) {
            Files.createFile(Paths.get(path))
        }
        File file = new File(path)
        file.write(data.text)
    }
}
