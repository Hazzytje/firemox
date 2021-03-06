/*
 * Created on 5 f�vr. 2005
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
 * 
 */
package net.sf.firemox.operation;

import java.io.IOException;
import java.io.InputStream;

import net.sf.firemox.expression.Expression;
import net.sf.firemox.expression.intlist.Counter;
import net.sf.firemox.expression.intlist.First;
import net.sf.firemox.expression.intlist.IndexOf;
import net.sf.firemox.expression.intlist.Last;
import net.sf.firemox.expression.intlist.LastIndexOf;
import net.sf.firemox.expression.intlist.Maximum;
import net.sf.firemox.expression.intlist.Minimum;
import net.sf.firemox.expression.intlist.Size;
import net.sf.firemox.expression.intlist.Sum;

/**
 * Reprsent an operation applied on an integer list.
 * 
 * @author <a href="mailto:fabdouglas@users.sourceforge.net">Fabrice Daugan </a>
 */
public final class IntList extends Operation {

	/**
	 * Creates a new instance of IntValue <br>
	 */
	private IntList() {
		super();
	}

	@Override
	public Expression readNextExpression(InputStream inputFile)
			throws IOException {
		final IdOperation idOperation = IdOperation.deserialize(inputFile);
		switch (idOperation) {
		case ADD:
			return new Sum(inputFile);
		case COUNTER:
			return new Counter(inputFile);
		case INDEX_OF:
			return new IndexOf(inputFile);
		case LAST_INDEX_OF:
			return new LastIndexOf(inputFile);
		case SIZE:
			return new Size(inputFile);
		case MAXIMUM:
			return new Maximum(inputFile);
		case MINIMUM:
			return new Minimum(inputFile);
		case FIRST:
			return new First(inputFile);
		case LAST:
			return new Last(inputFile);
		default:
			throw new InternalError("Unknown expression operation for int-list:"
					+ idOperation);
		}
	}

	@Override
	public int process(int leftValue, int rightValue) {
		throw new InternalError("should not be called");
	}

	@Override
	public String getOperator() {
		return "int-list value";
	}

	/**
	 * Return the unique instance of this operation.
	 * 
	 * @return the unique instance of this operation.
	 */
	public static Operation getInstance() {
		if (instance == null)
			instance = new IntList();
		return instance;
	}

	/**
	 * The unique instance of this operation
	 */
	private static Operation instance = null;
}
