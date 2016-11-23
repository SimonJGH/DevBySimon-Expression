package com.simon.expression;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

public class ExpressionFragment2 extends Fragment {
	private GridView gv_expression;
	private List<String> mList = new ArrayList<String>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		return inflater.inflate(R.layout.fragment_expression, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {

		super.onViewCreated(view, savedInstanceState);

		gv_expression = (GridView) view.findViewById(R.id.gv_expression);

		if (mList != null) {
			mList.clear();
		}
		for (int i = 17; i < 34; i++) {
			mList.add(ThumbParser.mThumbTexts[i]);
		}
		mList.add("[删除]");

		GVAdapter adapter = new GVAdapter();
		gv_expression.setAdapter(adapter);
		gv_expression.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				String value = mList.get(arg2);

				Intent intent = new Intent();
				intent.setAction("expression");
				intent.putExtra("expression", value);
				getActivity().sendBroadcast(intent);
			}
		});
	}

	class GVAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mList != null ? mList.size() : 0;
		}

		@Override
		public Object getItem(int position) {
			return mList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder mHolder;
			if (convertView == null) {
				mHolder = new ViewHolder();
				convertView = LayoutInflater.from(getActivity()).inflate(
						R.layout.gv_item, null);
				mHolder.tv_show = (TextView) convertView
						.findViewById(R.id.tv_show);
				convertView.setTag(mHolder);
			} else {
				mHolder = (ViewHolder) convertView.getTag();
			}
			CharSequence convertContent = ThumbParser.getInstance()
					.addThumbSpans(mList.get(position));
			mHolder.tv_show.setText(convertContent);
			return convertView;
		}

	}

	class ViewHolder {
		TextView tv_show;
	}
}
