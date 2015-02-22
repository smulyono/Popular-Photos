package courses.smulyono.me.instagramclient;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by smulyono on 2/22/15.
 */
public class CommentsDialog extends DialogFragment {

    private List<InstagramComment> comments;
    private ListView lvComments;
    private SwipeRefreshLayout swipeContainer;
    private InstagramCommentAdapter commentAdapter;
    private String mediaId;

    public CommentsDialog(){}


    public static CommentsDialog newInstance(String mediaId){
        CommentsDialog frag = new CommentsDialog();
        Bundle args = new Bundle();
        args.putString("mediaId", mediaId);
        frag.setArguments(args);
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.comment_photo, container);

        // media comments
        mediaId = getArguments().getString("mediaId");

        // setup the swipeRefreshLayout
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeComments);
        // setup refresh listeneer
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchComments(mediaId);
            }
        });
        swipeContainer.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        comments = new ArrayList<InstagramComment>();
        // create the adapter
        // attach arraylist into adapter
        commentAdapter = new InstagramCommentAdapter(getActivity(), comments);

        // get the elements
        lvComments = (ListView) view.findViewById(R.id.lvComments);
        // attach listview into adapter
        lvComments.setAdapter(commentAdapter);

        getDialog().setCanceledOnTouchOutside(true);
        getDialog().setTitle(R.string.showComments);

        fetchComments(mediaId);

        return view;
    }

    private void fetchComments(String mediaId){
//        client id
//        51802e168cb34954bc8fda6fa3bb2e82
//
//        Comments
//        https://api.instagram.com/v1/media/{media-id}}/comments?client_id=CLIENT_ID
        String url = "https://api.instagram.com/v1/media/" + mediaId + "/comments?client_id=" + PhotosActivity.CLIENT_ID;

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, null, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //
                JSONArray commentsJSON = null;
                commentAdapter.clear();
                try {
                    commentsJSON = response.getJSONArray("data");
                    // need to reverse the entry
                    for (int i = commentsJSON.length() - 1;i >=0 ; i--){
                        // iterate and put them into list
                        InstagramComment comment = new InstagramComment();
                        JSONObject commentJSON = commentsJSON.getJSONObject(i);

                        comment.text = commentJSON.getString("text");
                        comment.createdTime = commentJSON.getLong("created_time");
                        comment.fromUserFullName = commentJSON.getJSONObject("from").getString("username");
                        comment.fromUserProfilePic = commentJSON.getJSONObject("from").optString("profile_picture");
                        comments.add(comment);
                    }
                } catch (JSONException ev){
                    ev.printStackTrace();
                }
                commentAdapter.notifyDataSetChanged();

                swipeContainer.setRefreshing(false);
                Log.d(PhotosActivity.APP_TAG, "Total comment retrieved : " + comments.size());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d(PhotosActivity.APP_TAG, "Fail to fetch comments");
            }
        });

    }
}
