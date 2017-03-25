package com.lin.health.uart.domain;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

@Root
public class Command {
	public enum Icon {
		LEFT(0),
		UP(1),
		RIGHT(2),
		DOWN(3),
		SETTINGS(4),
		REW(5),
		PLAY(6),
		PAUSE(7),
		STOP(8),
		FWD(9),
		INFO(10),
		NUMBER_1(11),
		NUMBER_2(12),
		NUMBER_3(13),
		NUMBER_4(14),
		NUMBER_5(15),
		NUMBER_6(16),
		NUMBER_7(17),
		NUMBER_8(18),
		NUMBER_9(19);

		public final int index;

		Icon(final int index) {
			this.index = index;
		}
	}

	public enum Eol {
		LF(0),
		CR(1),
		CR_LF(2);

		public final int index;

		Eol(final int index) {
			this.index = index;
		}
	}

	@Text(required = false)
	private String command;

	@Attribute(required = false)
	private boolean active = false;

	@Attribute(required = false)
	private Eol eol = Eol.LF;

	@Attribute(required = false)
	private Icon icon = Icon.LEFT;

	/**
	 * Sets the command.
	 * @param command the command that will be sent to UART device
	 */
	public void setCommand(final String command) {
		this.command = command;
	}

	/**
	 * Sets whether the command is active.
	 * @param active true to make it active
	 */
	public void setActive(final boolean active) {
		this.active = active;
	}

	/**
	 * Sets the new line type.
	 * @param eol end of line terminator
	 */
	public void setEol(final int eol) {
		this.eol = Eol.values()[eol];
	}

	/**
	 * Sets the icon index.
	 * @param index index of the icon.
	 */
	public void setIconIndex(final int index) {
		this.icon = Icon.values()[index];
	}

	/**
	 * Returns the command that will be sent to UART device.
	 * @return the command
	 */
	public String getCommand() {
		return command;
	}

	/**
	 * Returns whether the icon is active.
	 * @return true if it's active
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * Returns the new line type.
	 * @return end of line terminator
	 */
	public Eol getEol() {
		return eol;
	}

	/**
	 * Returns the icon index.
	 * @return the icon index
	 */
	public int getIconIndex() {
		return icon.index;
	}
	/**
	 * Returns the EOL index.
	 * @return the EOL index
	 */
	public int getEolIndex() {
		return eol.index;
	}
}
