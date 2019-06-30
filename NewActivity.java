package com.plugin;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.provider.Settings.Secure;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;
import com.utility.AsyncResponse;
import com.utility.SceenTrait;
import com.utility.CurlApi;

public class NewActivity extends Activity  implements AsyncResponse{
 @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         String package_name = getApplication().getPackageName();
        setContentView(getApplication().getResources().getIdentifier("activity_new", "layout", package_name));
        final Button button_signup =findViewById(getResources().getIdentifier("sign_up", "id", getPackageName()));
        final  EditText email=findViewById(getResources().getIdentifier("editText2", "id", getPackageName()));

        ShapeDrawable shape = new ShapeDrawable(new RectShape());
        shape.getPaint().setColor(Color.RED);
        shape.getPaint().setStyle(Paint.Style.STROKE);
        shape.getPaint().setStrokeWidth(3);
        SceenTrait sceenTrait=new SceenTrait(getApplicationContext());
        sceenTrait.delegate=this;
        sceenTrait.execute();
        // Assign the created border to EditText widget
        email.setBackground(shape);
        final EditText password=findViewById(getResources().getIdentifier("editText3", "id", getPackageName()));
        final EditText confirm_password=findViewById(getResources().getIdentifier("editText4", "id", getPackageName()));
      //  button_signup.setX(20);

        button_signup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               String passwrd_str=password.getText().toString();
               String confrm_psswd_str=confirm_password.getText().toString();
               Log.e("Password ::",passwrd_str);
                Log.e("Confirm Passeord ::",confrm_psswd_str);
               if(!(passwrd_str.trim() .equals(confrm_psswd_str.trim()))){
                   Toast toast=android.widget.Toast.makeText(getApplicationContext(),"Passwords did not  match",Toast.LENGTH_SHORT);
                   TextView toastMessage = (TextView) toast.getView().findViewById(android.R.id.message);
                   toastMessage.setTextColor(Color.RED);
                   toast.show();
               }else{
                   String android_id = Secure.getString(getApplicationContext().getContentResolver(),
                                  Secure.ANDROID_ID);
                   android.widget.Toast.makeText(getApplicationContext(), "Email ::"+email.getText().toString()+"Password ::"+passwrd_str, Toast.LENGTH_SHORT).show();
                   CurlApi curlApi=new CurlApi(getApplicationContext(),email.getText().toString(),passwrd_str,android_id);
                   curlApi.execute();
               }

            }
        });

        final Button button_cancel = findViewById(getResources().getIdentifier("cancel", "id", getPackageName()));
        button_cancel.setX(20);
        button_cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                finish();
            }
        });

            }

    @Override
    public void processFinish(String result) {

       // TextView editText=(TextView) findViewById(R.id.textView4);
       // editText.setX(120);
        try {
            JSONObject screen_obj = new JSONObject(result);
            //String panel_str=response_obj.getString("Panel1");
            Iterator<String> json_itr = screen_obj.keys();
            RelativeLayout panel_obj;
            boolean is_panel=false;
            //Log.d("Panel1",panel_str);
            while (json_itr.hasNext()){

                String panel = json_itr.next();
                String value =screen_obj.getString(panel);
                if(isJSONValid(value)){
                   // Log.d("JsonSense :: ",screen_obj.getString(panel));
                    //if(panel=="panels"){
                     //   panel_obj=findViewById(getResources().getIdentifier(panel, "id", getPackageName()));
                  //  }else{

                  //  }
                    JSONObject prop_obj=new JSONObject(value);
                    Iterator<String> nested_itr=prop_obj.keys();
                   // this.processFinish(value);
                   this.processJsonStr(value,panel);
                }else{

                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void processJsonStr(String json_str,String parent_key) throws JSONException {
        if(isJSONValid(json_str)){
            JSONObject  panel_json_obj=new JSONObject(json_str);
            Iterator<String> json_itr = panel_json_obj.keys();

            while (json_itr.hasNext()){
                String panel_str_id=json_itr.next();
                String prop_str =panel_json_obj.getString(panel_str_id);
                Log.d("processJsonStr :: ",prop_str);
             //   Log.e("Key process ::",panel_str_id);
                if(parent_key.equals("panels")){
                    if(isJSONValid(prop_str)){
                        LinearLayout panel_obj=findViewById(getResources().getIdentifier(panel_str_id, "id", getPackageName()));
                        if(panel_obj!=null){
                           // Log.d("jsonprocess ::",panel_str_id);
                            this.setPropOnElem(prop_str,panel_str_id,panel_obj);
                        }

                    }
                }else if(parent_key.equals("textboxes")){
                    EditText editTextobj=findViewById(getResources().getIdentifier(panel_str_id, "id", getPackageName()));
                 //   Log.e("TextBoxes ::",panel_str_id);
                    this.setPropOnElem(prop_str,panel_str_id,editTextobj);
                }else if(parent_key.equals("labels")){
                    TextView textobj=findViewById(getResources().getIdentifier(panel_str_id, "id", getPackageName()));
                    Log.e("labels ::",panel_str_id);
                    this.setPropOnElem(prop_str,panel_str_id,textobj);
                }else if(parent_key.equals("buttons")){
                    Button buttonobj=findViewById(getResources().getIdentifier(panel_str_id, "id", getPackageName()));
                    Log.e("buttons ::",panel_str_id);
                    this.setPropOnElem(prop_str,panel_str_id,buttonobj);
                }else{
                    LinearLayout mother_obj=(LinearLayout) findViewById(getResources().getIdentifier("parent_screen", "id", getPackageName()));
                    //
                    Log.d("mother screen props ::",prop_str);
                    this.setPropOnElem(prop_str,"parent_screen",mother_obj);
                }

            }
        }
    }

    public  void setPropOnElem(String jsonStr,String parentKey,View rel_obj) throws JSONException {
        JSONObject  panel_json_obj=new JSONObject(jsonStr);
        Iterator<String> json_itr = panel_json_obj.keys();
        int r=-1,g=-1,b=-1,width,height,x,y;
        width=rel_obj.getWidth();
        height=rel_obj.getHeight();
        Log.e("Height::"+height + " :: Width "+width+"  ",rel_obj.toString());
        Log.e("View Object",rel_obj.toString());

        while (json_itr.hasNext()){
            String prop_key=json_itr.next();
            String prop_val=panel_json_obj.getString(prop_key);


            if(isJSONValid(prop_val)){
                 this.setPropOnElem(prop_val,prop_key,rel_obj);
            }else{
                Log.d("setPropOnElemKey :: ",prop_key);
                if(prop_key.equals("r")){
                    r=Integer.parseInt(prop_val);
                }else if(prop_key.equals("g")){
                    g=Integer.parseInt(prop_val);
                }else if(prop_key.equals("b")){
                    b=Integer.parseInt(prop_val);
                }else if(prop_key.equals("x")){

                   rel_obj.setX(Integer.parseInt(prop_val));

                }else if(prop_key.equals("y")){
                  //  rel_obj.setY(Integer.parseInt(prop_val));
                }else if(prop_key.equals("height")){

                    //rel_obj.setLayoutParams(new LinearLayout.LayoutParams(width, height));
                    height=Integer.parseInt(prop_val);
                    if(!(parentKey.equals("parent_screen"))) {
                        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) rel_obj.getLayoutParams();
                        lp.height = height;
                        rel_obj.setLayoutParams(lp);
                    }
                }else if(prop_key.equals("width")){
                    width=Integer.parseInt(prop_val);
                    if(!(parentKey.equals("parent_screen"))) {
                        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) rel_obj.getLayoutParams();
                        lp.width = width;
                        rel_obj.setLayoutParams(lp);
                    }
                }else if(prop_key.equals("font_style")){
                    Map sysfont_map=getSysFontList();
                    String font_style=prop_val;
                    Typeface typeface=(Typeface) sysfont_map.get(font_style);
                    if(typeface!=null){
                        ((TextView) rel_obj).setTypeface(typeface);
                    }else{
                        if(font_style.equals("italic")){
                            ((TextView) rel_obj).setTypeface(null, Typeface.ITALIC);
                        }else if(font_style.equals("bold_italic")){
                            ((TextView) rel_obj).setTypeface(null, Typeface.BOLD_ITALIC);
                        }else if(font_style.equals("normal")){
                            ((TextView) rel_obj).setTypeface(null, Typeface.BOLD);
                        }else if(font_style.equals("bold")){
                            ((TextView) rel_obj).setTypeface(null, Typeface.BOLD);
                        }
                    }

                }else if(prop_key.equals("font_size")){
                    ((TextView) rel_obj).setTextSize(TypedValue.COMPLEX_UNIT_SP,Integer.parseInt(prop_val));
                }
//                if(!(parentKey.equals("parent_screen"))){
//
//                    Log.e("Height orig :: ", Integer.toString(lp.height));
//                    Log.e("Height new ::",Integer.toString(height));
//                    if(height!=lp.height){
//                        lp.width=width;
//                        Log.i("Height Set ->",Integer.toString(height));
//
//
//
//
//                    }
//
//                }


                if(parentKey.equals("txt_color")){
                    if(r!=-1 && g!=-1 && b!=-1){
                        if(rel_obj instanceof  TextView){
                            ((TextView) rel_obj).setTextColor(Color.rgb(r,g,b));
                        }else if(rel_obj instanceof  Button){
                            // Log.e("Button",rel_obj.toString());
                            //Button myButton =(android.widget.Button) rel_obj;
                            //myButton.setTextColor(Color.rgb(r,g,b));
                            ((Button) rel_obj).setTextColor(Color.rgb(r,g,b));
                        }

                    }
                }else if(parentKey.equals("bg_color")){
                    if(r!=-1 && g!=-1 && b!=-1){
                        Log.e("View Object bg color",rel_obj.toString());
                        rel_obj.setBackgroundColor(Color.rgb(r,g,b));
                    }
                }else{
                    if(parentKey.equals("parent_screen")){
                        rel_obj.setBackgroundColor(Color.rgb(r,g,b));
                    }
                }
             //  Log.d("value ::",prop_val);
            }
        }
    }



        public boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

    public Map<String,Typeface> getSysFontList(){
        Map<String, Typeface> sSystemFontMap=null;
        Field f = null;
        try {
            f = Typeface.class.getDeclaredField("sSystemFontMap");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        f.setAccessible(true);
        Typeface typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL);
        try {
            sSystemFontMap = (Map<String, Typeface>) f.get(typeface);
            Log.d("TypeFaceMapFonts", sSystemFontMap.toString());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return sSystemFontMap;
    }

}
