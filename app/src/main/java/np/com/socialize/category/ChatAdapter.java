package np.com.socialize.category;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import np.com.socialize.R;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    ArrayList<ChatMessage> messages;
    String userID ;
    ChatMessage message;
    OnChatItemClick onChatItemClick;




    public ChatAdapter(ArrayList<ChatMessage> mylist,OnChatItemClick onclickedChatItem) {

        this.onChatItemClick=onclickedChatItem;
        this.messages = mylist;

        userID=FirebaseAuth.getInstance().getUid();


    }


    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_item_chat_message,parent,false);
        return new ChatViewHolder(view,onChatItemClick);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {

         holder.bind_data(messages.get(position));
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }


    public class ChatViewHolder extends RecyclerView.ViewHolder{

         TextView textView,personName,time;
         ImageView img_hobbies, imgMsg;




        public ChatViewHolder(@NonNull View itemView, OnChatItemClick chatItemClick) {
            super(itemView);

            this.onChatItemClicked=chatItemClick;

            textView = itemView.findViewById(R.id.text_message);
            personName = itemView.findViewById(R.id.person_name);
            time=itemView.findViewById(R.id.time);
            img_hobbies=itemView.findViewById(R.id.img_hobbies);
            imgMsg=itemView.findViewById(R.id.imgMsg);



            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    onChatItemClicked.onItemCliked(message);
                    time.setVisibility(View.VISIBLE);
                }
            });


        }


        OnChatItemClick onChatItemClicked;



        public  void bind_data( ChatMessage chatMessage){


            message=chatMessage;

            if (chatMessage.getMessage() != null && !chatMessage.getMessage().isEmpty()) {
                textView.setText(chatMessage.getMessage());
                textView.setVisibility(View.VISIBLE);
            } else {
                textView.setVisibility(View.GONE);
            }


            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(chatMessage.getMessageTime());


            Date date =calendar.getTime();
            SimpleDateFormat simpleDateFormat = null;
            try {
                simpleDateFormat = new SimpleDateFormat("EEE, HH:mm");
            } catch (Exception e) {
                e.printStackTrace();
            }
            String display= simpleDateFormat.format(date);
            time.setText(display);

            time.setVisibility(View.INVISIBLE);








            if (chatMessage.getSenderId() != null && chatMessage.getSenderId().equals(userID)){


                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) textView.getLayoutParams();
                layoutParams.gravity = Gravity.END;
                textView.setLayoutParams(layoutParams);
                imgMsg.setLayoutParams(layoutParams);

                personName.setVisibility(View.GONE);
                img_hobbies.setVisibility(View.GONE);


            }else{
                personName.setText(chatMessage.getMessageUser());


              Picasso
                    .get()
                    .load(chatMessage.getProfile())
                      .placeholder(R.drawable.loading)
                      .into(img_hobbies);



                personName.setVisibility(View.VISIBLE);
                img_hobbies.setVisibility(View.VISIBLE);


            }


            if(chatMessage.getImageMessage()!=null){



               if(message.getImageMessage().length() !=0){

                   imgMsg.setVisibility(View.VISIBLE);

                   Picasso.
                           get()
                           .load(message.getImageMessage())
                           .into(imgMsg);


               }else{
                   imgMsg.setVisibility(View.GONE);
               }

            }else{
                imgMsg.setVisibility(View.GONE);

            }


        }
    }




}
