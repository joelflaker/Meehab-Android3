package com.citrusbits.meehab.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RehaabFilterResultHolder implements Serializable {

	private boolean anyType = true;
	private boolean anyInsuranceAccepted = true;
	private boolean anyZipCode = true;
	private boolean anyDistance = true;

	List<String> rehabType = new ArrayList<String>();
	List<String> insuranceAccepted = new ArrayList<String>();

	private String zipCode;
	private String distance;

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public void addRehabType(String fType) {
		if (!this.rehabType.contains(fType)) {
			this.rehabType.add(fType);
		}

	}

	public List<String> getRehabType() {
		return this.rehabType;
	}

	public void removeRehabType(String fType) {

		this.rehabType.remove(fType);
	}

	public void addInsuranceAccepted(String insuranceAccepted) {
		if (!this.insuranceAccepted.contains(insuranceAccepted)) {
			this.insuranceAccepted.add(insuranceAccepted);
		}

	}

	public List<String> getInsuranceAccepted() {
		return this.insuranceAccepted;
	}

	public void removeInsuranceAccepted(String insuranceAccepted) {
		this.insuranceAccepted.remove(insuranceAccepted);
	}

	public void removeEthenticiy(String fType) {
		this.rehabType.remove(fType);

	}

	public void clearRehabType() {

		rehabType.clear();
	}

	public void clearInsuranceAccepted() {

		insuranceAccepted.clear();
	}

	public boolean isanyType() {
		return anyType;
	}

	public void setanyType(boolean anyType) {
		this.anyType = anyType;
	}

	public boolean isanyInsuranceAccepted() {
		return anyInsuranceAccepted;
	}

	public void setanyInsuranceAccepted(boolean anyInsuranceAccepted) {
		this.anyInsuranceAccepted = anyInsuranceAccepted;
	}

	public boolean isanyZipCode() {
		return anyZipCode;
	}

	public void setanyZipCode(boolean anyZipCode) {
		this.anyZipCode = anyZipCode;
	}

	public boolean isanyDistance() {
		return anyDistance;
	}

	public void setanyDistance(boolean anyDistance) {
		this.anyDistance = anyDistance;
	}

}
