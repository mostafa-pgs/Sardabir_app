package ir.weblogestaan.sardabir.Classes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by Windows on 1/22/15.
 */
public class Notif implements Serializable {

    public class NotifBase implements Serializable {
        public String notif_id,subject_name,created,seen,text;
        public User by,receiver;
        public String getID() {
            return this.notif_id;
        }

        public void setID(String _id) {
            this.notif_id = _id;
        }

        public NotifBase parseNotif(JSONObject jsonReminder) throws JSONException,IllegalAccessException {
            if (jsonReminder == null || jsonReminder.has("notif_id") == false) {
                return new PostNotif();
            }
            NotifBase n = new NotifBase();
            n.setID(jsonReminder.getString("notif_id"));
            for(Field f : n.getClass().getFields()) {
                if (jsonReminder.has(f.getName()))
                    f.set(n, jsonReminder.get(f.getName()));
                else
                    f.set(n, "");
            }
            return n;
        }

        @Override
        public String toString()
        {
            return this.subject_name+", "+this.notif_id;
        }
    }

    public  class PostNotif extends NotifBase implements Serializable {
        public Post subject;

        @Override
        public PostNotif parseNotif(JSONObject jsonReminder) throws JSONException, IllegalAccessException {
            if (jsonReminder == null || jsonReminder.has("notif_id") == false) {
                return new PostNotif();
            }

            PostNotif n = new PostNotif();
            n.setID(jsonReminder.getString("notif_id"));
            for(Field f : n.getClass().getFields()) {
                if (f.getName().equals("subject"))
                {
                    Post p = new Post();
                    if (jsonReminder.has(f.getName())) {
                        JSONArray subjectJson = jsonReminder.getJSONArray(f.getName());
                        for (int i = 0; i < subjectJson.length(); i++)
                            p = Post.parsePost(subjectJson.getJSONObject(i));
                    }
                    f.set(n, p);
                    continue;
                }
                if (f.getName().equals("by"))
                {
                    User p = new User();
                    if (jsonReminder.has(f.getName())) {
                        JSONArray subjectJson = jsonReminder.getJSONArray(f.getName());
                        for (int i = 0; i < subjectJson.length(); i++)
                            p = User.parseUser(subjectJson.getJSONObject(i));
                    }
                    f.set(n, p);
                    continue;
                }
                if (f.getName().equals("receiver"))
                {
                    User p = new User();
                    if (jsonReminder.has(f.getName())) {
                        JSONArray subjectJson = jsonReminder.getJSONArray(f.getName());
                        for (int i = 0; i < subjectJson.length(); i++)
                            p = User.parseUser(subjectJson.getJSONObject(i));
                    }
                    f.set(n, p);
                    continue;
                }
                if (jsonReminder.has(f.getName()))
                    f.set(n,jsonReminder.get(f.getName()));
                else
                    f.set(n,"");
            }

            return n;
        }
    }

    public class UserNotif extends NotifBase implements Serializable {
        public User subject;

        @Override
        public UserNotif parseNotif(JSONObject jsonReminder) throws JSONException, IllegalAccessException {
            if (jsonReminder == null || jsonReminder.has("notif_id") == false) {
                return new UserNotif();
            }

            UserNotif n = new UserNotif();
            n.setID(jsonReminder.getString("notif_id"));
            for(Field f : n.getClass().getFields()) {
                if (f.getName().equals("subject"))
                {
                    User p = new User();
                    if (jsonReminder.has(f.getName())) {
                        JSONArray subjectJson = jsonReminder.getJSONArray(f.getName());
                        for (int i = 0; i < subjectJson.length(); i++)
                            p = User.parseUser(subjectJson.getJSONObject(i));
                    }
                    f.set(n, p);
                    continue;
                }
                if (f.getName().equals("by"))
                {
                    User p = new User();
                    if (jsonReminder.has(f.getName())) {
                        JSONArray subjectJson = jsonReminder.getJSONArray(f.getName());
                        for (int i = 0; i < subjectJson.length(); i++)
                            p = User.parseUser(subjectJson.getJSONObject(i));
                    }
                    f.set(n, p);
                    continue;
                }
                if (f.getName().equals("receiver"))
                {
                    User p = new User();
                    if (jsonReminder.has(f.getName())) {
                        JSONArray subjectJson = jsonReminder.getJSONArray(f.getName());
                        for (int i = 0; i < subjectJson.length(); i++)
                            p = User.parseUser(subjectJson.getJSONObject(i));
                    }
                    f.set(n, p);
                    continue;
                }
                if (jsonReminder.has(f.getName()))
                    f.set(n,jsonReminder.get(f.getName()));
                else
                    f.set(n,"");
            }

            return n;
        }
    }

    public Notif()
    {

    }
}
