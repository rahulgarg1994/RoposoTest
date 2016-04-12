package com.rahul.roposo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.rahul.roposo.adapter.StoryAdapter;
import com.rahul.roposo.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class CardsActivity extends AppCompatActivity {

    static List<HashMap<String,String>> listStory;
    List<HashMap<String,String>> listAuthor;
    static ListView listViewStory;
    public static String jsonData;

    public String readFromFile() {

        String ret = "";

        try {
            InputStream inputStream = openFileInput("jsonData.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ret;
    }

    public void parseJSONFileToList()
    {
        jsonData = readFromFile();
        if(jsonData.equals(""))
        {
            InputStream inputStream = getResources().openRawResource(R.raw.data);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            int ctr;
            try {
                ctr = inputStream.read();
                while (ctr != -1) {
                    byteArrayOutputStream.write(ctr);
                    ctr = inputStream.read();
                }
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            jsonData = byteArrayOutputStream.toString();
        }

        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for(int i = 0; i < jsonArray.length(); i++)
            {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String type = jsonObject.optString("type");
                if(type.equals("story"))
                {
                    String id = jsonObject.optString("id");
                    String title = jsonObject.optString("title");
                    String description = jsonObject.optString("description");
                    if(description.trim().equals(""))
                    {
                        description = "--no description available--";
                    }
                    String author = jsonObject.optString("db");
                    String url = jsonObject.optString("url");
                    String image = jsonObject.optString("si");
                    String verb = jsonObject.optString("verb");
                    String follow = jsonObject.optString("like_flag");

                    HashMap<String,String> hm = new HashMap<String,String>();
                    hm.put("id",id);
                    hm.put("title",title);
                    hm.put("description",description);
                    hm.put("db",author);
                    hm.put("url",url);
                    hm.put("image",image);
                    hm.put("verb",verb);
                    hm.put("follow",follow);

                    listStory.add(hm);
                }
                else
                {
                    String id = jsonObject.optString("id");
                    String username = jsonObject.optString("username");
                    String about = jsonObject.optString("about");
                    String profile = jsonObject.optString("image");
                    String url = jsonObject.optString("url");

                    HashMap<String,String> hm = new HashMap<String,String>();
                    hm.put("id",id);
                    hm.put("username",username);
                    hm.put("about",about);
                    hm.put("image",profile);
                    hm.put("url",url);

                    listAuthor.add(hm);

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void  generateListView()
    {
        StoryAdapter storyAdapter = new StoryAdapter(listStory,listAuthor,this);
        listViewStory.setAdapter(storyAdapter);

        listViewStory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String title = listStory.get(position).get("title");
                String image = listStory.get(position).get("image");
                String description = listStory.get(position).get("description");
                String storyURL= listStory.get(position).get("url");
                String username = "";
                String aboutUser = "";
                String userURL = "";
                String userImage = "";
                for (HashMap<String,String> hm : listAuthor)
                {
                    if(hm.get("id").equals(listStory.get(position).get("db")))
                    {
                        username = hm.get("username");
                        aboutUser = hm.get("about");
                        userURL = hm.get("url");
                        userImage = hm.get("image");
                        break;
                    }
                }

                Intent intent = new Intent(getApplicationContext(),DetailActivity.class);
                intent.putExtra("position",position);
                intent.putExtra("title",title);
                intent.putExtra("image",image);
                intent.putExtra("description",description);
                intent.putExtra("storyURL",storyURL);
                intent.putExtra("username",username);
                intent.putExtra("aboutUser",aboutUser);
                intent.putExtra("userURL",userURL);
                intent.putExtra("userImage",userImage);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_cards);

        listStory = new ArrayList<HashMap<String,String>>();
        listAuthor = new ArrayList<HashMap<String,String>>();
        listViewStory = (ListView)findViewById(R.id.listViewStory);

        parseJSONFileToList();

        generateListView();
    }

}
