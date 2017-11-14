package rk.device.launcher.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import rk.device.launcher.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ManualConfigFragment extends Fragment {


	public ManualConfigFragment() {
		// Required empty public constructor
	}

	public static ManualConfigFragment newInstance() {
		ManualConfigFragment fragment = new ManualConfigFragment();
		return fragment;
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_manual_config, container, false);
	}

}
