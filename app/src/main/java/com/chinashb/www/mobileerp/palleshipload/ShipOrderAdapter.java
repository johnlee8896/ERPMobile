package com.chinashb.www.mobileerp.palleshipload;

/***
 * @date 创建时间 4/17/26 1:40 PM
 * @author 作者: liweifeng
 * @description
 */

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ShipOrderAdapter extends RecyclerView.Adapter<ShipOrderAdapter.ViewHolder> {

    private final ShipOrder shipOrder;

    public ShipOrderAdapter(ShipOrder shipOrder) {
        this.shipOrder = shipOrder;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StringBuilder sb = new StringBuilder();
        for (Container c : shipOrder.containers) {
            sb.append("📦 ").append(c.containerNo).append("\n");
            for (Pallet p : c.pallets) {
                sb.append("  🪵 ").append(p.palletNo).append("\n");
                for (String tag : p.materialTags) {
                    sb.append("    🏷 ").append(tag).append("\n");
                }
            }
        }
        holder.text.setText(sb.toString());
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView text;
        ViewHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(android.R.id.text1);
        }
    }
}
