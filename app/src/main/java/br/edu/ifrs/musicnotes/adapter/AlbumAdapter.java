package br.edu.ifrs.musicnotes.adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import br.edu.ifrs.musicnotes.R;
import br.edu.ifrs.musicnotes.helper.Firebase;
import br.edu.ifrs.musicnotes.helper.Helper;
import br.edu.ifrs.musicnotes.model.Album;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.MyViewHolder> {

    private Context context;
    private List<Album> albums;
    private ItemClickListener itemClickListener;

    public AlbumAdapter(List<Album> albums, ItemClickListener itemClickListener) {
        this.albums = albums;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();

        View item = LayoutInflater.from(context)
                .inflate(R.layout.adapter_album, parent, false);

        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.setIsRecyclable(false);

        Album album = albums.get(position);

        holder.itemView.setOnClickListener(v -> itemClickListener.onItemClick(album));

        showBasicData(holder, album);
        showExtraData(holder, album);
    }

    @Override
    public int getItemCount() {
        return albums.size();
    }

    private void showBasicData(MyViewHolder holder, Album album) {
        holder.title.setText(album.getTitle());
        holder.artists.setText(Helper.stringBuilder(album.getArtists()));
        holder.year.setText(String.valueOf(album.getYear()));
        Glide.with(context).load(album.getImages().get("medium")).into(holder.cover);
    }

    private void showExtraData(MyViewHolder holder, Album album) {
        Firebase.getAlbums().child(album.getId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Album album = snapshot.getValue(Album.class);
                            String albumIntegerRating = String.valueOf((int) (album.getRating() * 2));
                            holder.rating.setText(albumIntegerRating);
                            holder.rating.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public interface ItemClickListener {
        void onItemClick(Album album);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title, artists, year, rating;
        ImageView cover;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.adapterTitle);
            artists = itemView.findViewById(R.id.adapterArtists);
            year = itemView.findViewById(R.id.adapterYear);
            cover = itemView.findViewById(R.id.adapterCover);
            rating = itemView.findViewById(R.id.adapterRatingNumeric);
        }
    }
}