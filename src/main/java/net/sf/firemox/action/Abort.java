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
package net.sf.firemox.action;

import net.sf.firemox.action.context.ActionContextWrapper;
import net.sf.firemox.action.handler.FollowAction;
import net.sf.firemox.action.handler.StandardAction;
import net.sf.firemox.clickable.ability.Ability;
import net.sf.firemox.clickable.target.Target;
import net.sf.firemox.clickable.target.card.MCard;
import net.sf.firemox.event.context.ContextEventListener;
import net.sf.firemox.stack.StackManager;

/**
 * Cancel the target list (spell of stack).
 * 
 * @author <a href="mailto:fabdouglas@users.sourceforge.net">Fabrice Daugan </a>
 * @since 0.60
 */
final class Abort extends UserAction implements FollowAction, StandardAction {

	/**
	 */
	private Abort() {
		super((String) null);
	}

	@Override
	public Actiontype getIdAction() {
		return Actiontype.ABORT;
	}

	public void simulate(ActionContextWrapper actionContext,
			ContextEventListener context, Ability ability) {
		// Ignore that
	}

	public void rollback(ActionContextWrapper actionContext,
			ContextEventListener context, Ability ability) {
		// Ignore that since nothing done in simulate(..)
	}

	public boolean play(ContextEventListener context, Ability ability) {
		for (Target target : StackManager.getInstance().getTargetedList().list) {
			if (target.isCard()) {
				StackManager.getInstance().abortion((MCard) target, ability);
			}
		}
		return true;
	}

	@Override
	public String toString(Ability ability) {
		return "Abort";
	}

	/**
	 * The unique instance of this class
	 */
	public static Abort instance = new Abort();

}