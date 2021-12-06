package com.lunchteam.lunch.menu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.lunchteam.lunch.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder>{
    private ArrayList<JSONObject> mData = null ;

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_menu;
        TextView tv_visit;
        TextView tv_type;

        ViewHolder(View itemView) {
            super(itemView) ;

            // 뷰 객체에 대한 참조. (hold strong reference)
            tv_menu = itemView.findViewById(R.id.tv_menu) ;
            tv_visit = itemView.findViewById(R.id.tv_visit) ;
           //tv_reviewpt = itemView.findViewById(R.id.tv_reviewpt) ;
            tv_type = itemView.findViewById(R.id.tv_type) ;
        }
    }

    // 생성자에서 데이터 리스트 객체를 전달받음.
    MenuAdapter(ArrayList<JSONObject> list) {
        mData = list ;
    }

    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @Override
    public MenuAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.menu_item, parent, false) ;
        MenuAdapter.ViewHolder vh = new MenuAdapter.ViewHolder(view) ;

        return vh ;
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(MenuAdapter.ViewHolder holder, int position) {
        JSONObject menu = mData.get(position) ;

        try {
            int id = menu.getInt("id");
            String location = menu.getString("location") ;
            String name = menu.getString("name") ;
            String menuType = menu.getString("menuType") ;
            String menuName = menu.getString("menuName") ;
            int visitCount = menu.getInt("visitCount");
            String recentVisit = menu.getString("recentVisit") ;
            String insertDateTime = menu.getString("insertDateTime") ;

            holder.tv_menu.setText(name) ;
            holder.tv_visit.setText(Integer.toString(visitCount));
            holder.tv_type.setText(menuName);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return mData.size() ;
    }
}