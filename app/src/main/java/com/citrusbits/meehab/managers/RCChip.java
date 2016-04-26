package com.citrusbits.meehab.managers;

public class RCChip {

	int numbers;
	RCChipType rcChipType;

	public int getNumbers() {
		return numbers;
	}

	public void setNumbers(int numbers) {
		this.numbers = numbers;
	}

	public RCChipType getRcChipType() {
		return rcChipType;
	}

	public void setRcChipType(RCChipType rcChipType) {
		this.rcChipType = rcChipType;
	}

	public enum RCChipType {
		DAYS, MONTH, YEARS
	}

}
