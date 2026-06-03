package np.com.socialize.category;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import np.com.socialize.PrivateChat;

public class UserDataViewModel extends ViewModel {




    FirebaseFirestore db;
    FirebaseAuth mAuth;

    public UserDataViewModel() {

        db=FirebaseFirestore.getInstance();
        mAuth=FirebaseAuth.getInstance();
    }




    MutableLiveData<List<User>> allUser = new MutableLiveData<>();

    public MutableLiveData<List<User>> getAllUser() {
        return allUser;
    }
    MutableLiveData<User> currentUser = new MutableLiveData<>();


    public MutableLiveData<User> getCurrentUser() {
        return currentUser;
    }
    MutableLiveData<State> currentState = new MutableLiveData<>();
    public MutableLiveData<State> getCurrentState() {
        return currentState;
    }



    public void getData() {


        if (mAuth.getUid() != null){

            db.collection("users")
                    .document(mAuth.getUid())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {


                            try {
                                User user= documentSnapshot.toObject(User.class);
                                user.setId(documentSnapshot.getId());
                                Hawk.put("User", user);

                                currentUser.postValue(user);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });



        }



    }

    public void addData(User user) {


        if (mAuth.getUid() != null){

            currentState.postValue(State.LOADING);

            db.collection("users")
                    .document(mAuth.getUid())
                    .set(user)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            currentUser.postValue(user);
                            user.setId(mAuth.getUid());
                            Hawk.put("User", user);

                            currentState.postValue(State.SUCCESS);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {


                            currentState.postValue(State.FAILED);
                        }
                    });

        }



    }



    public  void fetchAllUser(){

          db.collection("users")
                  .get()
                  .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                      @Override
                      public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                          ArrayList<User> users = new ArrayList<>();

                          for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots.getDocuments()){

                              User user= documentSnapshot.toObject(User.class);
                              if (user == null) {
                                  continue;
                              }
                              user.setId(documentSnapshot.getId());
                              if (!documentSnapshot.getId().equals(mAuth.getUid())) {
                                  users.add(user);
                              }
                          }


                          allUser.postValue(users);
                          Log.d(TAG, "onSuccess: "+queryDocumentSnapshots.getDocuments().size());




                      }
                  })
                  .addOnFailureListener(new OnFailureListener() {
                      @Override
                      public void onFailure(@NonNull Exception e) {

                          Log.d(TAG, "onFailure: "+e.getMessage());
                          e.printStackTrace();
                      }
                  });

    }


    public interface OnPrivateChatReadyListener {
        void onReady(PrivateChat privateChat);
        void onFailure(Exception e);
    }

    public void openOrCreatePrivateChat(User user, OnPrivateChatReadyListener listener) {
        if (mAuth.getUid() == null || user == null || user.getId() == null) {
            if (listener != null) {
                listener.onFailure(new IllegalStateException("Not signed in or invalid user"));
            }
            return;
        }

        db.collection("privateChat")
                .whereArrayContains("members", mAuth.getUid())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    PrivateChat existing = null;
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                        PrivateChat chat = documentSnapshot.toObject(PrivateChat.class);
                        if (chat != null && chat.getMembers() != null && chat.getMembers().contains(user.getId())) {
                            existing = chat;
                            existing.setPrivate_id(documentSnapshot.getId());
                            break;
                        }
                    }
                    if (existing != null) {
                        if (listener != null) {
                            listener.onReady(existing);
                        }
                        return;
                    }
                    createNewChat(user, listener);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "openOrCreatePrivateChat", e);
                    if (listener != null) {
                        listener.onFailure(e);
                    }
                });
    }

    private void createNewChat(User user, OnPrivateChatReadyListener listener) {
        if (mAuth.getUid() == null) {
            return;
        }

        PrivateChat privateChat = new PrivateChat();

        ArrayList<String> members = new ArrayList<>();
        members.add(user.getId());
        members.add(mAuth.getUid());

        HashMap<String, Boolean> chatMembers = new HashMap<>();
        chatMembers.put(user.getId(), true);
        chatMembers.put(mAuth.getUid(), true);

        privateChat.setChatMembers(chatMembers);

        User myUser = Hawk.get("User");
        privateChat.setSender(myUser);
        privateChat.setReceiver(user);
        privateChat.setAccepted(false);
        privateChat.setMembers(members);
        privateChat.setLastMessage("New Message request");

        db.collection("privateChat")
                .add(privateChat)
                .addOnSuccessListener(documentReference -> {
                    privateChat.setPrivate_id(documentReference.getId());
                    fetchAllChat();
                    if (listener != null) {
                        listener.onReady(privateChat);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "createNewChat", e);
                    if (listener != null) {
                        listener.onFailure(e);
                    }
                });
    }



    MutableLiveData<List<PrivateChat>> allChat= new MutableLiveData<>();


    public MutableLiveData<List<PrivateChat>> getAllChat() {
        return allChat;
    }



    public  void fetchAllChat(){



        if (mAuth.getUid() == null){
            return;
        }
        db.collection("privateChat")
                .whereArrayContains("members",mAuth.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {



                        ArrayList<PrivateChat> users = new ArrayList<>();

                        for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots.getDocuments()){

                            PrivateChat user= documentSnapshot.toObject(PrivateChat.class);
                            if (user == null) {
                                continue;
                            }
                            user.setPrivate_id(documentSnapshot.getId());
                            if (user.getMembers() != null && mAuth.getUid() != null) {
                                String partnerId = user.getMembers().get(0).equals(mAuth.getUid())
                                        ? user.getMembers().get(1) : user.getMembers().get(0);
                                if (user.getSender() != null) {
                                    user.getSender().setId(mAuth.getUid());
                                }
                                if (user.getReceiver() != null) {
                                    user.getReceiver().setId(partnerId);
                                }
                            }
                            users.add(user);
                        }


                        allChat.postValue(users);


                        Log.d(TAG, "onSuccess: "+queryDocumentSnapshots.getDocuments().size());




                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.d(TAG, "onFailure: "+e.getMessage());
                        e.printStackTrace();
                    }
                });

    }




    private static final String TAG = "UserDataViewModel";
}
