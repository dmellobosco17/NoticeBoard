package com.bosco.noticeboard;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Bosco on 11/6/2015.
 */
public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.NoticeViewHolder> {

    List<Notice> notices;

    NoticeAdapter(List<Notice> notices) {
        this.notices = notices;
    }

    @Override
    public NoticeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.notice_layout, parent, false);
        NoticeViewHolder pvh = new NoticeViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(NoticeViewHolder holder, int i) {
        holder.subject.setText(notices.get(i).subject);
        holder.channel.setText(notices.get(i).channelName);
        holder.channelPhoto.setImageResource(notices.get(i).channelImage);

        if (notices.get(i).priority == 2)
            holder.cv.setCardBackgroundColor(holder.cv.getResources().getColor(R.color.icon_important));

        //We are also setting entire Notice object to each card
        //We can access this object in onNoticeSelect() Method
        holder.cv.setTag(notices.get(i));
    }

    @Override
    public int getItemCount() {
        return notices.size();
    }

    public static class NoticeViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView subject;
        TextView channel;
        ImageView channelPhoto;

        NoticeViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            subject = (TextView) itemView.findViewById(R.id.subject);
            channel = (TextView) itemView.findViewById(R.id.channel);
            channelPhoto = (ImageView) itemView.findViewById(R.id.church_photo);
        }
    }

}