package rk.device.launcher.ui.activity;

import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import butterknife.Bind;
import rk.device.launcher.R;
import rk.device.launcher.adapter.TimeZoneRvAdapter;
import rk.device.launcher.base.BaseCompatActivity;
import rk.device.launcher.widget.itemdecoration.WifiListRvItemDecoration;


public class TimeZoneListActivity extends BaseCompatActivity implements TimeZoneRvAdapter.OnItemClickListerner {
	
	private static final String TAG = "TimeZoneListActivity";
	@Bind(R.id.iv_back)
	ImageView mIvBack;
	@Bind(R.id.tv_title)
	TextView mTvTitle;
	@Bind(R.id.rv)
	RecyclerView mRv;
	private List<HashMap<String, Object>> mZoneList;
	private TimeZoneRvAdapter mTimeZoneRvAdapter;
	
	// 第1步
	@Override
	protected int getLayout() {
		return R.layout.activity_time_zone_list;
	}
	
	
	// 第2步
	@Override
	protected void initView() {
		goBack();
		setTitle("选择时区");
	}
	
	// 第3步
	@Override
	protected void initData() {
		ZoneGetter zoneGetter = new ZoneGetter();
		mZoneList = zoneGetter.getZones(this);
		mTimeZoneRvAdapter = new TimeZoneRvAdapter(mZoneList);
		mRv.setAdapter(mTimeZoneRvAdapter);
		mRv.addItemDecoration(new WifiListRvItemDecoration(this));
		mTimeZoneRvAdapter.setOnItemClickedListener(this);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	public static final String KEY_ID = "id";  // value: String
	public static final String KEY_DISPLAYNAME = "name";  // value: String
	public static final String KEY_GMT = "gmt";  // value: String
	public static final String KEY_OFFSET = "offset";  // value: int (Integer)
	public static final String XMLTAG_TIMEZONE = "timezone";
	
	@Override
	public void onItemClicked(int position) {
		final Map<?, ?> map = (Map<?, ?>)mZoneList.get(position);
		final String tzId = (String) map.get(KEY_ID);
		
		// Update the system timezone value
		final AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		alarm.setTimeZone(tzId);
		final TimeZone tz = TimeZone.getTimeZone(tzId);
		Intent intent = new Intent();
//		intent.putExtra(KEY_CHECKED_INDEX, position);
		setResult(RESULT_OK, intent);
		finish();
		
	}
	
	static class ZoneGetter {
		private final List<HashMap<String, Object>> mZones =
		new ArrayList<HashMap<String, Object>>();
		private final HashSet<String> mLocalZones = new HashSet<String>();
		private final Date mNow = Calendar.getInstance().getTime();
		private final SimpleDateFormat mZoneNameFormatter = new SimpleDateFormat("zzzz");
		
		private List<HashMap<String, Object>> getZones(Context context) {
//			for (String olsonId : TimeZoneNames.forLocale(Locale.getDefault())) {
//				mLocalZones.add(olsonId);
//			}
			try {
				XmlResourceParser xrp = context.getResources().getXml(R.xml.timezones);
				while (xrp.next() != XmlResourceParser.START_TAG) {
					continue;
				}
				xrp.next();
				while (xrp.getEventType() != XmlResourceParser.END_TAG) {
					while (xrp.getEventType() != XmlResourceParser.START_TAG) {
						if (xrp.getEventType() == XmlResourceParser.END_DOCUMENT) {
							return mZones;
						}
						xrp.next();
					}
					if (xrp.getName().equals(XMLTAG_TIMEZONE)) {
						String olsonId = xrp.getAttributeValue(0);
						xrp.next();
						String displayName = xrp.getText();
						addTimeZone(olsonId, displayName);
					}
					while (xrp.getEventType() != XmlResourceParser.END_TAG) {
						xrp.next();
					}
					xrp.next();
				}
				xrp.close();
			} catch (XmlPullParserException xppe) {
				Log.e(TAG, "Ill-formatted timezones.xml file");
			} catch (IOException ioe) {
				Log.e(TAG, "Unable to read timezones.xml file");
			}
			return mZones;
		}
		
		private void addTimeZone(String olsonId, String name) {
			// We always need the "GMT-07:00" string.
			final TimeZone tz = TimeZone.getTimeZone(olsonId);
			
			// For the display name, we treat time zones within the country differently
			// from other countries' time zones. So in en_US you'd get "Pacific Daylight Time"
			// but in de_DE you'd get "Los Angeles" for the same time zone.
			String displayName;
			if (mLocalZones.contains(olsonId)) {
				// Within a country, we just use the local name for the time zone.
				mZoneNameFormatter.setTimeZone(tz);
				displayName = mZoneNameFormatter.format(mNow);
			} else {
				// For other countries' time zones, we use the exemplar location.
				final String localeName = Locale.getDefault().toString();
//				displayName = TimeZoneNames.getExemplarLocation(localeName, olsonId);
//				LogUtil.d(TAG, "displayName = " + displayName);
			}
			displayName = name;
			
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put(KEY_ID, olsonId);
			map.put(KEY_DISPLAYNAME, displayName);
			map.put(KEY_GMT, SetTimeActivity.getTimeZoneText(tz, false));
			map.put(KEY_OFFSET, tz.getOffset(mNow.getTime()));
			
			mZones.add(map);
		}
	}
	
	
	
}
