package rk.device.launcher.service;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.igexin.sdk.GTIntentService;
import com.igexin.sdk.PushManager;
import com.igexin.sdk.message.GTCmdMessage;
import com.igexin.sdk.message.GTTransmitMessage;

import rk.device.launcher.base.utils.rxbus.RxBus;
import rk.device.launcher.bean.PushMessageModel;
import rk.device.launcher.bean.SetPageContentBean;
import rk.device.launcher.global.Constant;
import rk.device.launcher.utils.SPUtils;


public class RKLauncherPushIntentService extends GTIntentService {
	public RKLauncherPushIntentService() {
	}
	
	@Override
	public void onReceiveServicePid(Context context, int pid) {
	}
	
	@Override
	public void onReceiveMessageData(Context context, GTTransmitMessage msg) {
		String taskId = msg.getTaskId();
		String messageId = msg.getMessageId();
		byte[] payload = msg.getPayload();
		boolean result = PushManager.getInstance().sendFeedbackMessage(context, taskId, messageId, 90001);
		Log.d(TAG, "call sendFeedbackMessage = " + (result ? "success" : "failed"));
		if (payload == null) {
			Log.e(TAG, "receiver payload = null");
		} else {
			String playLoad = new String(payload);
			Log.d(TAG, "receiver payload = " + playLoad);
			PushMessageModel pushMessageModel = JSON.parseObject(playLoad, PushMessageModel.class);
			if (pushMessageModel == null) {
				Log.e(TAG, "pushMessageModel = null");
				return;
			}
			String msgtype = pushMessageModel.msgtype;
			PushMessageModel.Data data = pushMessageModel.data;
			switch (msgtype) {
				case "update": // 升级消息
					Log.d(TAG, "file = " + data.file);
					if (TextUtils.equals("rom", data.type)) { // rom升级
						DownLoadIntentService.startDownLoad(this, data.file, Constant.KEY_ROM);
					} else { // apk升级
						DownLoadIntentService.startDownLoad(this, data.file, Constant.KEY_APK);
					}
					break;
				case "notice": // 通知消息
					if (TextUtils.equals("text", data.type)) {
						if (TextUtils.isEmpty(data.content)) {
							return;
						}
						SPUtils.putString(Constant.KEY_FIRSTPAGE_CONTENT, data.content);
						RxBus.getDefault().post(new SetPageContentBean(data.content));
					}
					break;
				case "ad": // 广告通知
					break;
				default:
					Log.e(TAG, "未知类型消息");
					break;
			}
		}
	}
	
	@Override
	public void onReceiveClientId(Context context, String clientid) {
		Log.e(TAG, "onReceiveClientId -> " + "clientid = " + clientid);
	}
	
	@Override
	public void onReceiveOnlineState(Context context, boolean online) {
	}
	
	@Override
	public void onReceiveCommandResult(Context context, GTCmdMessage cmdMessage) {
	}
}
