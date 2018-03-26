package com.mindnotix.mnxchats.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mindnotix.mnxchats.R;
import com.mindnotix.mnxchats.model.ProfileImageEditModel;

import java.util.List;

/**
 * Created by Sridharan on 12/12/2017.
 */

public class ProfileImageBottomSheetAdapter extends RecyclerView.Adapter<ProfileImageBottomSheetAdapter.ItemHolder> {
    private List<ProfileImageEditModel> list;
    private OnItemClickListener onItemClickListener;

    public ProfileImageBottomSheetAdapter(List<ProfileImageEditModel> list) {
        this.list = list;
    }

    @Override
    public ProfileImageBottomSheetAdapter.ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.inflate_profile_image, parent, false);
        return new ItemHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(ProfileImageBottomSheetAdapter.ItemHolder holder, int position) {
        holder.bind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public static class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ProfileImageBottomSheetAdapter adapter;
        TextView textView;
        ImageView imageView;

        public ItemHolder(View itemView, ProfileImageBottomSheetAdapter parent) {
            super(itemView);
            itemView.setOnClickListener(this);
            this.adapter = parent;

            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            textView = (TextView) itemView.findViewById(R.id.textView);
        }

        public void bind(ProfileImageEditModel item)
        {
            textView.setText(item.getTitleId());
            imageView.setImageResource(item.getImageId());
        }

        @Override
        public void onClick(View v) {

            final OnItemClickListener listener = adapter.getOnItemClickListener();
            if (listener != null) {
                listener.onItemClick(this, getAdapterPosition());
            }

        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(ItemHolder item, int position);
    }

}
