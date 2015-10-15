package ir.weblogestaan.sardabir.Classes;

import java.io.Serializable;
import java.lang.reflect.Field;

import android.util.Log;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

public class Post implements Serializable{
	private static final long serialVersionUID = 1652131379131L;
	ImageView imageView;
    public String nid,title,body,date_create,date_str_fa,date_str_en,image_url,blog_id,blog_name,blog_address,renewser,like_count,renews_count,total_posts,liked,reposted,link, position;
	public Subject subject;
	public Post()
	{

	}

	public static Post parsePost(JSONObject jsonReminder) throws JSONException, IllegalAccessException {
        Post n = new Post();
		if (jsonReminder == null || jsonReminder.has("nid") == false) {
			return n;
		}

		n.setID(jsonReminder.getString("nid"));
		for(Field f : n.getClass().getFields()) {
			if (f.getName().equals("subject"))
			{
				if (jsonReminder.has(f.getName()))
					f.set(n,Subject.parseSubject(jsonReminder.getJSONObject(f.getName())));
				else
					f.set(n,new Subject());
				continue;
			}
            if (jsonReminder.has(f.getName())) {
				f.set(n, jsonReminder.get(f.getName()));
			}
			else
				f.set(n,null);
		}

		return n;
	}

	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
	}

	public String getID() {
		return this.nid;
	}

	public void setID(String _id) {
		this.nid = _id;
	}

	public String getReposted() {
		return this.reposted;
	}

	public void setReposted(boolean _id) {
		if (_id) this.reposted = "yes";
		else
			this.reposted = "no";
	}
	
	@Override
	public String toString(){
		return "Title : "+title+", Summary : "+body+" and ID : "+nid+ "image : "+image_url+", blog_address : "+blog_address+" and subject : "+subject.name;
		
	}

}
