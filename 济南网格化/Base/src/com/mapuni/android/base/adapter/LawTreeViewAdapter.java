package com.mapuni.android.base.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mapuni.android.base.R;
import com.mapuni.android.dataprovider.SqliteUtil;

/**
 * FileName: TreeViewAdapter.java Description: 两级树的适配器
 * 
 * @author 王红娟
 * @Version 1.3.4
 * @Copyright 中科宇图天下科技有限公司 Create at: 2012-12-3 下午05:57:50
 */
public class LawTreeViewAdapter extends BaseExpandableListAdapter {

	/** 每一个节点的高度 */
	public static final int ItemHeight = 75;
	/** 每一项距左边的距离 */
	public static final int PaddingLeft = 36;
	/** 初始化据左边的的距离 */
	private int myPaddingLeft = 0;

	/**
	 * FileName: TreeViewAdapter.java Description: 内部类
	 * 
	 * @author 王红娟
	 * @Version 1.3.4
	 * @Copyright 中科宇图天下科技有限公司 Create at: 2012-12-3 下午06:00:44
	 */
	static public class TreeNode {
		public Object parent;
		public List<HashMap<String, Object>> childs = new ArrayList<HashMap<String, Object>>();
	}

	List<TreeNode> treeNodes = new ArrayList<TreeNode>();
	Context parentContext;

	public LawTreeViewAdapter(Context view, int myPaddingLeft) {
		parentContext = view;
		this.myPaddingLeft = myPaddingLeft;
	}

	@Override
	public int getGroupCount() {
		return treeNodes.size();

	}

	@Override
	public int getChildrenCount(int groupPosition) {
		if (treeNodes == null || treeNodes.size() == 0) {
			return 0;
		}
		return treeNodes.get(groupPosition).childs.size();

	}

	@Override
	public Object getGroup(int groupPosition) {
		return treeNodes.get(groupPosition).parent;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return treeNodes.get(groupPosition).childs.size();

	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		HashMap<String, String> map = (HashMap<String, String>) getGroup(groupPosition);
		TextView textView = getGroupTextView(this.parentContext);

		// 查询出包含子项数目
		ArrayList<HashMap<String, Object>> list = SqliteUtil.getInstance()
				.queryBySqlReturnArrayListHashMap(
						"select count(*) from t_handbookcatalog where pid = '"
								+ map.get("id").toString() + "'");
		String numStr = "0";
		if (list != null) {
			numStr = list.get(0).get("count(*)").toString();
		}
		textView.setText(map.get("title") + "(" + numStr + ")");
		textView.setTag(map.get("id"));
		textView.setGravity(Gravity.CENTER_VERTICAL);
		textView.setPadding(myPaddingLeft + PaddingLeft, 0, 0, 0);
		textView.setTextColor(Color.BLACK);
		return textView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		// View treeView = getTextView(this.parentContext);
		View treeView = LayoutInflater.from(this.parentContext).inflate(
				R.layout.flfgtreeview, null);
		TextView textView = (TextView) treeView.findViewById(R.id.flfgtitle);
		ImageView image = (ImageView) treeView.findViewById(R.id.flfglefticon);
		Button fuhan = (Button) treeView.findViewById(R.id.fuhan);
		Button xxfg = (Button) treeView.findViewById(R.id.xxfg);
		xxfg.setWidth(220);
		fuhan.setWidth(150);
		String filepath = treeNodes.get(groupPosition).childs
				.get(childPosition).get("id").toString();
		String name = treeNodes.get(groupPosition).childs.get(childPosition)
				.get("title").toString();
		textView.setText(name);
		textView.setGravity(Gravity.CENTER_VERTICAL);
		textView.setTextSize(16);
		/*
		 * fuhan.setText("复      函"); xxfg.setText("现行法律");
		 */

		// 查询出复函数量
		ArrayList<HashMap<String, Object>> listFH = SqliteUtil.getInstance()
				.queryBySqlReturnArrayListHashMap(
						"select count(*) from t_attachment where FK_Id = '"
								+ filepath + "' and remark = '复函'");

		// wsc 增加显示文件夹下文件的复函数量
		if (listFH != null) {
			String sfh = "复函" + "(" + listFH.get(0).get("count(*)") + ")";
			fuhan.setText(sfh);
		}
		// 查询出先行法律数量
		ArrayList<HashMap<String, Object>> listXXFL = SqliteUtil.getInstance()
				.queryBySqlReturnArrayListHashMap(
						"select count(*) from t_attachment where FK_Id = '"
								+ filepath + "' and remark = '现行法律'");
		if (listXXFL != null) {
			String sfhxx = "现行法律" + "(" + listXXFL.get(0).get("count(*)") + ")";
			xxfg.setText(sfhxx);
		}

		xxfg.setTag(treeNodes.get(groupPosition).childs.get(childPosition));
		fuhan.setTag(treeNodes.get(groupPosition).childs.get(childPosition));

		xxfg.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				HashMap<String, Object> map = (HashMap<String, Object>) v
						.getTag();
				Intent intent = new Intent();
				intent.setClassName("com.mapuni.android.MobileEnforcement",
						"com.mapuni.android.infoQuery.LNFLFGShow");
				intent.putExtra("pid", map.get("id").toString());
				intent.putExtra("title", map.get("title").toString());
				intent.putExtra("remark", "现行法律");
				parentContext.startActivity(intent);

			}
		});
		textView.setTag(filepath);
		Drawable d = this.parentContext.getResources().getDrawable(
				R.drawable.specialitem_right);
		image.setBackgroundDrawable(d);
		fuhan.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				HashMap<String, Object> map = (HashMap<String, Object>) v
						.getTag();
				Intent intent = new Intent();
				intent.setClassName("com.mapuni.android.MobileEnforcement",
						"com.mapuni.android.infoQuery.LNFLFGShow");
				intent.putExtra("pid", map.get("id").toString());
				intent.putExtra("title", map.get("title").toString());
				intent.putExtra("remark", "复函");
				parentContext.startActivity(intent);

			}
		});
		textView.setTextColor(Color.BLACK);
		fuhan.setTextColor(Color.BLACK);
		xxfg.setTextColor(Color.BLACK);

		textView.setHeight(ItemHeight);
		image.setPadding(0, 0, 0, 0);
		// File file = new File(filepath);
		// if (!ifHaveDirectory(file)) {
		// fuhan.setVisibility(View.GONE);
		// xxfg.setVisibility(View.GONE);
		// }
		return treeView;
	}

	@Override
	public boolean isChildSelectable(int i, int j) {

		return true;
	}

	static public View getTextView(Context context) {
		View itemview = LayoutInflater.from(context).inflate(
				R.layout.flfgtreeview, null);
		// AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
		// ViewGroup.LayoutParams.FILL_PARENT, ItemHeight);
		// itemview.setLayoutParams(lp);
		// textView.setPadding(10, 0, 0, 0);
		// textView.setGravity(Gravity.CENTER_VERTICAL);

		return itemview;
	}

	static public TextView getGroupTextView(Context context) {
		AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT, ItemHeight);

		TextView textView = new TextView(context);
		textView.setTextSize(22);
		textView.setLayoutParams(lp);
		textView.setPadding(10, 0, 0, 0);
		textView.setGravity(Gravity.CENTER_VERTICAL);

		return textView;
	}

	public List<TreeNode> GetTreeNode() {
		return treeNodes;
	}

	public void UpdateTreeNode(List<TreeNode> nodes) {
		treeNodes = nodes;
	}

	public boolean ifHaveDirectory(File file) {
		File[] files = file.listFiles();
		for (File f : files) {
			if (f.isDirectory()) {
				return true;
			}

		}
		return false;
	}

}
