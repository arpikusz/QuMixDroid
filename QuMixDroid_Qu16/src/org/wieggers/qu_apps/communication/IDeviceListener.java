/*******************************************************************************
 * Copyright (c) 2013 george wieggers.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     george wieggers - initial API and implementation
 ******************************************************************************/
package org.wieggers.qu_apps.communication;

public interface IDeviceListener {
	void receivedMessage(byte[] message);
	void errorOccurred(Exception exception);
}
