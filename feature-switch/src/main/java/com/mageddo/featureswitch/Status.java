package com.mageddo.featureswitch;

public enum Status {

	ACTIVE(1),
	INACTIVE(0),
	RESTRICTED(2),
	;

	private final int code;

	Status(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public static Status fromCode(String code) {
		return fromCode(Integer.parseInt(code));
	}

	public static Status fromCode(int code){
		for (Status value : values()) {
			if(value.getCode() == code){
				return value;
			}
		}
		return null;
	}
}
