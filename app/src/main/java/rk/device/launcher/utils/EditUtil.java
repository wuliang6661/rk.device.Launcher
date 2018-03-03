package rk.device.launcher.utils;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mundane on 2017/12/12 下午5:59
 */

public class EditUtil {
	private static final String TAG = "EditUtil";
	public static void limitInput(EditText editText) {
		editText.setInputType(3);
		editText.addTextChangedListener(new TextWatcher() {
			private String mBeforeText;
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String text = s.toString();
				LogUtil.d(TAG, text);
				if (TextUtils.isEmpty(text)) {
					return;
				}
				int beforeLength = mBeforeText.length();
				int currentLength = text.length();
				if (currentLength < beforeLength) {
					return;
				}
				// 连续出现了4个数字
				// .*表示任何字符一次或多次
				String regex1 = "^.*\\d{4,}.*$";
				// 表示整个输入中出现了4个.
				// \\.表示一个. (因为.在正则中有特殊含义, 所以要加一个\, \转义为\\)
				// .*表示.之间出入了任意个任意字符
				String regex2 = "^.*\\..*\\..*\\..*\\..*$";
				// 表示除了数字和.以外的字符出现了一次或多次
				String regex3 = "^.*[^\\d^\\.]+.*$";
				if (text.contains("..") || text.startsWith(".") || isMatchRegex(regex1, text)
				|| isMatchRegex(regex2, text) || isMatchRegex(regex3, text)) {
					String newString = text.substring(0, text.length() - 1);
					editText.setText(newString);
					editText.setSelection(newString.length());
				}
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}
	
	private static boolean isMatchRegex(String regex, String text) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(text);
		return matcher.matches();
	}
	
}
