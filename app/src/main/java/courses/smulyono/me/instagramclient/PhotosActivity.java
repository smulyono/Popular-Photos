package courses.smulyono.me.instagramclient;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class PhotosActivity extends ActionBarActivity {
    public static final String APP_TAG = PhotosActivity.class.getSimpleName();

    public static final String CLIENT_ID = "51802e168cb34954bc8fda6fa3bb2e82";

    private ArrayList<InstagramPhoto> photos;
    private InstagramPhotoAdapter aPhotos;
    private ListView lvPhotos;
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);

        // setup the swipeRefreshLayout
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // setup refresh listeneer
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchPopularPhotos();
            }
        });
        swipeContainer.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        photos = new ArrayList<InstagramPhoto>();

        // Attach the adapter
        aPhotos = new InstagramPhotoAdapter(this, photos);
        lvPhotos = (ListView) findViewById(R.id.lvPhotos);
        lvPhotos.setAdapter(aPhotos);

        // send API request
        fetchPopularPhotos();
    }

    private void fetchPopularPhotos() {
//        client id
//        51802e168cb34954bc8fda6fa3bb2e82
//
//        Popular
//        https://api.instagram.com/v1/media/popular?access_token=ACCESS-TOKEN

        String url = "https://api.instagram.com/v1/media/popular?client_id=" + CLIENT_ID;

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, null, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.i(PhotosActivity.APP_TAG, "Reloading new data success!");
                aPhotos.clear();
                JSONArray photosJSON = null;
                try {
                    photosJSON = response.getJSONArray("data");
                    if (photosJSON != null) {
                        for (int i=0; i < photosJSON.length(); i++) {
                            JSONObject photoJSON = photosJSON.getJSONObject(i);
                            InstagramPhoto photo = new InstagramPhoto();
                            // Map the property
                            // https://instagram.com/developer/endpoints/media/#get_media_popular
                            photo.username = photoJSON.getJSONObject("user").getString("username");
                            if (photoJSON.has("user") && photoJSON.getJSONObject("user").has("profile_picture")) {
                                photo.userProfilePictureUrl = photoJSON.getJSONObject("user").getString("profile_picture");
                            }
                            if (photoJSON.has("caption") &&
                                    photoJSON.optJSONObject("caption") != null && photoJSON.getJSONObject("caption").has("text")) {
                                try {
                                    photo.caption = photoJSON.getJSONObject("caption").getString("text");
                                } catch (Exception ev){
                                    photo.caption = "";
                                }
                            }
                            photo.imageUrl = photoJSON.getJSONObject("images").getJSONObject("standard_resolution")
                                    .getString("url");
                            photo.imageHeight = photoJSON.getJSONObject("images").getJSONObject("standard_resolution")
                                    .getInt("height");
                            photo.likesCount = photoJSON.getJSONObject("likes").getInt("count");
                            // comments count
                            photo.commentCounts = photoJSON.getJSONObject("comments").getInt("count");
                            if (photo.commentCounts > 0){
                                // iterate for each comments for this media and get the first 2
                                photo.comments = new ArrayList<InstagramComment>();
                                for (int j = 0;j<photo.commentCounts && j < 2; j++){
                                    JSONObject jsonComment = photoJSON.getJSONObject("comments").getJSONArray("data").getJSONObject(j);
                                    InstagramComment comment = new InstagramComment();
                                    comment.createdTime = jsonComment.getLong("created_time");
                                    comment.fromUserFullName = jsonComment.getJSONObject("from").getString("username");
                                    comment.fromUserProfilePic = jsonComment.getJSONObject("from").getString("profile_picture");
                                    comment.text = jsonComment.getString("text");
                                    photo.comments.add(comment);
                                }
                            }
                            // get the rest of the comments
                            photo.createdDate = photoJSON.getLong("created_time");
                            // media id
                            photo.id = photoJSON.getString("id");

                            // add to main arrayList
                            photos.add(photo);
                        }
                    }
                } catch (JSONException ev){
                    Log.d(PhotosActivity.APP_TAG, "Fail on " + photosJSON.toString());
                    ev.printStackTrace();
                }

                Log.d(APP_TAG, "Total photos : " + photos.size());

                // callback (once this proces done, need to notify adapter)
                aPhotos.notifyDataSetChanged();
                // to signal refresh finished (on swiperefreshlayout)
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_photos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
