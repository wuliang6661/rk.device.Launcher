package rk.device.launcher.utils;

import android.content.Context;
import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.Hashtable;

import rk.device.launcher.R;

public class QRCodeUtils {
	static int QR_WIDTH	= 450, QR_HEIGHT = 450;

	public static Bitmap createQRCode(String url, int qr_width, int qr_height) {
		try {
			// 判断URL合法性
			if (url == null || "".equals(url) || url.length() < 1) {
				return null;
			}

//			if(qr_width == 0 || qr_height ==0 ){
//				qr_width = QR_WIDTH;
//				qr_height = QR_HEIGHT;
//			}

			Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			// 图像数据转换，使用了矩阵转换
			BitMatrix bitMatrix = new QRCodeWriter().encode(url,
					BarcodeFormat.QR_CODE, qr_width, qr_height, hints);
			int[] pixels = new int[qr_width * qr_height];
			// 下面这里按照二维码的算法，逐个生成二维码的图片，
			// 两个for循环是图片横列扫描的结果
			for (int y = 0; y < qr_height; y++) {
				for (int x = 0; x < qr_width; x++) {
					if (bitMatrix.get(x, y)) {
						pixels[y * qr_width + x] = 0xff000000;
					} else {
						pixels[y * qr_width + x] = 0xffffffff;
					}
				}
			}
			// 生成二维码图片的格式，使用ARGB_8888
			Bitmap bitmap = Bitmap.createBitmap(qr_width, qr_height,
					Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, qr_width, 0, 0, qr_width, qr_height);
			return bitmap;
		} catch (WriterException e) {
			e.printStackTrace();
		}

		return null;
	}


	public static Bitmap createQRCode(String url) {
		try {
			// 判断URL合法性
			if (url == null || "".equals(url) || url.length() < 1) {
				return null;
			}
			Hashtable<EncodeHintType, String> hints = new Hashtable<>();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			// 图像数据转换，使用了矩阵转换
			BitMatrix bitMatrix = new QRCodeWriter().encode(url,
					BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
			int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
			// 下面这里按照二维码的算法，逐个生成二维码的图片，
			// 两个for循环是图片横列扫描的结果
			for (int y = 0; y < QR_HEIGHT; y++) {
				for (int x = 0; x < QR_WIDTH; x++) {
					if (bitMatrix.get(x, y)) {
						pixels[y * QR_WIDTH + x] = 0xff000000;
					} else {
						pixels[y * QR_WIDTH + x] = 0xffffffff;
					}
				}
			}
			// 生成二维码图片的格式，使用ARGB_8888
			Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT,
					Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
			return bitmap;
		} catch (WriterException e) {
			e.printStackTrace();
		}

		return null;
	}

	static int IMAGE_HALFWIDTH = 30;
	// 前景色
	static int FOREGROUND_COLOR	= 0xff000000;
	// 背景色
	static int BACKGROUND_COLOR	= 0xffffffff;

	public static Bitmap createQRCodeWithIcon(String str, Bitmap icon, Context context) {
		try {
			// 缩放一个35*35的图片
			int size = context.getResources().getDimensionPixelSize(R.dimen.dp30);
			icon = BitmapUtil.scaleBtimap(icon, size, size);
			icon = BitmapUtil.getRoundedCornerBitmap(icon, 10);
			Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
			hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			hints.put(EncodeHintType.MARGIN, 1);
			// 生成二维矩阵,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
			BitMatrix matrix = new MultiFormatWriter().encode(str,
					BarcodeFormat.QR_CODE, 450, 450, hints);
			int width = matrix.getWidth();
			int height = matrix.getHeight();
			// 二维矩阵转为一维像素数组,也就是一直横着排了
			int halfW = width / 2;
			int halfH = height / 2;
			int[] pixels = new int[width * height];
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					if (x > halfW - IMAGE_HALFWIDTH && x < halfW + IMAGE_HALFWIDTH
							&& y > halfH - IMAGE_HALFWIDTH
							&& y < halfH + IMAGE_HALFWIDTH) {
						pixels[y * width + x] = icon.getPixel(x - halfW
								+ IMAGE_HALFWIDTH, y - halfH + IMAGE_HALFWIDTH);
					} else {
						if (matrix.get(x, y)) {
							pixels[y * width + x] = FOREGROUND_COLOR;
						} else { // 无信息设置像素点为白色
							pixels[y * width + x] = BACKGROUND_COLOR;
						}
					}

				}
			}
			Bitmap bitmap = Bitmap.createBitmap(width, height,
					Bitmap.Config.ARGB_8888);
			// 通过像素数组生成bitmap
			bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
			return bitmap;
		}
		catch (Exception e) {
			return null;
		}
	}

	public static Bitmap createOneDCode(String content) {
		try {
			// 生成一维条码,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
			BitMatrix matrix = new MultiFormatWriter().encode(content,
					BarcodeFormat.CODE_128, 700, 200);
			int width = matrix.getWidth();
			int height = matrix.getHeight();
			int[] pixels = new int[width * height];
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					if (matrix.get(x, y)) {
						pixels[y * width + x] = 0xff000000;
					}
				}
			}

			Bitmap bitmap = Bitmap.createBitmap(width, height,
					Bitmap.Config.ARGB_8888);
			// 通过像素数组生成bitmap,具体参考api
			bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
			return bitmap;
		}
		catch (Exception e) {

		}

		return null;
	}
}
