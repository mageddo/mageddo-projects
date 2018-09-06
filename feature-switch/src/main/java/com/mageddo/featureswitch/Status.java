package com.mageddo.featureswitch;

import java.util.Objects;

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

	public static Status fromCode(int code){
		for (Status value : values()) {
			if(value.getCode() == code){
				return value;
			}
		}
		return null;
	}

	public static Status fromCode(String code){
		return fromCode(code, Status.INACTIVE);
	}

	public static Status fromCode(String code, Status defaultStatus){
		for (Status value : values()) {
			if(Objects.equals(code, String.valueOf(value.getCode()))){
				return value;
			}
		}
		return defaultStatus;
	}
}
