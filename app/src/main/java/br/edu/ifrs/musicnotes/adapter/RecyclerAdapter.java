package br.edu.ifrs.musicnotes.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.Iterator;
import java.util.List;

import br.edu.ifrs.musicnotes.R;
import br.edu.ifrs.musicnotes.model.Album;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {

    private Context mContext;
    private List<Album> mAlbumList;

    public RecyclerAdapter(Context mContext, List<Album> mAlbumList) {
        this.mContext = mContext;
        this.mAlbumList = mAlbumList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(mContext).inflate(R.layout.adapter_spotify_albums, parent, false);
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.setIsRecyclable(false);

        Album album = mAlbumList.get(position);

        holder.albumName.setText(album.getTitle());

        for (Iterator<String> i = album.getArtists().iterator(); i.hasNext(); ) {
            holder.artistName.append(i.next());
            if (i.hasNext()) holder.artistName.append(", ");
        }

        holder.albumYear.setText(String.valueOf(album.getYear()));

        Glide.with(mContext).load(album.getImages().get("small")).into(holder.albumCover);
    }

    @Override
    public int getItemCount() {
        return mAlbumList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView albumName, artistName, albumYear;
        ImageView albumCover;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            albumName = itemView.findViewById(R.id.albumName);
            artistName = itemView.findViewById(R.id.artistName);
            albumYear = itemView.findViewById(R.id.albumYear);
            albumCover = itemView.findViewById(R.id.albumCover);
        }
    }
}