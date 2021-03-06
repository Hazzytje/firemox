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
package net.sf.firemox.action.intlist;

import java.io.IOException;
import java.io.InputStream;

import net.sf.firemox.clickable.ability.Ability;
import net.sf.firemox.event.context.ContextEventListener;
import net.sf.firemox.stack.StackManager;
import net.sf.firemox.tools.IntegerList;

/**
 * @author <a href="mailto:fabdouglas@users.sourceforge.net">Fabrice Daugan </a>
 * @since 0.82
 */
class IntListOccurenceList extends IntListOccurence {

	/**
	 * Create an instance of IntListOccurence by reading a file Offset's file must
	 * pointing on the first byte of this action <br>
	 * <ul>
	 * Structure of InputStream : Data[size]
	 * <li>idAction [1]</li>
	 * <li>idType [1]</li>
	 * <li>list-index : Expression [...]</li>
	 * </ul>
	 * 
	 * @param inputFile
	 *          file containing this action
	 * @throws IOException
	 *           If some other I/O error occurs
	 */
	IntListOccurenceList(InputStream inputFile) throws IOException {
		super(inputFile);
	}

	@Override
	public boolean play(ContextEventListener context, Ability ability) {
		int listIndex = this.listIndex.getValue(ability, null, context);
		if (listIndex >= 0) {
			IntegerList checked = StackManager.SAVED_INT_LISTS.get(listIndex);
			int index = 0;
			int firstIndex = checked.indexOf(StackManager.intList.getInt(index++));
			int checkedIndex = firstIndex + 1;
			while (firstIndex < checked.size()) {
				while (index < StackManager.intList.size()
						&& checkedIndex < checked.size()) {
					if (checked.getInt(checkedIndex) == StackManager.intList
							.getInt(index)) {
						checkedIndex++;
						index++;
						continue;
					}
					while (++firstIndex < checked.size()) {
						// try to find the next occurrence
						if (checked.getInt(firstIndex) == StackManager.intList.getInt(0)) {
							// same head found
							break;
						}
					}
					if (firstIndex >= checked.size()) {
						// no more occurrence
						break;
					}
					index = 1;
					firstIndex++;
				}
				if (index >= StackManager.intList.size()) {
					// the full list has been found, add this occurrence index to int-list
					StackManager.intList.addInt(firstIndex++);
				}
			}
		}
		return true;
	}

	@Override
	public String toString(Ability ability) {
		return "int-list.occurrence-list";
	}
}
