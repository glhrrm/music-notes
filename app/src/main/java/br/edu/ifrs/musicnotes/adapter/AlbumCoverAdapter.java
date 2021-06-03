package br.edu.ifrs.musicnotes.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import br.edu.ifrs.musicnotes.R;
import br.edu.ifrs.musicnotes.model.Album;

public class AlbumCoverAdapter extends RecyclerView.Adapter<AlbumCoverAdapter.MyViewHolder> {

    private Context mContext;
    private List<Album> mAlbumList;

    public AlbumCoverAdapter(Context mContext, List<Album> mAlbumList) {
        this.mContext = mContext;
        this.mAlbumList = mAlbumList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(mContext).inflate(R.layout.adapter_album_cover, parent, false);
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.setIsRecyclable(false);

        Album album = mAlbumList.get(position);

        Glide.with(mContext).load(album.getImages().get("medium")).into(holder.albumCover);
    }

    @Override
    public int getItemCount() {
        return mAlbumList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView albumCover;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            albumCover = itemView.findViewById(R.id.albumCover);
        }
    }
}