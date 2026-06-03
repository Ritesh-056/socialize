package np.com.socialize;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import np.com.socialize.category.FindFriendsAdapter;
import np.com.socialize.category.User;
import np.com.socialize.category.UserDataViewModel;

public class FindFriendFragment extends Fragment  implements FindFriendsAdapter.CheckedMessageIconInterface {



    UserDataViewModel userDataViewModel;
    RecyclerView recyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


      //  model = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        userDataViewModel = new ViewModelProvider(getActivity()).get(UserDataViewModel.class);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {



         return  inflater.inflate(R.layout.fragment_second,container,false);

    //    return super.onCreateViewg(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


       recyclerView = view.findViewById(R.id.recyclerView);
       recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));



        userDataViewModel.fetchAllUser();

        userDataViewModel.getAllUser().observe(getViewLifecycleOwner(), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                if (users == null) {
                    return;
                }

                Log.d(TAG, "onChanged: OnFirstFragment" +users.size());



                FindFriendsAdapter  adapter = new FindFriendsAdapter((ArrayList<User>) users, FindFriendFragment.this);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();



            }
        });




    }


    private static final String TAG = "FirstFragment";



    @Override
    public void OnItemClicked(User user) {


        //check if alreay got the chat with this user, open chat acitivity
        //esle create new chat


        String receiverId = user.getId();
        String senderId = FirebaseAuth.getInstance().getUid();




//
//
//        CollectionReference reference = FirebaseFirestore.getInstance().collection("privateChat");
//
//
//        reference.whereIn("members", Arrays.asList(receiverId,senderId));
//
//        reference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//            @Override
//            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//
//
//                Log.d(TAG, "onSuccess: reverse:" + queryDocumentSnapshots.getDocuments().size());
//
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//
//                Log.d(TAG, "onFailure: ");
//            }
//        });



        userDataViewModel.openOrCreatePrivateChat(user, new UserDataViewModel.OnPrivateChatReadyListener() {
            @Override
            public void onReady(PrivateChat privateChat) {
                openChat(privateChat);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), "Could not start chat", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openChat(PrivateChat privateChat) {
        if (getContext() == null || privateChat.getPrivate_id() == null) {
            return;
        }
        String currentUserId = FirebaseAuth.getInstance().getUid();
        User partner = privateChat.getPartner(currentUserId);
        String title = partner != null && partner.getName() != null ? partner.getName() : "Chat";
        String image = partner != null ? partner.getProfile_photo() : null;

        Intent intent = new Intent(getContext(), ChatActivity.class);
        intent.putExtra("hobbies_item", title);
        intent.putExtra("hobbies_image", image);
        intent.putExtra("server_id", privateChat.getPrivate_id());
        intent.putExtra("type", "privateChat");
        startActivity(intent);
    }
}
