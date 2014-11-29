package com.taxiforsure.util;

import android.content.Context;

public class ShowProgressDialog {
	private TransparentProgressDialog progressDialog;

	public void showProgressBar(Context context, String msg) {
		progressDialog = new TransparentProgressDialog(context,
				com.example.taxiforsure.R.drawable.spinner_outer);
		progressDialog.show();
	}

	public void dismissProgressBar() {
		if (progressDialog != null && progressDialog.isShowing())
			progressDialog.dismiss();
	}
}
