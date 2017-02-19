package pw.bmyo.www.bmyobaselibrary.bean.db;

import io.realm.RealmObject;

/**
 * Created by huang on 2016/11/24.
 */

public class UserInfo extends RealmObject {

    /**
     * article_num : 1
     * name :
     * type : 0
     * avatar : http://oek8vqzku.bkt.clouddn.com/default_avatar-avatar
     * user_id : 20
     * nickname : 123
     * draft_num : 2
     */

    private long user_id;
    private int article_num;
    private String name;
    private int type;
    private String avatar;
    private String nickname;
    private int draft_num;

    public int getArticle_num() {
        return article_num;
    }

    public void setArticle_num(int article_num) {
        this.article_num = article_num;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getDraft_num() {
        return draft_num;
    }

    public void setDraft_num(int draft_num) {
        this.draft_num = draft_num;
    }

    public UserInfo copy(UserInfo user) {
        if (user == null) {
            return this;
        }
        user_id = user.getUser_id();
        name = user.getName();
        type = user.getType();
        avatar = user.getAvatar();
        nickname = user.getNickname();
        return this;
    }

}
