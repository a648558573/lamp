package com.example.administrator.light.Tree;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.light.ExpandablelistView;
import com.example.administrator.light.R;
import com.example.administrator.light.BaseActivity;
import com.example.administrator.light.map.MapActivity;
import com.example.administrator.light.other.AssetRatio;
import com.example.administrator.light.other.CentralizedFaultQuery;
import com.example.administrator.light.other.Daily;
import com.example.administrator.light.other.Energy_query;
import com.example.administrator.light.other.SingleFaultQuery;
import com.example.administrator.light.other.SingleParaHistory;
import com.example.administrator.light.other.Three_phase;
import com.example.administrator.light.system.CentralizingSwitch;
import com.example.administrator.light.system.CentralizingSwitch_Group;
import com.example.administrator.light.system.ElectricalParameter;
import com.example.administrator.light.system.SingleTimeSetting;
import com.example.administrator.light.system.Single_Dimming_TimeCtrl.SingleActivity;
import com.example.administrator.light.system.TimeControl;
import com.example.administrator.light.system.TimeControl_Group;

/**
 * Created by binyejiang on 2019/6/24.
 */

public class testExpandableList extends BaseActivity {
    /** Called when the activity is first created. */
    ExpandableListView expandableList;
    //TreeViewAdapter adapter;
    SuperTreeViewAdapter superAdapter;
    Button btnNormal,btnSuper;
    // Sample data set. children[i] contains the children (String[]) for groups[i].
    public String[] groups = { "xxxx好友", "xxxx同学", "xxxxx女人"};
    public String[][] child= {
            { "A君", "B君", "C君", "D君" },
            { "同学甲", "同学乙", "同学丙"},
            { "御姐", "萝莉" }
    };
    public String[] parent = { "xxxx好友", "xxxx同学"};
    List<String> list=new ArrayList<String >();
    List<List<String> > list1=new ArrayList<List<String>  >();
    List<List<List<String>> > list2=new ArrayList<List<List<String>>  >();
    public String[][][] child_grandson= {
            {{"A君"},
                    {"AA","AAA"}},
            {{"B君"},
                    {"BBB","BBBB","BBBBB"}},
            {{"C君"},
                    {"CCC","CCCC"}},
            {{"D君"},
                    {"DDD","DDDD","DDDDD"}},
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_tree);
        list.add("xxxx好友");
        list.add("xxxx通讯");
        for(int i=0;i<list.size();i++){
            list1.add(list);
        }
        for(int i=0;i<list1.size();i++){
            list2.add(list1);
        }
        Adapter();
   }
    public void Adapter() {
        ExpandableListAdapter adapter = new BaseExpandableListAdapter() {
            @Override
            public int getGroupCount() {
                return list.size();
            }

            @Override
            public int getChildrenCount(int groupPosition) {
                return list1.get(groupPosition).size();
            }

            @Override
            public Object getGroup(int groupPosition) {
                return list1.get(groupPosition);
            }

            @Override
            public Object getChild(int groupPosition, int childPosition) {
                return list1.get(groupPosition).get(childPosition);
            }

            public Object getChildright(int groupPosition, int childPosition) {
                return list1.get(groupPosition).get(childPosition);
            }

            @Override
            public long getGroupId(int groupPosition) {
                return groupPosition;
            }

            @Override
            public long getChildId(int groupPosition, int childPosition) {
                return childPosition;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }

            @Override
            public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
                LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
                View ll = inflater.inflate(R.layout.grouplayout,null);
                final TextView group_text = (TextView)ll.findViewById(R.id.group_text);
                ImageView group_img = (ImageView)ll.findViewById(R.id.group_img);
                RelativeLayout group_relative = (RelativeLayout)ll.findViewById(R.id.group_relative);
                group_img.setImageResource(R.drawable.devdown);
                group_text.setText(getGroup(groupPosition).toString());
                if(isExpanded) {
                    group_img.setImageResource(R.drawable.devup);
                }
                group_relative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //  Toast.makeText(ExpandablelistView.this,"niam",Toast.LENGTH_SHORT).show();
                        String groupname = list.get(groupPosition);
                        //根据不同功能需求，让点击子列表后转向不同的activity
                        Intent intent = new Intent();
                        intent.putExtra("groupname", groupname);
                    }
                });
                return ll;
            }

            @Override
            public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
                ExpandableListAdapter adapter1 = new BaseExpandableListAdapter() {
                    @Override
                    public int getGroupCount() {
                        return list1.get(groupPosition).size();
                    }

                    @Override
                    public int getChildrenCount(int qPosition) {
                        return list2.get(groupPosition).get(qPosition).size();
                    }

                    @Override
                    public Object getGroup(int qPosition) {
                        return list1.get(groupPosition).get(qPosition);
                    }

                    @Override
                    public Object getChild(int qPosition, int cqPosition) {
                        return list2.get(groupPosition).get(qPosition).get(cqPosition);
                    }

                    public Object getChildright(int groupPosition, int childPosition) {
                        return list1.get(groupPosition).get(childPosition);
                    }

                    @Override
                    public long getGroupId(int groupPosition) {
                        return groupPosition;
                    }

                    @Override
                    public long getChildId(int groupPosition, int childPosition) {
                        return childPosition;
                    }

                    @Override
                    public boolean hasStableIds() {
                        return true;
                    }

                    @Override
                    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
                        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
                        View ll = inflater.inflate(R.layout.grouplayout,null);
                        final TextView group_text = (TextView)ll.findViewById(R.id.group_text);
                        ImageView group_img = (ImageView)ll.findViewById(R.id.group_img);
                        RelativeLayout group_relative = (RelativeLayout)ll.findViewById(R.id.group_relative);
                        group_text.setText(getGroup(groupPosition).toString());
                        group_relative.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //  Toast.makeText(ExpandablelistView.this,"niam",Toast.LENGTH_SHORT).show();
                                String groupname = list.get(groupPosition);
                                Toast.makeText(testExpandableList.this,groupPosition+"",Toast.LENGTH_SHORT).show();
                                //根据不同功能需求，让点击子列表后转向不同的activity
                                Intent intent = new Intent();
                                intent.putExtra("groupname", groupname);
                            }
                        });
                        return ll;
                    }

                    @Override
                    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
                        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
                        View ll1 = inflater.inflate(R.layout.childlayout,null);
                        TextView left = (TextView)ll1.findViewById(R.id.left);
                        TextView right = (TextView)ll1.findViewById(R.id.right);
                        ImageView img = (ImageView)ll1.findViewById(R.id.img);
                        left.setText(getChild(groupPosition, childPosition).toString());
                        if(getChildright(groupPosition, childPosition).toString().trim().equals("connet")) {
                            right.setText("连接成功");
                            img.setImageResource(R.drawable.connect_success);
                        } else {
                            right.setText("掉线");
                            img.setImageResource(R.drawable.connect_fail);
                        }
                        return ll1;
                    }

                    @Override
                    public boolean isChildSelectable(int i, int i1) {
                        return true;
                    }
                };
                ExpandableListView expandablelistView =new ExpandableListView(testExpandableList.this);
                expandablelistView.setAdapter(adapter1);
                //子列表点击事件
                expandablelistView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                    @Override
                    public boolean onChildClick(ExpandableListView expandableListView, View convertView, int groupPosition, int childPosition, long l) {
                        String childname =list1.get(groupPosition).get(childPosition);
                        //根据不同功能需求，让点击子列表后转向不同的activity


                        return false;
                    }
                });
                return expandablelistView;
            }

            @Override
            public boolean isChildSelectable(int i, int i1) {
                return true;
            }
        };
        ExpandableListView expandablelistView = (ExpandableListView)findViewById(R.id.listtree);
        expandablelistView.setAdapter(adapter);
        //子列表点击事件
        expandablelistView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View convertView, int groupPosition, int childPosition, long l) {
                String childname =list1.get(groupPosition).get(childPosition);
                //根据不同功能需求，让点击子列表后转向不同的activity


                return false;
            }
        });
    }
    /**
     * 三级树形菜单的事件不再可用，本函数由三级树形菜单的子项（二级菜单）进行回调
     */
    OnChildClickListener stvClickEvent=new OnChildClickListener(){
        @Override
        public boolean onChildClick(ExpandableListView parent,
                                    View v, int groupPosition, int childPosition,
                                    long id) {
            String str="parent id:"+String.valueOf(groupPosition)+",children id:"+String.valueOf(childPosition);
            Toast.makeText(testExpandableList.this, str, 300).show();
            return false;
      }
    };
}
