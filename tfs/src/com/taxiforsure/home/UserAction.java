package com.taxiforsure.home;

public interface UserAction {
	public enum TaxiSelectionAction {
		NONE, HATCHBACK, SEDAN, SUV
	}

	public enum TaxiRideTimeSelectionAction {
		NONE, NOW, LATER
	}
}
