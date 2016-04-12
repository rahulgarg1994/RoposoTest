package com.rahul.roposo.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.rahul.roposo.R;
import com.rahul.roposo.activities.CardsActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StoryAdapter extends BaseAdapter implements ListAdapter {

    private List<HashMap<String,String>> listStory = new ArrayList<HashMap<String,String>>();
    private List<HashMap<String,String>> listAuthor = new ArrayList<HashMap<String,String>>();
    private Context context;

    public StoryAdapter(List<HashMap<String,String>> listStory, List<HashMap<String,String>> listAuthor, Context context) {
        this.listStory = listStory;
        this.listAuthor = listAuthor;
        this.context = context;
    }

    @Override
    public int getCount() {
        return listStory.size();
    }

    @Override
    public Object getItem(int position) {
        return listStory.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public class ImageDownloader extends AsyncTask<String,Void,Bitmap>
    {
        @Override
        protected Bitmap doInBackground(String... urls) {

            try {
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
                urlConnection.connect();
                InputStream in = urlConnection.getInputStream();
                Bitmap myImage = BitmapFactory.decodeStream(in);
                return myImage;
            } catch (Exception e) {
                e.printStackTrace();
                return  null;
            }
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.custom_listview_layout, null);
        }

        TextView txtTitle = (TextView)view.findViewById(R.id.txtTitle);
        txtTitle.setText(listStory.get(position).get("title"));
        txtTitle.setTypeface(null, Typeface.BOLD);


        TextView txtDescription = (TextView)view.findViewById(R.id.txtDescription);
        txtDescription.setText(listStory.get(position).get("description"));


        ImageView imgStory = (ImageView)view.findViewById(R.id.imgStory);
        ImageDownloader imgDownload = new ImageDownloader();
        Bitmap downloadImg = null;
        try {
            downloadImg = imgDownload.execute(listStory.get(position).get("image")).get();
            imgStory.setImageBitmap(downloadImg);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ImageView imgProfile = (ImageView)view.findViewById(R.id.imgProfile);
        String profile = "";
        for (HashMap<String,String> hm : listAuthor)
        {
            if(hm.get("id").equals(listStory.get(position).get("db")))
            {
                profile = hm.get("image");
                break;
            }
        }
        ImageDownloader imgDownloader = new ImageDownloader();
        Bitmap downloadedImg = null;
        try {
            downloadedImg = imgDownloader.execute(profile).get();
            imgProfile.setImageBitmap(downloadedImg);
        } catch (Exception e) {
            e.printStackTrace();
        }

        TextView txtVerb = (TextView)view.findViewById(R.id.txtVerb);
        txtVerb.setText(listStory.get(position).get("verb"));


        TextView txtAuthor = (TextView)view.findViewById(R.id.txtAuthor);
        String author = "";
        for (HashMap<String,String> hm : listAuthor)
        {
            if(hm.get("id").equals(listStory.get(position).get("db")))
            {
                author = hm.get("username");
                break;
            }
        }
        txtAuthor.setText(author);
        txtAuthor.setTypeface(null,Typeface.BOLD);


        final Button btnFollow = (Button)view.findViewById(R.id.btnFollow);
        btnFollow.setBackgroundColor(Color.parseColor("#297DDB"));
        btnFollow.setTextColor(Color.WHITE);

        if(listStory.get(position).get("follow").equals("false"))
        {
            btnFollow.setText("Follow");
        }
        else
        {
            btnFollow.setText("Following");
        }
        btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btnFollow.getText().equals("Follow"))
                {
                    btnFollow.setText("Following");
                    editJSONFile(listStory.get(position).get("id"),"Follow");
                }
                else
                {
                    btnFollow.setText("Follow");
                    editJSONFile(listStory.get(position).get("id"),"Following");
                }
            }
        });

        return view;
    }

    public void editJSONFile(String idToEdit, String followStatus)
    {
        try {
            JSONArray jsonArray = new JSONArray(CardsActivity.jsonData);
            for(int i = 0; i < jsonArray.length(); i++)
            {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String type = jsonObject.optString("type");
                if(type.equals("story"))
                {
                    String id = jsonObject.optString("id");
                    if(id.equals(idToEdit))
                    {
                        String likeFlagValue = "";
                        if(followStatus.equals("Follow"))
                        {
                            likeFlagValue = "true";
                        }
                        else
                        {
                            likeFlagValue = "false";
                        }
                        jsonObject.put("like_flag",likeFlagValue);
                        CardsActivity.jsonData = jsonArray.toString();
                        break;
                    }
                }
            }
            writeToFile(CardsActivity.jsonData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeToFile(String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("jsonData.txt", context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
