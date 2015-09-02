package ir.weblogestaan.sardabir.Classes;

import android.content.Context;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

import ir.weblogestaan.sardabir.Classes.Subject;
import ir.weblogestaan.sardabir.MainActivity;
import ir.weblogestaan.sardabir.SubjectsActivity;
import ir.weblogestaan.sardabir.TermActivity;

public class EndlessScrollListener implements OnScrollListener {

    private int visibleThreshold = 10;
    private int currentPage = 1;
    private int previewedPage = 0;
    private int Total = 0;
    public boolean loading = true;
    private Context that;
    public String subject;
    public String className;

    public EndlessScrollListener() {
    }
    public EndlessScrollListener(String className, Context ctx,String sub,Subject s,int currentPage,int _total) {
        this.that = ctx;
        this.subject = sub;
        this.currentPage = currentPage;
        this.Total = _total;
        this.className = className;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {

    }

    @Override
    public void onScrollStateChanged(AbsListView listView, int scrollState) {
    	int lastVP = listView.getLastVisiblePosition();
        if (scrollState == SCROLL_STATE_IDLE) {
            if (lastVP >= listView.getCount() - 1 ) {
                currentPage = currentPage + 1;
                switch (this.className) {
                    case "MainActivity" :
                        ((MainActivity)that).LoadPosts(currentPage, true);
                        break;
                    case "TermActivity" :
                        ((TermActivity)that).LoadPosts(currentPage, true);
                        break;
                }
            }
        }
    }
}
