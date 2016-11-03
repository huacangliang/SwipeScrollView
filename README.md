# SwipeScrollView
滑动更多菜单

使用方法

<com.lazymc.swipescrollview.SwipeScrollView
        android:layout_width="match_parent"
        android:layout_height="150dp"
        android:background="#ffffff">

        <RelativeLayout
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:background="#cccccc"
            android:tag="@string/swipe_content_tag">

            <TextView
                android:id="@+id/tv_txt"
                android:layout_width="100dp"
                android:layout_height="50dp"
                android:layout_centerInParent="true"
                android:text="test"
                android:gravity="center"
                android:background="#ffaacc"
                android:textColor="#ffffff"/>
        </RelativeLayout>

        <TextView
            android:id="@+id/tv_collect"
            android:layout_width="100dp"
            android:layout_height="match_parent"
            android:background="#ff660b"
            android:gravity="center"
            android:text="收藏"
            android:textColor="#ffffff"/>

        <TextView
            android:id="@+id/tv_follower"
            android:layout_width="100dp"
            android:layout_height="match_parent"
            android:background="#ff4ceb"
            android:gravity="center"
            android:text="关注"
            android:textColor="#ffffff"/>

        <TextView
            android:id="@+id/tv_del"
            android:layout_width="100dp"
            android:layout_height="match_parent"
            android:background="#ff000b"
            android:gravity="center"
            android:text="删除"
            android:textColor="#ffffff"/>
</com.lazymc.swipescrollview.SwipeScrollView>
    
    内容容器需要设置tag
    
    android:tag="@string/swipe_content_tag"
    
    以便库能够知道怎么布局
    
    原则上可以放在任意非横向滑动控件里，欢迎热爱编程同学一起优化，加入更多功能
