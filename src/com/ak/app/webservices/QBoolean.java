// FileName: QBoolean.java
// ----------------------------------------------------------------------------
// 
// QBitSystems, 2008
// www.qbitsystems.com
//
// Description:     
// Created:			Shamim
// Last Changed By: Shamim
//
// FileRevision:        1.0.0
// FileRevision Date:	12/26/08
//
// ============================================================================

package com.ak.app.webservices;

public class QBoolean {

	private boolean mValue = false;

	public QBoolean(){
	}

	public QBoolean(boolean value){
	}

	public void setValue(boolean value){
		mValue = value;
	}

	public boolean booleanValue(){
		return mValue;
	}
}
