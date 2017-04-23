/*
 * Copyright (C) 2017 - Protium - Ussoltsev Dmitry, Jankurazov Ruslan - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package net.protium.core.util;
/*
In net.protium.core.util
From temporary-protium
*/

import javafx.scene.control.TextArea;

import java.io.IOException;
import java.io.OutputStream;

public class ErrCLI extends OutputStream {
	private TextArea output;

	public ErrCLI(TextArea ta) {
		this.output = ta;
	}

	@Override
	public void write(int i) throws IOException {
		output.appendText(String.valueOf((char) i));
	}
}
