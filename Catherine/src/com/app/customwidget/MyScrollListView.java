package com.app.customwidget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.LinearLayout.LayoutParams;

public class MyScrollListView extends ListView{

	private int motionLastX;
	private int motionLastY;
	private int listTopPosition;//�б�������listTopPositionλ��
	private int listBottomPosition; //�б�����listTopPositionλ��
	public MyScrollListView(Context context) {
		super(context);
		init();
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @author WJL
	 * ��ʼ���Զ���listview������������listTopPosition��λ��Ϊ��ǰ��λ��
	 * �͵�����listBottomPosition��λ��Ϊ����λ��
	 */
	private void init() {
		// TODO Auto-generated method stub
		listTopPosition = 0;
		listBottomPosition = 1;
		setVerticalScrollBarEnabled(false);
		
		setDivider( new ColorDrawable( 0xffe0f0f0 ) );
		setDividerHeight(10);
	}
	
	/* (non-Javadoc)
	 * @see android.widget.AbsListView#onTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		final int action = ev.getAction();
		final int motionCurrentY = (int)ev.getY();
		
		switch(action)
		{
		case MotionEvent.ACTION_DOWN:
//			Log.i("MyScrollListView","ACTION_DOWN");
			motionLastY = motionCurrentY;
			myScrollListViewListner.onMotionDown(ev);
			break;
		case MotionEvent.ACTION_MOVE:
//			Log.i("MyScrollListView","ACTION_MOVE");
			
			final int childCount = getChildCount();//��ǰҳ��ɼ���child�ĸ���
			if(childCount == 0) return super.onTouchEvent(ev);
			
			int firstItemTop = getChildAt(0).getTop();
			int lastItemBottom = getChildAt(childCount-1).getBottom();
			
			float listBegin = getListPaddingTop();
			float listEnd = getHeight() - getListPaddingBottom();
			
			int totalItemCount = getAdapter().getCount() - listBottomPosition;
			int deltaY = motionCurrentY - motionLastY;
			int firstVisiblePosition = getFirstVisiblePosition();
//			Log.i("MyScrollListView", "dy = "+deltaY);
			myScrollListViewListner.onMotionMove(ev, deltaY);
			
			if(firstVisiblePosition <= listTopPosition && firstItemTop >= listBegin && deltaY > 0)
			{
//				Log.i("MyScrollListView", "---------------Pull Down");
				myScrollListViewListner.onListViewTopAndPullDown(deltaY);
				motionLastY = motionCurrentY;
			}
			
			if(firstVisiblePosition + childCount >= totalItemCount && lastItemBottom <= listEnd && deltaY < 0)
			{
//				Log.i("MyScrollListView", "---------------Pull Up");
				myScrollListViewListner.onListViewBottomAndPullUp(deltaY);
				motionLastY = motionCurrentY;
			}

			break;
		case MotionEvent.ACTION_UP:
//			Log.i("MyScrollListView","ACTION_UP");
			motionLastY = motionCurrentY;
			myScrollListViewListner.onMotionUp(ev);
			break;
			default: break;
		}
		
		return super.onTouchEvent(ev);
		
	}
	
	/**
	 * @author WJL
	 * ����MyScrollListView�ļ���������ʱΪ��
	 */
	onScrollListViewListener myScrollListViewListner = new onScrollListViewListener() {
		
		@Override
		public boolean onMotionUp(MotionEvent ev) {
			// TODO Auto-generated method stub
			return false;
		}
		
		@Override
		public boolean onMotionMove(MotionEvent ev, int deltaY) {
			// TODO Auto-generated method stub
			return false;
		}
		
		@Override
		public boolean onMotionDown(MotionEvent ev) {
			// TODO Auto-generated method stub
			return false;
		}
		
		@Override
		public boolean onListViewTopAndPullDown(int deltaY) {
			// TODO Auto-generated method stub
			return false;
		}
		
		@Override
		public boolean onListViewBottomAndPullUp(int deltaY) {
			// TODO Auto-generated method stub
			return false;
		}
	};
	
	/**
	 * @author WJL
	 * ��myScrollListViewListener���óɾ���ʵ�ֵ�listener
	 * 
	 * @param listener ����ʵ�ֵ�listener
	 */
	public void setOnScrollListViewListener(onScrollListViewListener listener)
	{
		myScrollListViewListner = listener;
	}
	
	/**
	 * @author WJL
	 * MyScrollListView �ļ������ӿ�
	 */
	public interface onScrollListViewListener
	{
		/**
		 * @author WJL
		 * �Ӷ���������ʱ�򴥷�
		 * @param deltaY
		 * @return
		 */
		boolean onListViewTopAndPullDown(int deltaY);
		
		/**
		 * @author WJL
		 * �ӵײ�������ʱ�򴥷�
		 * @param deltaY
		 * @return
		 */
		boolean onListViewBottomAndPullUp(int deltaY);
		
		/**
		 * @author WJL
		 * ������Ļ���µ�ʱ�򴥷�
		 * @param ev
		 * @return
		 */
		boolean onMotionDown(MotionEvent ev);
		
		/**
		 * @author WJL
		 * ������Ļ�ϻ�����ʱ�򴥷�
		 * @param ev
		 * @param deltaY
		 * @return
		 */
		boolean onMotionMove(MotionEvent ev, int deltaY);
		
		/**
		 * @author WJL
		 * ������Ļ��̧���ʱ�򴥷�
		 * @param ev
		 * @return
		 */
		boolean onMotionUp(MotionEvent ev);
	}

}
