package np.com.socialize.category;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import np.com.socialize.PrivateChat;
import np.com.socialize.R;

public class CurrentChatAdapter extends RecyclerView.Adapter<CurrentChatAdapter.FindFriendsViewHolder> {



      ArrayList<PrivateChat> users = new ArrayList<>();
      CheckedMessageIconInterface checkInterface;



    public CurrentChatAdapter(ArrayList<PrivateChat> user , CheckedMessageIconInterface checkedMessageIconInterface) {

        this.users=user;
        this.checkInterface =checkedMessageIconInterface;


    }


    @NonNull
    @Override
    public FindFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_item_find_friends_list,parent,false);
        return new FindFriendsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FindFriendsViewHolder holder, int position) {


         holder.bind(users.get(position));

    }

    @Override
    public int getItemCount() {
        return users.size();
    }





    public class FindFriendsViewHolder extends RecyclerView.ViewHolder{


        TextView txtName;
        ImageView imgMsgIcon;
        ImageView img_friend_profile;

        PrivateChat mUser;


        public FindFriendsViewHolder(@NonNull View itemView) {
            super(itemView);


            txtName =itemView.findViewById(R.id.txtName);
            imgMsgIcon=itemView.findViewById(R.id.imgMsgIcon);
            img_friend_profile = itemView.findViewById(R.id.img_friend_profile);
            imgMsgIcon.setVisibility(View.GONE);



            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if(mUser !=null){

                        checkInterface.OnItemClicked(mUser);

                    }

                }
            });
        }






        public  void bind(PrivateChat user){


            mUser=user;

            String currentUserId = FirebaseAuth.getInstance().getUid();
            User partner = user.getPartner(currentUserId);
            String displayName = partner != null && partner.getName() != null ? partner.getName() : "Chat";
            if (user.getLastMessage() != null && !user.getLastMessage().isEmpty()) {
                displayName = displayName + " — " + user.getLastMessage();
            }
            txtName.setText(displayName);

            if (partner != null && partner.getProfile_photo() != null) {
                Picasso.get()
                        .load(partner.getProfile_photo())
                        .placeholder(R.drawable.back)
                        .into(img_friend_profile);
            }




        }

    }



    public  interface  CheckedMessageIconInterface{

        public  void OnItemClicked(PrivateChat user);
    }



}



