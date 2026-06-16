package com.example.uitpayapp.merchant.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uitpayapp.R;
import com.example.uitpayapp.merchant.home.home_model.SellerHistoryOrder;
import com.example.uitpayapp.merchant.home.home_model.SellerOrder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ConfirmedOrdersFragment extends Fragment {

    private RecyclerView rvOrders;
    private SellerOrderAdapter adapter;
    private SellerOrderViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_seller_orders_list, container, false);
        rvOrders = view.findViewById(R.id.rv_orders_list);
        rvOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(SellerOrderViewModel.class);

        adapter = new SellerOrderAdapter(getContext(), new ArrayList<>(), new SellerOrderAdapter.OnOrderActionListener() {
            @Override
            public void onAccept(SellerOrder order) {
                // Đối với tab Đã xác nhận, "Accept" nghĩa là hoàn tất và chuyển sang lịch sử
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                String pickupTime = sdf.format(System.currentTimeMillis());
                SellerHistoryOrder historyOrder = new SellerHistoryOrder(
                        order.getId(),
                        order.getCustomerName(),
                        "Đang giao",
                        pickupTime,
                        order.getNumberOfDishes(),
                        "0.1 km",
                        "Hôm nay",
                        "Đang giao",
                        order.getTotalPrice()
                );
                viewModel.moveToHistory(historyOrder, order);
            }

            @Override
            public void onSeeMore(SellerOrder order) {
                if (getActivity() instanceof SellerHomeActivity) {
                    ((SellerHomeActivity) getActivity()).showOrderDetailBottomSheet(order);
                }
            }
        });

        rvOrders.setAdapter(adapter);

        viewModel.getConfirmedOrders().observe(getViewLifecycleOwner(), orders -> {
            adapter.updateData(orders);
        });
    }
}
