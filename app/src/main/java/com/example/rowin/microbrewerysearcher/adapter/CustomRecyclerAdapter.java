package com.example.rowin.microbrewerysearcher.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rowin.microbrewerysearcher.R;
import com.example.rowin.microbrewerysearcher.activity.MapsActivity;
import com.example.rowin.microbrewerysearcher.model.Brewery;

import java.util.ArrayList;

/**
 * Created by Rowin on 10/2/2017.
 */

public class CustomRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface OnItemClickListener{
        void onItemClick(Brewery brewery);
    }

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private ArrayList<Brewery> breweryList = new ArrayList<>();
    private OnItemClickListener listener;
    private Context context;

    public CustomRecyclerAdapter(Context context, ArrayList<Brewery> breweryList, OnItemClickListener listener){
        this.listener = listener;
        this.breweryList = breweryList;
        this.context = context;
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder{
        ImageView openClosedBrewery;
        TextView nameBrewery;
        TextView distanceFromUser;
        TextView openClosedText;

        ItemViewHolder(View itemView){
            super(itemView);

            openClosedBrewery = (ImageView) itemView.findViewById(R.id.openClosedImage);
            openClosedText = (TextView) itemView.findViewById(R.id.openClosedText);
            nameBrewery = (TextView) itemView.findViewById(R.id.recycler_brewery_name);
            distanceFromUser = (TextView) itemView.findViewById(R.id.distance);
        }

        void bind(final Brewery brewery, final OnItemClickListener onItemClickListener){
            Boolean breweryOpen = MapsActivity.isBreweryOpen(brewery.getOpeningTimes());

            nameBrewery.setText(brewery.getBreweryName());
            distanceFromUser.setText(context.getString(R.string.distance, brewery.getDistanceBetweenBrewery()));

            if (breweryOpen) {
                openClosedText.setText(R.string.brewery_opened);
                openClosedBrewery.setImageResource(R.drawable.green_round);
            } else {
                openClosedText.setText(R.string.brewery_closed);
                openClosedBrewery.setImageResource(R.drawable.red_round);
            }

            itemView.setOnClickListener(new View.OnClickListener(){
                @Override public void onClick(View v) {
                    onItemClickListener.onItemClick(brewery);
                }
            });
        }
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder{
        TextView title;

        HeaderViewHolder(View headerView){
            super(headerView);

            title = (TextView) headerView.findViewById(R.id.txtHeader);
        }

        void bind(){
            title.setText(context.getString(R.string.breweries_at_distance_from, breweryList.get(0).getBreweryName()));
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        if(viewType == TYPE_HEADER){
            View headerView = LayoutInflater.from(context).inflate(R.layout.recycler_header_item, parent, false);
            return new HeaderViewHolder(headerView);
        } else if(viewType == TYPE_ITEM){
            View itemView = LayoutInflater.from(context).inflate(R.layout.custom_recycler_row, parent, false);
            return new ItemViewHolder(itemView);
        }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof HeaderViewHolder){
            ((HeaderViewHolder) holder).bind();
        } else if(holder instanceof ItemViewHolder) {
            ((ItemViewHolder) holder).bind(breweryList.get(position), listener);
        }
    }

    @Override
    public int getItemViewType(int position){
        if(isPositionHeader(position)){
            return TYPE_HEADER;
        } else {
            return TYPE_ITEM;
        }
    }

    private boolean isPositionHeader(int position)
    {
        return position == 0;
    }

    @Override
    public int getItemCount() {
        return breweryList.size();
    }
}
