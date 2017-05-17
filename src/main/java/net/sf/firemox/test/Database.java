/*
 *   Firemox is a turn based strategy simulator
 *   Copyright (C) 2003-2007 Fabrice Daugan
 *
 *   This program is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU General Public License as published by the Free 
 * Software Foundation; either version 2 of the License, or (at your option) any
 * later version.
 *
 *   This program is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
 * details.
 *
 *   You should have received a copy of the GNU General Public License along  
 * with this program; if not, write to the Free Software Foundation, Inc., 
 * 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package net.sf.firemox.test;

import java.io.IOException;
import java.io.InputStream;

import net.sf.firemox.clickable.ability.Ability;
import net.sf.firemox.clickable.target.card.MCard;
import net.sf.firemox.tools.MToolKit;

/**
 * Database property/value test.
 * 
 * @author <a href="mailto:fabdouglas@users.sourceforge.net">Fabrice Daugan </a>
 * @since 0.94
 */
class Database extends TestCard {

	/**
	 * Create an instance of this class by reading a file. Offset's file must
	 * pointing on the first byte of this test <br>
	 * <ul>
	 * Structure of InputStream : Data[size]
	 * <li>property name [String]</li>
	 * <li>property value [String]</li>
	 * </ul>
	 * 
	 * @param inputFile
	 *          is the file containing this event
	 * @throws IOException
	 *           if error occurred during the reading process from the specified
	 *           input stream
	 */
	Database(InputStream inputFile) throws IOException {
		super(inputFile);
		propertyName = MToolKit.readString(inputFile).intern();
		propertyValue = MToolKit.readString(inputFile).intern();
	}

	/**
	 * Return the result of test beetwen left and right op applied on the
	 * specified card.
	 * 
	 * @param ability
	 *          is the ability owning this test. The card component of this
	 *          ability should correspond to the card owning this test too.
	 * @param tested
	 *          the tested card
	 * @return true if the specified card matches with the test to do
	 */
	@Override
	protected boolean testCard(Ability ability, MCard tested) {
		final String propertyValue = on.getCard(ability, tested).getDatabase()
				.getProperty(propertyName);
		return propertyValue != null
				&& this.propertyValue.equalsIgnoreCase(propertyValue);
	}

	/**
	 * The property name.
	 */
	private final String propertyName;

	/**
	 * The property value.
	 */
	private final String propertyValue;
}
