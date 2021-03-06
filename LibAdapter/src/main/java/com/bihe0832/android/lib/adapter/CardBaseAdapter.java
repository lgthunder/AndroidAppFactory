package com.bihe0832.android.lib.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.bihe0832.android.lib.adapter.item.BadDataTypeHolder;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;


/**
 * @author zixie
 */
public class CardBaseAdapter extends BaseMultiItemQuickAdapter<CardBaseModule, BaseViewHolder> {

    private Context mContext;
    private ArrayList mHeaderIDList = new ArrayList();

    public CardBaseAdapter(Context context, List data) {
        super(data);
        mContext = context;
    }

    protected void addItemToAdapter(Class<? extends CardBaseModule> module) {
        addItemToAdapter(module, false);
    }

    protected void addItemToAdapter(Class<? extends CardBaseModule> module, boolean isHeader) {
        int resID = CardInfoHelper.getInstance().getResIdByCardInfo(module);
        CardInfoHelper.getInstance().addCardItem(module);
        if (isHeader) {
            mHeaderIDList.add(resID);
        }
    }

    protected boolean isFixedViewType(int type) {
        return mHeaderIDList.contains(type);
    }

    @Override
    protected BaseViewHolder createBaseViewHolder(ViewGroup parent, int resID) {
        try {
            return super.createBaseViewHolder(parent, resID);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected BaseViewHolder onCreateDefViewHolder(ViewGroup parent, int cardId) {
        BaseViewHolder holder = null;
        if (BaseMultiItemQuickAdapter.TYPE_NOT_FOUND != cardId) {
            try {
                View itemView = getItemView(cardId, parent);
                holder = CardInfoHelper.getInstance().createViewHolder(cardId, itemView, mContext);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (holder == null) {
            holder = new BadDataTypeHolder(getItemView(getBadDataTypeHolderID(), parent), mContext);
        }
        return holder;
    }

    protected int getBadDataTypeHolderID() {
        return R.layout.card_bad_data_item;
    }

    @Override
    protected void convert(BaseViewHolder helper, CardBaseModule item) {
        try {
            if (helper instanceof CardBaseHolder) {
                ((CardBaseHolder) helper).initData(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
