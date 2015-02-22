package courses.smulyono.me.instagramclient;

import java.util.List;

/**
 * Created by smulyono on 2/21/15.
 */
public class InstagramPhoto {
    public String username;
    public String userProfilePictureUrl;
    public String caption;
    public String imageUrl;
    public String id;
    public int imageHeight;
    public int likesCount;
    public long createdDate;
    public int commentCounts;
    public List<InstagramComment> comments;
}
