package rk.device.launcher.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import rk.device.launcher.R;
import rk.device.launcher.utils.NetUtils;

public class AutoObtainNetworkConfigFragment extends Fragment {


	@Bind(R.id.tv_ip)
	TextView mTvIp;
	@Bind(R.id.tv_sub_code)
	TextView mTvNetMask;
	@Bind(R.id.tv_net_gate)
	TextView mTvNetGate;
	@Bind(R.id.tv_dns)
	TextView mTvDns;

	public AutoObtainNetworkConfigFragment() {
		// Required empty public constructor
	}

	public static AutoObtainNetworkConfigFragment newInstance() {
		AutoObtainNetworkConfigFragment fragment = new AutoObtainNetworkConfigFragment();
		return fragment;
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_auto_obtain_network_config, container, false);
		ButterKnife.bind(this, view);
		return view;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		String ip = NetUtils.getIp2(getContext());
		mTvIp.setText(ip);
		String netMask = NetUtils.getNetMask(getContext());
		mTvNetMask.setText(netMask);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ButterKnife.unbind(this);
	}
}
