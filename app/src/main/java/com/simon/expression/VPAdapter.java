package com.simon.expression;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

public class VPAdapter extends FragmentPagerAdapter {
	private List<Fragment> mList;

	/**
	 * FragmentPagerAdapter
	 * 继承PagerAdapter，该适配生成的Fragment都保存在内存之中，因此适用于相对静态并且数量较少的情况。
	 */

	/**
	 * FragmentStatePagerAdapter
	 * 继承PagerAdapter，该适配生成的Fragment只有当前页面保留，其它离开视野的页面全部销毁释放
	 * ，和ListView的BaseAdapter相似。
	 */

	public VPAdapter(FragmentManager fm, List<Fragment> list) {
		super(fm);
		this.mList = list;
	}

	/**
	 * 生产新的Fragment对象
	 */
	@Override
	public Fragment getItem(int arg0) {

		return mList.get(arg0);
	}

	/**
	 * 调用getItem()生成新的Fragment
	 */
	@Override
	public Object instantiateItem(ViewGroup container, int position) {

		return super.instantiateItem(container, position);
	}

	@Override
	public int getCount() {

		return mList.size();
	}

	/**
	 * (FragmentPagerAdapter)对Fragment进行Detach()处理并非remove()，
	 * 因此Fragment资源并不会释放还存在FragmentManager管理中。
	 * （FragmentStatePagerAdapter）对Fragment进行remove()处理，直接释放资源。
	 */
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {

		super.destroyItem(container, position, object);
	}
}
