package pl.edu.pb.shoppingapp.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import pl.edu.pb.shoppingapp.entity.Notification;
import pl.edu.pb.shoppingapp.model.NotificationViewModel;
import pl.edu.pb.shoppingapp.R;
import pl.edu.pb.shoppingapp.databinding.FragmentNotificationsBinding;

public class NotificationsFragment extends Fragment {
    private FragmentNotificationsBinding binding;
    private NotificationViewModel notificationViewModel;

    private static final String TAG = "NOTIFICATIONS_FRAGMENT";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);

        binding.backBtn.setOnClickListener(v -> {
            if (isStateSaved()) {
                requireActivity().onBackPressed();
                requireActivity().overridePendingTransition(androidx.navigation.ui.R.anim.nav_default_pop_enter_anim, androidx.navigation.ui.R.anim.nav_default_pop_exit_anim);
            } else {
                Fragment fragment = new MoreFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager
                        .beginTransaction()
                        .setCustomAnimations(androidx.navigation.ui.R.anim.nav_default_pop_enter_anim, androidx.navigation.ui.R.anim.nav_default_pop_exit_anim)
                        .replace(R.id.main_view, fragment)
                        .addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.notifications_recyclerview);

        final NotificationAdapter adapter = new NotificationAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        notificationViewModel = new ViewModelProvider(this).get(NotificationViewModel.class);
        notificationViewModel.findAll().observe(getViewLifecycleOwner(), adapter::setNotifications);
    }

    private class NotificationHolder extends RecyclerView.ViewHolder {
        private TextView notificationTitleTextView;
        private TextView notificationBodyTextView;
        private Notification notification;

        public NotificationHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.notification_item_list, parent, false));

            notificationTitleTextView = itemView.findViewById(R.id.notification_title);
            notificationBodyTextView = itemView.findViewById(R.id.notification_body);

            View notificationItem = itemView.findViewById(R.id.notification_delete);

            notificationItem.setOnClickListener(v -> {
                notificationViewModel.delete(notification);
            });
        }

        public void bind(Notification notification) {
            notificationTitleTextView.setText(notification.getTitle());
            notificationBodyTextView.setText(notification.getBody());
            this.notification = notification;
        }
    }

    private class NotificationAdapter extends RecyclerView.Adapter<NotificationHolder> {
        private List<Notification> notifications;

        @NonNull
        @Override
        public NotificationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new NotificationHolder(getLayoutInflater(), parent);
        }

        @Override
        public void onBindViewHolder(@NonNull NotificationHolder holder, int position) {
            if (notifications != null) {
                Notification notification = notifications.get(position);
                holder.bind(notification);
            } else {
                Log.d(TAG, "No notifications");
            }
        }

        @Override
        public int getItemCount() {
            if (notifications != null) {
                return notifications.size();
            } else {
                return 0;
            }
        }

        public void setNotifications(List<Notification> notifications) {
            this.notifications = notifications;
            notifyDataSetChanged();
        }
    }
}