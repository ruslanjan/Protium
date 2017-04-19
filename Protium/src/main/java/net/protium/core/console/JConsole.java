/*
 * Copyright (C) 2017 Protium - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.core.console;


import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;


public class JConsole implements Runnable {

	private static final String PROMPT = " ~ $ ";
	private static final String HEADER = "Protium/Console\n";
	protected final ArrayList < Command > commandHistory = new ArrayList <>();
	private final Scanner scanner;
	private CommandList commandList = null;

	public JConsole(CommandList list) {
		commandList = list;
		scanner = new Scanner(System.in);
	}

	private Command parse(String rawCommand) {
		rawCommand = rawCommand.replaceAll("(\"[\\s\\S]*?\"|[\\S]+)\\s*", "$1\u0001");
		rawCommand = rawCommand.replaceAll("\"([\\s\\S]*?)\"", "$1");
		String[] rawList = rawCommand.split("\u0001");

		ArrayList < String > args = new ArrayList <>(Arrays.asList(rawList));

		String command = args.remove(0).trim();

		return new Command(command, rawCommand, args);
	}

	private Object execute(Command command) throws InvocationTargetException, IllegalAccessException {
		Executable executable = commandList.get(command.getName());

		if (executable == null) {
			return "Can't find command: '" + command.getName() + "'";
		}

		return executable.invoke(command.getArgs().toArray());
	}

	protected void prompt( ) {
		System.err.println();
		System.err.print(PROMPT);

		Command command = parse(getInputLine());

		commandHistory.add(command);

		Object result;
		try {
			result = execute(command);
		} catch (InvocationTargetException | IllegalAccessException e) {
			System.err.println("Can not execute command '" + command.getName() + "'!\n\tReason: " + e.getMessage());
			return;
		}

		if (result != null)
			System.err.println(result.toString());
	}

	protected String getInputLine( ) {
		return scanner.nextLine();
	}

	public void run( ) {
		System.err.println(HEADER);

		//noinspection InfiniteLoopStatement
		while (true) {
			prompt();
		}
	}
}
