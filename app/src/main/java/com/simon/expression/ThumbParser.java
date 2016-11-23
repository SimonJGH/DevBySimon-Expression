package com.simon.expression;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;

public class ThumbParser {
	/*** 表情大小 */
	private final int SMILE_ICON_SIZE = 60;
	/*** 表情文本 */
	public static String[] mThumbTexts;
	/*** 表情图标 */
	public static Bitmap[] mThumbIcons;
	/*** 表情名称 、图标的映射 */
	private final HashMap<String, Bitmap> mThumbToRes;

	private final Pattern mPattern;
	private final Context mContext;
	private static ThumbParser sInstance;

	/***
	 * 取得表情处理类实例
	 * 
	 * @return
	 */
	public static ThumbParser getInstance() {
		return sInstance;
	}

	/***
	 * 初始化表情解析器（将文本解析为表情图标显示，如[微笑] 显示微笑图标）
	 * 
	 * @param context
	 */
	public static void init(Context context) {
		sInstance = new ThumbParser(context);
	}

	/***
	 * 构造函数
	 * 
	 * @param context
	 */
	private ThumbParser(Context context) {
		mContext = context;
		// 表情文本 读取arrays.xml
		mThumbTexts = mContext.getResources().getStringArray(
				R.array.default_thumbs_texts);
		// 构建表情文本和表情图标之间的映射
		mThumbToRes = buildThumbToRes();
		// 构建正则表达式
		mPattern = buildPattern();
	}

	/**
	 * 根据文本替换成图片
	 */
	public CharSequence addThumbSpans(CharSequence text) {
		SpannableStringBuilder builder = new SpannableStringBuilder(text);
		Matcher matcher = mPattern.matcher(text);
		int i = 1;
		while (matcher.find()) {
			System.out.println(i++ + " = " + matcher.group());
			Bitmap icon = mThumbToRes.get(matcher.group());
			builder.setSpan(new ImageSpan(mContext, icon), matcher.start(),
					matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			System.out.println(builder);
		}
		return builder;
	}

	/***
	 * 构建表情文本和表情图标之间的映射
	 * 
	 * @return HashMap<String, Bitmap>
	 */

	private HashMap<String, Bitmap> buildThumbToRes() {
		// 表情图标名称
		String[] thumbIconNames = mContext.getResources().getStringArray(
				R.array.default_thumbs_names);
		// 判断 图标数组 与 图标名称数组 是否匹配
		if (thumbIconNames.length != mThumbTexts.length) {
			throw new IllegalStateException("Thumb resource ID/text mismatch");
		}

		int iconNum = thumbIconNames.length;
		mThumbIcons = new Bitmap[iconNum];
		// 图标和名称映射的表
		HashMap<String, Bitmap> thumbToRes = new HashMap<String, Bitmap>(
				iconNum);
		for (int i = 0; i < iconNum; i++) {
			// 构建图标路径
			String iconFileName = "thumbs/" + thumbIconNames[i] + ".png";
			// 从Assets中读取图片
			Bitmap iconBitmap = getImageFromAssets(mContext, iconFileName);
			if (iconBitmap == null)
				continue;
			// 图片任意形状的放大缩小
			iconBitmap = ZoomToFixShape(iconBitmap, SMILE_ICON_SIZE,
					SMILE_ICON_SIZE);
			mThumbIcons[i] = iconBitmap;
			thumbToRes.put(mThumbTexts[i], mThumbIcons[i]);
		}

		return thumbToRes;
	}

	/** 构建正则表达式 */
	private Pattern buildPattern() {
		StringBuilder patternString = new StringBuilder(mThumbTexts.length * 3);

		// Build a regex that looks like (:-)|:-(|...), but escaping the smilies
		// properly so they will be interpreted literally by the regex matcher.
		patternString.append('(');
		for (String s : mThumbTexts) {
			patternString.append(Pattern.quote(s));
			patternString.append('|');
		}
		// Replace the extra '|' with a ')'
		patternString.replace(patternString.length() - 1,
				patternString.length(), ")");

		return Pattern.compile(patternString.toString());
	}

	/***
	 * 从Assets中读取图片 范例： Bitmap bmp = getImageFromAssetsFile("kugame/test.png");
	 * 
	 * @param context
	 * @param fileName
	 * @return
	 */
	static Bitmap getImageFromAssets(Context context, String fileName) {
		Bitmap image = null;
		AssetManager am = context.getResources().getAssets();
		try {
			InputStream is = am.open(fileName);
			image = decodeStream(is);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return image;
	}

	/**
	 * 解码图片流，据说可以避免内存溢出：bitmap size exceeds VM budget
	 * 
	 * @param picStream
	 * @return
	 */
	public static Bitmap decodeStream(InputStream picStream) {
		Bitmap bitmap = null;
		BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
		bitmapOptions.inSampleSize = 4;
		bitmap = BitmapFactory.decodeStream(picStream);
		return bitmap;
	}

	/**
	 * 图片任意形状的放大缩小
	 */
	static public Bitmap ZoomToFixShape(Bitmap pic1, int w, int h) {
		Bitmap tempBitmap = null;
		int bitH = pic1.getHeight();
		int bitW = pic1.getWidth();
		Matrix mMatrix = new Matrix();

		float scoleW = (float) w / (float) bitW;
		float scoleH = (float) h / (float) bitH;

		mMatrix.reset();
		mMatrix.postScale(scoleW, scoleH);
		tempBitmap = Bitmap.createBitmap(pic1, 0, 0, bitW, bitH, mMatrix, true);
		// pic1.recycle();
		return tempBitmap;

	}

	/** 对外接口：获取表情文本数组 */
	public static String[] getSmileTexts() {
		return mThumbTexts;
	}

	/** 对外接口：获取表情图标数组 */
	public static Bitmap[] getSmileIcons() {
		return mThumbIcons;
	}
}
