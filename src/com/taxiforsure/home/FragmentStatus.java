package com.taxiforsure.home;

public interface FragmentStatus {
	public enum FragmentName {
		HOME_FRAGMENT
	}

	public interface FragementVisibility {
		public void toggleVisibility();
	}

}
