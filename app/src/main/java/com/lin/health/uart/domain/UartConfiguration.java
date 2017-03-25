
package com.lin.health.uart.domain;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementArray;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.core.PersistenceException;
import org.simpleframework.xml.core.Validate;

@Root
public class UartConfiguration {
	public static final int COMMANDS_COUNT = 9;

	@Attribute(required = false, empty = "Unnamed")
	private String name;

	@ElementArray
	private Command[] commands = new Command[COMMANDS_COUNT];

	/**
	 * Returns the field name
	 *
	 * @return optional name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name to specified value
	 * @param name the new name
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * Returns the array of commands. There is always 9 of them.
	 * @return the commands array
	 */
	public Command[] getCommands() {
		return commands;
	}

	@Validate
	private void validate() throws PersistenceException{
		if (commands == null || commands.length != COMMANDS_COUNT)
			throw new PersistenceException("There must be always " + COMMANDS_COUNT + " commands in a configuration.");
	}
}
