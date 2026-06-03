package np.com.socialize;

import java.util.ArrayList;
import java.util.HashMap;

import com.orhanobut.hawk.Hawk;

import np.com.socialize.category.User;

public class PrivateChat {


    String private_id;
    User sender;
    User receiver;
    Boolean isAccepted;
    String lastMessage;
    ArrayList<String> members = new ArrayList<>();

    HashMap<String, Boolean> chatMembers = new  HashMap<String, Boolean>();


    public ArrayList<String> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<String> members) {
        this.members = members;
    }

    public String getPrivate_id() {
        return private_id;
    }

    public void setPrivate_id(String private_id) {
        this.private_id = private_id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public Boolean getAccepted() {
        return isAccepted;
    }

    public void setAccepted(Boolean accepted) {
        isAccepted = accepted;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }


    public HashMap<String, Boolean> getChatMembers() {
        return chatMembers;
    }

    public void setChatMembers(HashMap<String, Boolean> chatMembers) {
        this.chatMembers = chatMembers;
    }

    public User getPartner(String currentUserId) {
        if (currentUserId == null) {
            return receiver != null ? receiver : sender;
        }
        if (sender != null && sender.getId() != null && currentUserId.equals(sender.getId())) {
            return receiver;
        }
        if (receiver != null && receiver.getId() != null && currentUserId.equals(receiver.getId())) {
            return sender;
        }
        User myUser = Hawk.get("User");
        if (myUser != null && sender != null && myUser.getName() != null
                && myUser.getName().equals(sender.getName())) {
            return receiver;
        }
        return receiver != null ? receiver : sender;
    }
}
