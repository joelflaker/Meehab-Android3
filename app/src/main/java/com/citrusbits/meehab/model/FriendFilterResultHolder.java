package com.citrusbits.meehab.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FriendFilterResultHolder implements Serializable{

	public boolean isOnlineNow() {
		return onlineNow;
	}

	public void setOnlineNow(boolean onlineNow) {
		this.onlineNow = onlineNow;
	}

	public boolean isWillingToSponsor() {
		return willingToSponsor;
	}

	public void setWillingToSponsor(boolean willingToSponsor) {
		this.willingToSponsor = willingToSponsor;
	}

	public boolean isHasKids() {
		return hasKids;
	}

	public void setHasKids(boolean hasKids) {
		this.hasKids = hasKids;
	}

	private boolean onlineNow;
	private boolean willingToSponsor;
	private boolean hasKids;

	private boolean anyFriendType = true;
	private boolean anyGender = true;
	private boolean anyAge = true;

	private boolean anyEthenticity = true;
	private boolean anyMaterialStatus = true;
	private boolean anyinterestedIn = true;
	private boolean anyTimeSober = true;
	private boolean anyHeight = true;
	private boolean anyWeight = true;

	List<String> friendType = new ArrayList<>();
	List<String> gender = new ArrayList<>();
	List<String> age = new ArrayList<>();

	List<String> ethenticity = new ArrayList<>();
	List<String> materialStatus = new ArrayList<>();
	List<String> interestedIn = new ArrayList<>();
	List<String> sober = new ArrayList<>();
	List<String> height = new ArrayList<>();
	List<String> weight = new ArrayList<>();

	public void addFriendType(String fType) {
		if (!this.friendType.contains(fType)) {
			this.friendType.add(fType);
		}

	}
	
	public List<String> getFriendType(){
		return this.friendType;
	}

	public void removeFriendType(String fType) {

		this.friendType.remove(fType);
	}

	public void addGender(String gender) {
		if (!this.gender.contains(gender)) {
			this.gender.add(gender);
		}

	}
	
	public List<String> getGender(){
		return this.gender;
	}

	public void removeGender(String gender) {
		this.gender.remove(gender);
	}

	public void addAge(String age) {

		if (!this.age.contains(age)) {
			this.age.add(age);
		}

	}
	
	public List<String> getAge(){
		
		return this.age;
	}

	public void removeAge(String age) {
		this.age.remove(age);
	}

	public void addEthenticity(String ethenticity) {

		if (!this.ethenticity.contains(ethenticity)) {
			this.ethenticity.add(ethenticity);
		}

	}
	
	public List<String> getEthenticity(){
		return this.ethenticity;
	}

	public void removeEthenticiy(String fType) {
		this.friendType.remove(fType);

	}

	public void addMaterialStatus(String materialStatus) {
		if (!this.materialStatus.contains(materialStatus)) {
			this.materialStatus.add(materialStatus);
		}

	}
	
	public List<String> getMaterialStatus(){
		return this.materialStatus;
	}

	public void removeMaterialStatus(String materialStatus) {
		this.materialStatus.remove(materialStatus);

	}

	public void addInterestedIn(String interestedIn) {

		if (!this.interestedIn.contains(interestedIn)) {
			this.interestedIn.add(interestedIn);
		}

	}
	
	public List<String> getInterestedIn(){
		return this.interestedIn;
	}

	public void removeInterestedIn(String interestedIn) {
		this.interestedIn.remove(interestedIn);
	}

	public void addSober(String sober) {

		if (!this.sober.contains(sober)) {
			this.sober.add(sober);
		}

	}
	
	public List<String> getSober(){
		return this.sober;
	}

	public void removeSober(String sober) {
		this.sober.remove(sober);
	}

	public void addHeight(String height) {

		if (!this.height.contains(height)) {
			this.height.add(height);
		}

	}
	
	public List<String> getHeight(){
		return this.height;
	}

	public void removeHeight(String height) {
		this.height.remove(height);
	}

	public void addWeight(String weight) {

		if (!this.weight.contains(weight)) {
			this.weight.add(weight);
		}

	}
	
	public List<String> getWeight(){
		return this.weight;
	}

	public void removeWeight(String weight) {
		this.weight.remove(weight);
	}

	public void clearFriendType() {

		friendType.clear();
	}

	public void clearGender() {

		gender.clear();
	}

	public void clearAge() {

		age.clear();
	}

	public void clearEhtenticity() {

		ethenticity.clear();
	}

	public void clearMaterialStatus() {

		materialStatus.clear();
	}

	public void clearInterestedIn() {

		interestedIn.clear();
	}

	public void clearSober() {

		sober.clear();
	}

	public void clearHeight() {

		height.clear();
	}

	public void clearWeight() {

		weight.clear();
	}

	public boolean isAnyFriendType() {
		return anyFriendType;
	}

	public void setAnyFriendType(boolean anyFriendType) {
		this.anyFriendType = anyFriendType;
	}

	public boolean isAnyGender() {
		return anyGender;
	}

	public void setAnyGender(boolean anyGender) {
		this.anyGender = anyGender;
	}

	public boolean isAnyAge() {
		return anyAge;
	}

	public void setAnyAge(boolean anyAge) {
		this.anyAge = anyAge;
	}

	public boolean isAnyEthenticity() {
		return anyEthenticity;
	}

	public void setAnyEthenticity(boolean anyEthenticity) {
		this.anyEthenticity = anyEthenticity;
	}

	public boolean isAnyMaterialStatus() {
		return anyMaterialStatus;
	}

	public void setAnyMaterialStatus(boolean anyMaterialStatus) {
		this.anyMaterialStatus = anyMaterialStatus;
	}

	public boolean isAnyinterestedIn() {
		return anyinterestedIn;
	}

	public void setAnyinterestedIn(boolean anyinterestedIn) {
		this.anyinterestedIn = anyinterestedIn;
	}

	public boolean isAnyTimeSober() {
		return anyTimeSober;
	}

	public void setAnyTimeSober(boolean anyTimeSober) {
		this.anyTimeSober = anyTimeSober;
	}

	public boolean isAnyHeight() {
		return anyHeight;
	}

	public void setAnyHeight(boolean anyHeight) {
		this.anyHeight = anyHeight;
	}

	public boolean isAnyWeight() {
		return anyWeight;
	}

	public void setAnyWeight(boolean anyWeight) {
		this.anyWeight = anyWeight;
	}

}
