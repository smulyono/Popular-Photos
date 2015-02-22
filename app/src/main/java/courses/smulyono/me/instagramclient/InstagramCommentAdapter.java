package courses.smulyono.me.instagramclient;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by smulyono on 2/22/15.
 */
public class InstagramCommentAdapter extends ArrayAdapter<InstagramComment> {

    private class CommentViewHolder {
        ImageView userProfilePic;
        TextView userComment;
    }

    private CommentViewHolder viewHolder;

    public InstagramCommentAdapter(Context context, List<InstagramComment> objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        InstagramComment comment = getItem(position);

        if (convertView == null){
            viewHolder = new CommentViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_comment, parent, false);

            // register the component into viewHolder
            viewHolder.userComment = (TextView) convertView.findViewById(R.id.tvComment);
            viewHolder.userProfilePic = (ImageView) convertView.findViewById(R.id.ivCommentUserPic);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (CommentViewHolder) convertView.getTag();
        }

        // update the information
        // user profile pic
        // get the userprofile pic to always resize to 40,40
        viewHolder.userProfilePic.setImageResource(0);
        if (!comment.fromUserProfilePic.isEmpty()){
            Picasso.with(getContext()).load(comment.fromUserProfilePic)
                    .placeholder(R.drawable.usericon)
                    .resize(80,80)
                    .into(viewHolder.userProfilePic);
        } else {
            viewHolder.userProfilePic.setImageResource(R.drawable.usericon);
        }
        // show the comments
        viewHolder.userComment.setText(comment.text);

        return convertView;
    }
}
