package ch.hevs.students.raclettedb.adapter;

import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;


import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;

import ch.hevs.students.raclettedb.R;
import ch.hevs.students.raclettedb.database.entity.CheeseEntity;
import ch.hevs.students.raclettedb.database.entity.ShielingEntity;
import ch.hevs.students.raclettedb.util.RecyclerViewItemClickListener;

public class RecyclerAdapter<T> extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private List<T> mData;
    private RecyclerViewItemClickListener mListener;


    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;
        ViewHolder(TextView textView) {
            super(textView);
            mTextView = textView;
        }
    }

    public RecyclerAdapter(RecyclerViewItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextView v = (TextView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view, parent, false);
        final ViewHolder viewHolder = new ViewHolder(v);
        v.setOnClickListener(view -> mListener.onItemClick(view, viewHolder.getAdapterPosition()));
        v.setOnLongClickListener(view -> {
            mListener.onItemLongClick(view, viewHolder.getAdapterPosition());
            return true;
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerAdapter.ViewHolder holder, int position) {
        T item = mData.get(position);
        if (item.getClass().equals(CheeseEntity.class))
            holder.mTextView.setText(((CheeseEntity) item).getName());
        if (item.getClass().equals(ShielingEntity.class))
            holder.mTextView.setText(((ShielingEntity) item).getName());
    }

    @Override
    public int getItemCount() {
        if (mData != null) {
            return mData.size();
        } else {
            return 0;
        }
    }

    public void setData(final List<T> data) {
        if (mData == null) {
            mData = data;
            notifyItemRangeInserted(0, data.size());
        } else {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return mData.size();
                }

                @Override
                public int getNewListSize() {
                    return data.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    if (mData instanceof CheeseEntity) {
                        return ((CheeseEntity) mData.get(oldItemPosition)).getId().equals(((CheeseEntity) data.get(newItemPosition)).getId());
                    }
                    if (mData instanceof ShielingEntity) {
                        return ((ShielingEntity) mData.get(oldItemPosition)).getId().equals(((ShielingEntity) data.get(newItemPosition)).getId());
                    }
                    return false;
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    if (mData instanceof CheeseEntity) {
                        CheeseEntity newCheese = (CheeseEntity) data.get(newItemPosition);
                        CheeseEntity oldCheese = (CheeseEntity) mData.get(newItemPosition);
                        return newCheese.getName().equals(oldCheese.getName())
                                && newCheese.getType().equals(oldCheese.getType())
                                && newCheese.getDescription().equals(oldCheese.getDescription())
                                //&& ((Integer)newCheese.getEan()).equals(((Integer)oldCheese.getEan())
                        ;
                    }
                    if (mData instanceof ShielingEntity) {
                        ShielingEntity newShieling = (ShielingEntity) data.get(newItemPosition);
                        ShielingEntity oldShieling = (ShielingEntity) mData.get(newItemPosition);
                        return newShieling.getName().equals(oldShieling.getName())
                                && newShieling.getDescription().equals(oldShieling.getDescription());
                    }
                    return false;
                }
            });
            mData = data;
            result.dispatchUpdatesTo(this);
        }
    }
}
