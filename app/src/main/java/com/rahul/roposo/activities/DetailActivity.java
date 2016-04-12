package com.rahul.roposo.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.rahul.roposo.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


public class DetailActivity extends AppCompatActivity {

    int position = 0;
    Button btnFollow2;

    public void changeFollowStatus(View view)
    {
        int wantedPosition = position;
        int firstPosition = CardsActivity.listViewStory.getFirstVisiblePosition() - CardsActivity.listViewStory.getHeaderViewsCount(); // This is the same as child #0
        int wantedChild = wantedPosition - firstPosition;
        View listViewRow = CardsActivity.listViewStory.getChildAt(wantedChild);
        Button btnListView = (Button)listViewRow.findViewById(R.id.btnFollow);
        if(btnFollow2.getText().equals("Follow"))
        {
            btnFollow2.setText("Following");
            btnListView.setText("Following");
            editJSONFile(CardsActivity.listStory.get(position).get("id"),"Follow");
        }
        else
        {
            btnFollow2.setText("Follow");
            btnListView.setText("Follow");
            editJSONFile(CardsActivity.listStory.get(position).get("id"),"Following");
        }
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_details);
        position = getIntent().getIntExtra("position",-1);

        btnFollow2 = (Button)findViewById(R.id.btnFollow2);
        int wantedPosition = position;
        int firstPosition = CardsActivity.listViewStory.getFirstVisiblePosition() - CardsActivity.listViewStory.getHeaderViewsCount();
        int wantedChild = wantedPosition - firstPosition;
        View listViewRow = CardsActivity.listViewStory.getChildAt(wantedChild);
        Button btnListView = (Button)listViewRow.findViewById(R.id.btnFollow);
        btnFollow2.setText(btnListView.getText());
        btnFollow2.setBackgroundColor(Color.parseColor("#1569c7"));
        btnFollow2.setTextColor(Color.WHITE);

        TextView txtTitle = (TextView)findViewById(R.id.txtTitle);
        txtTitle.setText( getIntent().getStringExtra("title")  );

        TextView txtDescription = (TextView)findViewById(R.id.txtDescription);
        txtDescription.setText(getIntent().getStringExtra("description") );

        ImageView imgProfile = (ImageView)findViewById(R.id.imgProfile);
        ImageDownloader imgDownload = new ImageDownloader();
        Bitmap downloadImg = null;
        try {
            downloadImg = imgDownload.execute(getIntent().getStringExtra("userImage")).get();
            imgProfile.setImageBitmap(downloadImg);
        } catch (Exception e) {
            e.printStackTrace();
        }



        TextView txtUsername = (TextView)findViewById(R.id.txtUsername);
        txtUsername.setText(getIntent().getStringExtra("username") + "\n");

        TextView txtAboutUser = (TextView)findViewById(R.id.txtAboutUser);
        txtAboutUser.setText(getIntent().getStringExtra("aboutUser") + "\n");

        TextView txtLabel = (TextView)findViewById(R.id.txtLabel);


        ImageView imgStory = (ImageView)findViewById(R.id.imgStory);
        ImageDownloader imgDownloader = new ImageDownloader();
        Bitmap downloadedImg = null;
        try {
            downloadedImg = imgDownloader.execute(getIntent().getStringExtra("image")).get();
            imgStory.setImageBitmap(downloadedImg);
        } catch (Exception e) {
            e.printStackTrace();
        }


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
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(this.openFileOutput("jsonData.txt", this.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
