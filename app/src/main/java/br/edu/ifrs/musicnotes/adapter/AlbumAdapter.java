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

import java.util.Iterator;
import java.util.List;

import br.edu.ifrs.musicnotes.R;
import br.edu.ifrs.musicnotes.helper.Firebase;
import br.edu.ifrs.musicnotes.model.Album;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.MyViewHolder> {

    private Context mContext;
    private List<Album> mAlbumList;

    public AlbumAdapter(Context mContext, List<Album> mAlbumList) {
        this.mContext = mContext;
        this.mAlbumList = mAlbumList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(mContext).inflate(R.layout.adapter_album, parent, false);
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.setIsRecyclable(false);

        Album album = mAlbumList.get(position);
        setAlbumFromApi(holder, album);
        setAlbumFromDatabase(holder, album);
    }

    @Override
    public int getItemCount() {
        return mAlbumList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView albumName, artistName, albumYear, albumInfoSeparator, albumRating;
        ImageView albumCover;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            albumName = itemView.findViewById(R.id.albumName);
            artistName = itemView.findViewById(R.id.artistName);
            albumInfoSeparator = itemView.findViewById(R.id.albumInfoSeparator);
            albumYear = itemView.findViewById(R.id.albumYear);
            albumCover = itemView.findViewById(R.id.albumCover);
            albumRating = itemView.findViewById(R.id.albumRatingNumeric);
        }
    }

    /*
    Sets data from Spotify API passed through an Album bundle object to the adapter.
    These data are not stored into the app database, i.e., album title, artists, year and images.
     */
    private void setAlbumFromApi(MyViewHolder holder, Album album) {
        holder.albumName.setText(album.getTitle());

        for (Iterator<String> artist = album.getArtists().iterator(); artist.hasNext(); ) {
            holder.artistName.append(artist.next());
            if (artist.hasNext()) holder.artistName.append(", ");
        }

        holder.albumYear.setText(String.valueOf(album.getYear()));

        Glide.with(mContext).load(album.getImages().get("medium")).into(holder.albumCover);
    }

    /*
    Sets album rating from database (app-exclusive data) passed through an Album object to the adapter.
     */
    private void setAlbumFromDatabase(MyViewHolder holder, Album album) {
        Firebase.getAlbums().child(album.getId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Album album = snapshot.getValue(Album.class);
                            String albumIntegerRating = String.valueOf((int) (album.getRating() * 2));
                            holder.albumRating.setText(albumIntegerRating);
                            holder.albumRating.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}