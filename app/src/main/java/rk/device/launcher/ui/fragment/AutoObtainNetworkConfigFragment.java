package rk.device.launcher.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import rk.device.launcher.R;

public class AutoObtainNetworkConfigFragment extends Fragment {


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
		return inflater.inflate(R.layout.fragment_auto_obtain_network_config, container, false);
	}

}
