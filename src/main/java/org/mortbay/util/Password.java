package org.mortbay.util;

/**
 * $Id$ Copyright 1998-2004 Mort Bay Consulting Pty. Ltd.
 * ------------------------------------------------------------------------
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 * ========================================================================
 */

/* ------------------------------------------------------------ */
/**
 * Password utility class. This utility class gets a password or pass phrase
 * either by:
 * 
 * <PRE>
 * + Password is set as a system property. + The password is prompted for
 * and read from standard input + A program is run to get the password.
 * 
 * </pre>
 * 
 * Passwords that begin with OBF: are de obfuscated. Passwords can be obfuscated
 * by run org.mortbay.util.Password as a main class. Obfuscated password are
 * required if a system needs to recover the full password (eg. so that it may
 * be passed to another system). They are not secure, but prevent casual
 * observation.
 * <p>
 * Passwords that begin with CRYPT: are one way encrypted with UnixCrypt. The
 * real password cannot be retrieved, but comparisons can be made to other
 * passwords. A Crypt can be generated by running org.mortbay.util.UnixCrypt as
 * a main class, passing password and then the user name. Checksum passwords are
 * a secure(ish) way to store passwords that only need to be checked rather than
 * recovered. Note that it is not strong security - specially if simple
 * passwords are used.
 * 
 * @version $Id$
 * @author Greg Wilkins (gregw)
 */
public final class Password {

	/**
	 * Create a new instance of this class.
	 */
	private Password() {
		super();
	}

	/**
	 * Prefix of obfuscated login+password string.
	 */
	public static final String OBFUSCATE_STR = "OBF|";

	/**
	 * This method change the specific string into another one more difficult to
	 * read
	 * 
	 * @param s
	 *          the original string.
	 * @return the obfuscated string.
	 */
	public static String obfuscate(String s) {
		StringBuffer buf = new StringBuffer();
		byte[] b = s.getBytes();

		synchronized (buf) {
			buf.append(OBFUSCATE_STR);
			for (int i = 0; i < b.length; i++) {
				byte b1 = b[i];
				byte b2 = b[s.length() - (i + 1)];
				int i1 = b1 + b2 + 127;
				int i2 = b1 - b2 + 127;
				int i0 = i1 * 256 + i2;
				String x = Integer.toString(i0, 36);

				switch (x.length()) {
				case 1:
					buf.append('0');
					break;
				case 2:
					buf.append('0');
					break;
				case 3:
					buf.append('0');
					break;
				default:
					buf.append(x);
				}
			}
			return buf.toString();
		}
	}

	/**
	 * This method return the hided string behind the specified string. If the
	 * string is not hided, it returns it.
	 * 
	 * @param s
	 *          the hided string.
	 * @return the hided string behind the specified string. If the string is not
	 *         hided, it returns it.
	 */
	public static String deobfuscate(String s) {
		if (!s.startsWith(OBFUSCATE_STR)) {
			// no need to de obfuscate
			return s;
		}

		final String x = s.substring(OBFUSCATE_STR.length());
		final byte[] b = new byte[x.length() / 2];
		int l = 0;
		for (int i = 0; i < x.length(); i += 4) {
			String xi = x.substring(i, i + 4);
			int i0 = Integer.parseInt(xi, 36);
			int i1 = i0 / 256;
			int i2 = i0 % 256;
			b[l++] = (byte) ((i1 + i2 - 254) / 2);
		}
		return new String(b, 0, l);
	}
}
