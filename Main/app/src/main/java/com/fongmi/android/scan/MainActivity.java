package com.fongmi.android.scan;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.kroegerama.kaiteki.bcode.BarcodeResultListener;
import com.kroegerama.kaiteki.bcode.ui.BarcodeFragment;

import java.util.Arrays;
import java.util.Collections;

public class MainActivity extends AppCompatActivity implements BarcodeResultListener {

	private boolean scanned;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		hideSystemUI();
		initView();
	}

	private void initView() {
		getSupportFragmentManager().beginTransaction().replace(R.id.container, BarcodeFragment.Companion.makeInstance(Collections.singletonList(BarcodeFormat.QR_CODE), false)).commit();
	}

	@Override
	public boolean onBarcodeResult(@NonNull Result result) {
		if (scanned) return true;
		setScanned(true);
		openIntent(result.toString());
		return false;
	}

	private void openIntent(String data) {
		if (data.toLowerCase().startsWith("sms")) openSms(data);
		else if (Patterns.WEB_URL.matcher(data).matches()) openUrl(data);
		else show(data);
	}

	private void openSms(String data) {
		String[] array = data.split(":");
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("smsto:" + array[1]));
		intent.putExtra("sms_body", array[2]);
		startActivity(intent);
		finish();
	}

	private void openUrl(String data) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(data));
		startActivity(intent);
		finish();
	}

	private void show(String data) {
		runOnUiThread(() -> Toast.makeText(this, data, Toast.LENGTH_LONG).show());
		setScanned(false);
	}

	private void setScanned(boolean scanned) {
		this.scanned = scanned;
	}

	@Override
	public void onBarcodeScanCancelled() {
	}

	@Override
	public void onConfigurationChanged(@NonNull Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		hideSystemUI();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) hideSystemUI();
	}

	private void hideSystemUI() {
		int flags = View.SYSTEM_UI_FLAG_LOW_PROFILE | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
		getWindow().getDecorView().setSystemUiVisibility(flags);
	}
}