/*
 * Created on 4 f�vr. 2005
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
package net.sf.firemox.action.targetlist;

import java.io.IOException;
import java.io.InputStream;

import net.sf.firemox.clickable.ability.Ability;
import net.sf.firemox.event.context.ContextEventListener;
import net.sf.firemox.stack.StackManager;

/**
 * @author <a href="mailto:fabdouglas@users.sourceforge.net">Fabrice Daugan </a>
 * @since 0.82
 */
class TargetListRemoveList extends TargetListRemove {

	/**
	 * Create an instance of TargetListRemoveList by reading a file Offset's file
	 * must pointing on the first byte of this action <br>
	 * <ul>
	 * Structure of InputStream : Data[size]
	 * <li>[super]</li>
	 * </ul>
	 * 
	 * @param inputFile
	 *          file containing this action
	 * @throws IOException
	 *           If some other I/O error occurs
	 */
	TargetListRemoveList(InputStream inputFile) throws IOException {
		super(inputFile);
	}

	@Override
	public boolean play(ContextEventListener context, Ability ability) {
		final int listIndex = getlistIndex(ability, context);
		if (listIndex < 0) {
			StackManager.SAVED_TARGET_LISTS.remove(StackManager.SAVED_TARGET_LISTS
					.size() - 1);
		} else {
			StackManager.SAVED_TARGET_LISTS.remove(listIndex);
		}
		return true;
	}

	@Override
	public String toString(Ability ability) {
		return "target-list.remove-list";
	}

}
