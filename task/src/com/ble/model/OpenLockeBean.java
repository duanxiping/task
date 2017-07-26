package com.ble.model;

public class OpenLockeBean {
	
	private String openSuccess;
	private String openTime;
	
	
	public String getOpenSuccess() {
		return openSuccess;
	}
	public void setOpenSuccess(String openSuccess) {
		this.openSuccess = openSuccess;
	}
	public String getOpenTime() {
		return openTime;
	}
	public void setOpenTime(String openTime) {
		this.openTime = openTime;
	}
	@Override
	public String toString() {
		return "OpenLockeBean [openSuccess=" + openSuccess + ", openTime="
				+ openTime + "]";
	}
	
	

}
