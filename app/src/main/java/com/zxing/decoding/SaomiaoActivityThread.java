package com.zxing.decoding;

import android.os.Handler;
import android.os.Looper;

import com.example.logisticsprivacysystem.SaomiaoActivity;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.ResultPointCallback;

import java.util.Hashtable;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Axes on 2017/4/23.
 */

final class SaomiaoActivityThread extends Thread{
	public static final String BARCODE_BITMAP = "barcode_bitmap";
	private final SaomiaoActivity activity;
	private final Hashtable<DecodeHintType, Object> hints;
	private Handler handler;
	private final CountDownLatch handlerInitLatch;

	SaomiaoActivityThread(SaomiaoActivity activity,
				 Vector<BarcodeFormat> decodeFormats,
				 String characterSet,
				 ResultPointCallback resultPointCallback) {

		this.activity = activity;
		handlerInitLatch = new CountDownLatch(1);

		hints = new Hashtable<DecodeHintType, Object>(3);

		if (decodeFormats == null || decodeFormats.isEmpty()) {
			decodeFormats = new Vector<BarcodeFormat>();
			decodeFormats.addAll(DecodeFormatManager.ONE_D_FORMATS);
			decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS);
			decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS);
		}

		hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);

		if (characterSet != null) {
			hints.put(DecodeHintType.CHARACTER_SET, characterSet);
		}

		hints.put(DecodeHintType.NEED_RESULT_POINT_CALLBACK, resultPointCallback);
	}

	Handler getHandler() {
		try {
			handlerInitLatch.await();
		} catch (InterruptedException ie) {
			// continue?
		}
		return handler;
	}

	@Override
	public void run() {
		Looper.prepare();
		handler = new SaomiaoDecodeHandler(activity, hints);
		handlerInitLatch.countDown();
		Looper.loop();
	}

}
