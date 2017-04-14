/*
 * Copyright (C) 2017 Ruslan Jankurazov, Dmitry Ussoltsev - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.core.http;

import net.protium.api.events.Response;
import net.protium.api.exceptions.NotFoundException;
import net.protium.core.modulemanagement.Manager;
import net.protium.core.utils.Constant;
import net.protium.core.utils.Functions;
import net.protium.core.utils.JSONParser;
import net.protium.core.utils.Pair;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Router_OLD {
	private Manager manager;
	private Map < String, List < Pair < String, String > > > routes;

	public Router_OLD(Manager manager) {
		Logger logger = Logger.getLogger(this.getClass().getName());
		try {
			logger.addHandler((new FileHandler(
				Functions.createFile(Constant.LOG_D, this.getClass().getName(), Constant.LOG_EXT))));
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Failed to write logs", e);
		}
		this.manager = manager;
		reloadRoutes();
	}

	@SuppressWarnings("WeakerAccess")
	public void reloadRoutes( ) {
		this.routes = new HashMap <>();
		String[] paths = Functions.listFiles(Constant.ROUTES_D, Constant.CONF_EXT);
		for (String path : paths) {
			JSONParser jsonParser;
			jsonParser = new JSONParser((new File(path)));
			@SuppressWarnings("unchecked")
			Map < String, Map < String, String > > map = (Map < String, Map < String, String > >) jsonParser.get();
			for (Map.Entry < String, Map < String, String > > mapEntry : map.entrySet()) {
				List < Pair < String, String > > arr = new ArrayList <>();

				for (Map.Entry < String, String > route : mapEntry.getValue().entrySet()) {
					arr.add(new Pair <>(route.getKey(), route.getValue()));
				}

				routes.put(mapEntry.getKey(), arr);
			}
		}
	}

	public Response perform(HTTPRequest data) throws NotFoundException {
		String url = data.getURL();
		for (Map.Entry < String, List < Pair < String, String > > > entry : routes.entrySet()) {
			for (Pair < String, String > pattern : entry.getValue()) {
				if (Functions.matchRegex(pattern.getLeft(), url)) {
					data.setAction(pattern.getRight());
					return manager.getModule(entry.getKey()).onRequest(data);
				}
			}
		}
		throw new NotFoundException();
	}
}
