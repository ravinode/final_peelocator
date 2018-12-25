package com.peelocator.kira.streak.pojo;

import java.io.Serializable;

public class Point implements Serializable {

	private String points;
	private String pointStatus;

	public String getPoints() {
		return points;
	}
	public void setPoints(String points) {
		this.points = points;
	}
	public String getPointStatus() {
		return pointStatus;
	}
	public void setPointStatus(String pointStatus) {
		this.pointStatus = pointStatus;
	}
	
	
}
