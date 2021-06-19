package com.arty.Common;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class CommonFragment extends Fragment {
    protected SwipeRefreshLayout swipeRefreshLayout = null;
    public TimeComponent timeComponent;

    public CommonFragment() {
        timeComponent = new TimeComponent();
    }
}
