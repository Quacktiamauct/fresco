/*******************************************************************************
 * Copyright (c) 2016 FRESCO (http://github.com/aicis/fresco).
 *
 * This file is part of the FRESCO project.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * FRESCO uses SCAPI - http://crypto.biu.ac.il/SCAPI, Crypto++, Miracl, NTL,
 * and Bouncy Castle. Please see these projects for any further licensing issues.
 *******************************************************************************/
package dk.alexandra.fresco.suite.ninja;

import dk.alexandra.fresco.framework.value.OBool;
import dk.alexandra.fresco.framework.value.SBool;
import dk.alexandra.fresco.lib.field.bool.AndProtocol;
import dk.alexandra.fresco.lib.field.bool.BasicLogicFactory;
import dk.alexandra.fresco.lib.field.bool.CloseBoolProtocol;
import dk.alexandra.fresco.lib.field.bool.NotProtocol;
import dk.alexandra.fresco.lib.field.bool.OpenBoolProtocol;
import dk.alexandra.fresco.lib.field.bool.XorProtocol;
import dk.alexandra.fresco.lib.logic.AbstractBinaryFactory;
import dk.alexandra.fresco.suite.ninja.protocols.NinjaANDProtocol;
import dk.alexandra.fresco.suite.ninja.protocols.NinjaCloseProtocol;
import dk.alexandra.fresco.suite.ninja.protocols.NinjaNOTProtocol;
import dk.alexandra.fresco.suite.ninja.protocols.NinjaOpenToAllProtocol;
import dk.alexandra.fresco.suite.ninja.protocols.NinjaXORProtocol;

public class NinjaFactory extends AbstractBinaryFactory implements BasicLogicFactory {

	private int counter;
	
	public NinjaFactory() {
		this.counter = 0;
	}
	
	@Override
	public CloseBoolProtocol getCloseProtocol(int source, OBool open, SBool closed) {
		return new NinjaCloseProtocol(counter++, source, open, closed);
	}

	@Override
	public OpenBoolProtocol getOpenProtocol(SBool closed, OBool open) {
		return new NinjaOpenToAllProtocol(counter++, (NinjaSBool)closed, (NinjaOBool)open);
	}

	@Override
	public OpenBoolProtocol getOpenProtocol(int target, SBool closed, OBool open) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SBool getSBool() {
		return new NinjaSBool();
	}

	@Override
	public SBool[] getSBools(int amount) {
		SBool[] res = new SBool[amount];
		for(int i = 0; i < amount; i++) {
			res [i] = this.getSBool();
		}
		return res;
	}

	@Override
	public SBool getKnownConstantSBool(boolean b) {
		NinjaSBool bool = new NinjaSBool();
		bool.setValue(b);
		return bool;
	}

	@Override
	public SBool[] getKnownConstantSBools(boolean[] bools) {
		SBool[] ninjaSBools = new SBool[bools.length];
		for (int i = 0; i < ninjaSBools.length; i++) {
			ninjaSBools[i] = getKnownConstantSBool(bools[i]);
		}
		return ninjaSBools;
	}

	@Override
	public OBool getOBool() {
		return new NinjaOBool();
	}

	@Override
	public OBool getKnownConstantOBool(boolean b) {
		return new NinjaOBool(b);
	}

	@Override
	public AndProtocol getAndProtocol(SBool inLeft, SBool inRight, SBool out) {
		return new NinjaANDProtocol(counter++, (NinjaSBool)inLeft, (NinjaSBool)inRight, (NinjaSBool)out);
	}

	@Override
	public AndProtocol getAndProtocol(SBool inLeft, OBool inRight, SBool out) {
		throw new RuntimeException("Not implemented yet");
	}

	@Override
	public NotProtocol getNotProtocol(SBool in, SBool out) {
		return new NinjaNOTProtocol(counter++, (NinjaSBool)in, (NinjaSBool)out);
	}

	@Override
	public XorProtocol getXorProtocol(SBool inLeft, SBool inRight, SBool out) {
		return new NinjaXORProtocol(counter++, (NinjaSBool)inLeft, (NinjaSBool)inRight, (NinjaSBool)out);
	}

	@Override
	public XorProtocol getXorProtocol(SBool inLeft, OBool inRight, SBool out) {
		throw new RuntimeException("Not implemented yet");
	}

}
