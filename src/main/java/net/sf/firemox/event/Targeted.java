/*
 * Targeted.java 
 * Created on 29 janv. 2004
 * 
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
package net.sf.firemox.event;

import java.io.IOException;
import java.io.InputStream;

import net.sf.firemox.clickable.ability.Ability;
import net.sf.firemox.clickable.target.Target;
import net.sf.firemox.clickable.target.card.MCard;
import net.sf.firemox.event.context.MContextTarget;
import net.sf.firemox.test.Test;
import net.sf.firemox.test.TestFactory;

/**
 * When this a component is targeted. The 2 attached tests are applied on the
 * source and the destination. The context contains the source and the
 * destination, and can be acceded with the 'context.card' or 'context.player'
 * for the destination, and 'context.card2' register for the source.
 * 
 * @author <a href="mailto:fabdouglas@users.sourceforge.net">Fabrice Daugan </a>
 * @since 0.54
 */
public class Targeted extends TriggeredEvent {

	/**
	 * Create an instance of MEventListener by reading a file Offset's file must
	 * pointing on the first byte of this event <br>
	 * <ul>
	 * Structure of InputStream : Data[size]
	 * <li>idZone [1]</li>
	 * <li>test to apply on source [...]</li>
	 * <li>test to apply on destination [...]</li>
	 * </ul>
	 * 
	 * @param inputFile
	 *          is the file containing this event
	 * @param card
	 *          is the card owning this event
	 * @throws IOException
	 *           if error occurred during the reading process from the specified
	 *           input stream
	 */
	Targeted(InputStream inputFile, MCard card) throws IOException {
		super(inputFile, card);
		testTarget = TestFactory.readNextTest(inputFile);
	}

	/**
	 * Creates a new instance of CanICast specifying all attributes of this class.
	 * All parameters are copied, not cloned. So this new object shares the card
	 * and the specified codes
	 * 
	 * @param idZone
	 *          the place constraint to activate this event
	 * @param test
	 *          the additional test of this event
	 * @param card
	 *          is the card owning this card
	 */
	private Targeted(int idPlace, Test test, MCard card, Test testTarget) {
		super(idPlace, test, card);
		this.testTarget = testTarget;
	}

	/**
	 * Return a copy of this with the specified owner
	 * 
	 * @param card
	 *          is the card of the ability of this event
	 * @return copy of this event
	 */
	@Override
	public MEventListener clone(MCard card) {
		return new Targeted(idZone, test, card, testTarget);
	}

	/**
	 * Tell if the current event matches with this event. If there is an
	 * additional code to check, it'would be checked if the main event matches
	 * with the main event
	 * 
	 * @param ability
	 *          is the ability owning this test. The card component of this
	 *          ability should correspond to the card owning this test too.
	 * @param source
	 *          the source of the spell/ability
	 * @param target
	 *          the targeted player/card
	 * @return true if the current event match with this event
	 */
	public boolean isMatching(Ability ability, MCard source, Target target) {
		return test.test(ability, source) && testTarget.test(ability, target);
	}

	/**
	 * Dispatch this event to all active event listeners able to understand this
	 * event. The listening events able to understand this event are <code>this
	 * </code>
	 * and other multiple event listeners. For each event listeners having
	 * responded they have been activated, the corresponding ability is added to
	 * the triggered buffer zone of player owning this ability
	 * 
	 * @param source
	 *          source of target action
	 * @param target
	 *          the targeted card
	 * @see #isMatching(Ability, MCard, Target)
	 */
	public static void dispatchEvent(MCard source, Target target) {
		for (Ability ability : TRIGGRED_ABILITIES.get(EVENT)) {
			if (((Targeted) ability.eventComing())
					.isMatching(ability, source, target)) {
				ability.triggerIt(new MContextTarget(target));
			}
		}
	}

	@Override
	public final Event getIdEvent() {
		return EVENT;
	}

	/**
	 * The event type.
	 */
	public static final Event EVENT = Event.TARGETED;

	/**
	 * The test appied to the targeted
	 */
	private Test testTarget;
}