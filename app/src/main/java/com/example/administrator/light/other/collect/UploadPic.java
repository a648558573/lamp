package com.example.administrator.light.other.collect;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.administrator.light.BaseActivity;
import com.example.administrator.light.R;
import com.example.administrator.light.VollyQueue;
import com.example.administrator.light.util.FileImageUtils;
import com.example.administrator.light.util.SharedPreferencesUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by JO on 2016/5/26.
 */
public class UploadPic extends BaseActivity implements View.OnClickListener {
    private ImageView addPic, Pic;
    private TextView tvName;
    private EditText editName;
    private Button bt;
    private ProgressDialog progressDialog = null;
    private Spinner spinner;
    private ArrayList<String> smallTreeList;
    private ArrayAdapter<String> smallTreeAdapter;
    private String rootURL, account = null, password = null;
    private String DevGroup = "区域分组";
    private boolean isSelectPIC = false;

    /*用来标识请求照相功能的activity*/
    private static final int CAMERA_WITH_DATA = 3023;
    /*用来标识请求gallery的activity*/
    private static final int PHOTO_PICKED_WITH_DATA = 3021;

    /*拍照的照片存储位置*/
    private static final File PHOTO_DIR = new File(Environment.getExternalStorageDirectory() + "/DCIM/Light");

    private File mCurrentPhotoFile;//照相机拍照得到的图片
    private Bitmap tempBitmap;//缓存要上传的文件 = FileImageUtils.getImage(mCurrentPhotoFile.getPath());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collect_upload);
        init();
        getSmallTree(DevGroup, account, password);
    }

    private void init() {
        getToolbarTitle().setText("上传图片");
        getToolbarRightTv().setText("列表  ");
        getToolbarRightTv().setTextSize(16);
        getToolbarRightTv().setVisibility(View.VISIBLE);
        getToolbarRightTv().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(UploadPic.this, PicList.class);
                startActivity(intent);
            }
        });

        rootURL = (String) SharedPreferencesUtils.getParam(this, "rootURL", "");
        account = (String) SharedPreferencesUtils.getParam(this, "username", "");
        password = (String) SharedPreferencesUtils.getParam(this, "password", "");
        smallTreeList = new ArrayList<String>();

        addPic = (ImageView)findViewById(R.id.upload_add_pic);
        Pic = (ImageView)findViewById(R.id.pic);
        tvName = (TextView)findViewById(R.id.upload_tv);
        editName = (EditText)findViewById(R.id.upload_edit);
        spinner = (Spinner)findViewById(R.id.upload_spinner);
        bt = (Button)findViewById(R.id.upload_bt);
        addPic.setOnClickListener(this);
        bt.setOnClickListener(this);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                tvName.setText(spinner.getSelectedItem().toString().trim().split("-")[0] + "-");
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.upload_add_pic:
                doPickPhotoAction();
                break;
            case R.id.upload_bt:
                if(!isSelectPIC) {
                    Toast.makeText(getApplicationContext(), "请选择图片", Toast.LENGTH_SHORT).show();
                } else if (editName.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "图片名为空", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog = ProgressDialog.show(UploadPic.this, null, "正在上传...", true);
                    // 开启一个子线程，进行网络操作，等待有返回结果，使用handler通知UI
                    new Thread(networkTask).start();
                }
                break;
            default:
                break;
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String val = data.getString("value");
            Toast.makeText(getApplicationContext(), val, Toast.LENGTH_SHORT).show();
            System.out.println("请求结果为-->" + val);
            // UI界面的更新等相关操作
            progressDialog.dismiss();
        }
    };

    //网络操作相关的子线程
    Runnable networkTask = new Runnable() {
        @Override
        public void run() {
            // 在这里进行 http request.网络请求相关操作
            PHOTO_DIR.mkdirs();// 创建照片的存储目录
            File tempFile = new File(PHOTO_DIR, "temp.jpg");//将要保存图片的路径
            FileImageUtils.saveBitmapFile(tempBitmap, tempFile);

            String result = FileImageUtils.uploadFile(tempFile,
                    tvName.getText().toString() + editName.getText().toString(),
                    rootURL + "/Tree/upload");
            if(tempFile.exists()) {
                //System.out.println("The tempFile exists!");
                tempFile.delete();
            }
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putString("value", result);
            msg.setData(data);
            handler.sendMessage(msg);
        }
    };

    private void getSmallTree(String DevGroup, String account, String password) {
        try {
            progressDialog = ProgressDialog.show(UploadPic.this, null, "加载中...", true);
            String URL = rootURL + "/Tree/DevInfoGroup?DevGroup="+ URLEncoder.encode(DevGroup, "utf-8").trim()
                    + "&log_name=" + account
                    + "&log_pass=" + password + "&sn_node_mode=1";
            System.out.println(URL);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            System.out.println(response);
                            progressDialog.dismiss();
                            if(!response.isEmpty() && !response.trim().equals("")) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    JSONArray jsonArray = jsonObject.getJSONArray("smalltree");
                                    smallTreeList.clear();
                                    for(int i = 0; i < jsonArray.length(); i++) {
                                        JSONArray itemArray = jsonArray.getJSONArray(i);
                                        for(int j = 0; j < itemArray.length(); j++) {
                                            smallTreeList.add(itemArray.getString(j));
                                        }
                                    }
                                    System.out.println(smallTreeList);
                                    smallTreeAdapter = new ArrayAdapter<String>(getApplicationContext(),
                                            R.layout.custom_spinner, smallTreeList);
                                    spinner.setAdapter(smallTreeAdapter);
                                } catch (JSONException e) {
                                    Toast.makeText(getApplicationContext(), "用户名加载失败", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "用户加载找不到接口!!!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Toast.makeText(getApplicationContext(), "网络异常", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
            //设置请求的Tag标签，可以在全局请求队列中通过Tag标签进行请求的查找
            stringRequest.setTag("");
            //将请求加入全局队列中
            VollyQueue.getHttpQueues().add(stringRequest);
        } catch (Exception e) { //UnsupportedEncodingException
            e.printStackTrace();
            progressDialog.dismiss();
        }
    }

    private void doPickPhotoAction() {
        final Context dialogContext = new ContextThemeWrapper(UploadPic.this,
                android.R.style.Theme_Light);
        String[] choices;
        choices = new String[2];
        choices[0] = "拍照";  //拍照
        choices[1] = "从相册中选择";  //从相册中选择
        final ListAdapter adapter = new ArrayAdapter<String>(dialogContext,
                android.R.layout.simple_list_item_1, choices);

        final AlertDialog.Builder builder = new AlertDialog.Builder(dialogContext);
        builder.setSingleChoiceItems(adapter, -1,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        switch (which) {
                            case 0: {
                                String status = Environment.getExternalStorageState();
                                if (status.equals(Environment.MEDIA_MOUNTED)) {//判断是否有SD卡
                                    doTakePhoto();// 用户点击了从照相机获取
                                } else {
                                    Toast.makeText(UploadPic.this, "没有SD卡", Toast.LENGTH_SHORT);
                                }
                                break;
                            }
                            case 1:
                                doPickPhotoFromGallery();// 从相册中去获取
                                break;
                        }
                    }
                });
        builder.create().show();
    }

    /**
     * 拍照获取图片
     */
    protected void doTakePhoto() {
        try {
            PHOTO_DIR.mkdirs();// 创建照片的存储目录
            mCurrentPhotoFile = new File(PHOTO_DIR, getPhotoFileName());// 给新照的照片文件命名
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mCurrentPhotoFile));
            startActivityForResult(intent, CAMERA_WITH_DATA);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "photoPicker Not Found", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 用当前时间给取得的图片命名
     */
    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date) + ".jpg";
    }

    // 请求Gallery程序
    protected void doPickPhotoFromGallery() {
        try {
            // 请求Gallery的intent
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            //intent.putExtra("crop", "true");
            //intent.putExtra("aspectX", 1);
            //intent.putExtra("aspectY", 1);
            //intent.putExtra("outputX", 80);
            //intent.putExtra("outputY", 80);
            intent.putExtra("return-data", true);
            startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "photoPicker Not Found1", Toast.LENGTH_SHORT).show();
        }
    }

    // 判断Camera和Gally各自的返回情况
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;
        switch (requestCode) {
            case PHOTO_PICKED_WITH_DATA: {// 调用Gallery返回的
                System.out.println("resultCode:" + resultCode);
                System.out.println("data:" + data);
                System.out.println("data:" + data.getData());

                Uri selectedImage = data.getData(); //获取系统返回的照片的Uri
                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);//从系统表中查询指定Uri对应的照片
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);  //获取照片路径
                cursor.close();

                tempBitmap = BitmapFactory.decodeFile(picturePath);
                //tempBitmap = data.getParcelableExtra("data");
                // 下面就是显示照片了
                System.out.println(tempBitmap);
                //缓存用户选择的图片
//                img = getBitmapByte(photo);
//                mEditor.setPhotoBitmap(photo);
                isSelectPIC = true;
                Pic.setImageBitmap(tempBitmap);
                System.out.println("set new photo");
                break;
            }
            case CAMERA_WITH_DATA: {// 照相机程序返回的
                if(mCurrentPhotoFile == null) System.out.println("null!!");
                System.out.println(mCurrentPhotoFile.getPath());
                tempBitmap = BitmapFactory.decodeFile(mCurrentPhotoFile.getPath());
                isSelectPIC = true;
                Pic.setImageBitmap(tempBitmap);
                System.out.println("set new photo");
                break;
            }
        }
    }

}
