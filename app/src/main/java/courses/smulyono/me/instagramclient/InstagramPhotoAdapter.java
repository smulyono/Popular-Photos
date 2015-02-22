package courses.smulyono.me.instagramclient;

import android.content.Context;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.Date;
import java.util.List;

import courses.smulyono.me.instagramclient.courses.smulyono.me.instagramclient.utils.DeviceDimensionsHelper;

/**
 * Created by smulyono on 2/21/15.
 */
public class InstagramPhotoAdapter extends ArrayAdapter<InstagramPhoto> {

    private class InstagramViewHolder {
        TextView tvCaption;
        TextView tvUsername;
        TextView tvTime;
        TextView tvLikes;

        ImageView ivPhoto;
        ImageView ivUserProfilePic;
    }


    public InstagramPhotoAdapter(Context context, List<InstagramPhoto> objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        InstagramPhoto photo = getItem(position);

        InstagramViewHolder viewHolder;

        if (convertView == null){
            viewHolder = new InstagramViewHolder();

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_photo, parent, false);

            viewHolder.tvCaption = (TextView) convertView.findViewById(R.id.tvCaption);
            viewHolder.ivPhoto  = (ImageView) convertView.findViewById(R.id.ivPhoto);
            viewHolder.tvUsername = (TextView) convertView.findViewById(R.id.tvUser);
            viewHolder.tvTime = (TextView) convertView.findViewById(R.id.tvTimecaption);
            viewHolder.tvLikes = (TextView) convertView.findViewById(R.id.tvLikes);
            viewHolder.ivUserProfilePic = (ImageView) convertView.findViewById(R.id.ivUserProfile);
            convertView.setTag(viewHolder);
        } else {
            // retrieve the viewholder
            viewHolder = (InstagramViewHolder) convertView.getTag();
        }

        // populate the data
        String userName ="<strong style='color:" + R.color.abc_search_url_text + "'>" + photo.username + "</strong>";
        // username
        viewHolder.tvUsername.setText(Html.fromHtml(userName));
        // time stamp
        // need * 1000; to accomodate the timestamp given in seconds instead of milliseconds
        String timeRelativeString = (String)DateUtils.getRelativeTimeSpanString(photo.createdDate * 1000, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
        viewHolder.tvTime.setText(timeRelativeString);
        // number of likes
        viewHolder.tvLikes.setText(NumberFormat.getInstance().format(photo.likesCount) + " likes");
        // caption
        viewHolder.tvCaption.setText(photo.caption);
        // clear out the image view (make sure the old images are not shown during waiting)
        viewHolder.ivPhoto.setImageResource(0);
        // resize to the width
        int screenWidth = DeviceDimensionsHelper.getDisplayWidth(getContext());
        // get the image (use picaso, since the image only given as URL from the JSON)
        Picasso.with(getContext()).load(photo.imageUrl)
                .placeholder(R.drawable.imgloading)
                .resize(screenWidth,0)
                .into(viewHolder.ivPhoto);

        // get the userprofile pic to always resize to 40,40
        viewHolder.ivUserProfilePic.setImageResource(0);
        if (!photo.userProfilePictureUrl.isEmpty()){
            Picasso.with(getContext()).load(photo.userProfilePictureUrl)
                    .placeholder(R.drawable.usericon)
                    .resize(40,40)
                    .into(viewHolder.ivUserProfilePic);
        } else {
            viewHolder.ivUserProfilePic.setImageResource(R.drawable.usericon);
        }
        return convertView;
    }
}
