package org.hyperion.rs2.content.skills;

import org.hyperion.rs2.content.skills.Fletching.LogFletchingType;

public class FletchingVariables {

	private boolean isFletching = false;
	private LogFletchingType fletchingLogType;

	public void setFletching(boolean isFletching) {
		this.isFletching = isFletching;
	}

	public boolean isFletching() {
		return isFletching;
	}

	public void setFletchingType(LogFletchingType fletchingLogType) {
		this.fletchingLogType = fletchingLogType;
	}

	public LogFletchingType getFletchingType() {
		return fletchingLogType;
	}

}
