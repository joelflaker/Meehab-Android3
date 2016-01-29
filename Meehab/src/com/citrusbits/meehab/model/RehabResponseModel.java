package com.citrusbits.meehab.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RehabResponseModel {
	HashMap<Integer, String> days = new HashMap<>();

	List<RehabModel> rehabModels = new ArrayList<>();

	public void addDay(int dayId, String dayName) {
		days.put(dayId, dayName);
	}

	public String getDay(int dayId) {
		return days.get(dayId);
	}

	public HashMap<Integer, String> getDays() {
		return days;
	}

	public void setDays(HashMap<Integer, String> days) {
		this.days = days;
	}
	
	
	public void addRehabModels(List<RehabModel> rehabModels) {
		this.rehabModels.addAll(rehabModels);
	}

	public void addRehabModel(RehabModel rehabModel) {
		this.rehabModels.add(rehabModel);
	}

	public RehabModel getRehabModel(int postion) {

		if (postion >= rehabModels.size() || postion < 0) {
			throw new IllegalArgumentException("Enter valid position");
		}
		return rehabModels.get(postion);
	}

	public List<RehabModel> getRehabs() {
		return this.rehabModels;
	}
}
