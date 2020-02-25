package Handlers;

import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ResponseHandler extends ListenerAdapter {

    List<CheckState> backlog;


    public ResponseHandler(){
        backlog = new ArrayList<>();
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if(event.getReactionEmote().getName().equals("\u2705")){
            for (CheckState checker : backlog){
                if(event.getUser().getId().contains(checker.getSender()) && event.getMessageId().contains(checker.getmID())){
                    int index = backlog.indexOf(checker);
                    backlog.get(index).respondYes();
                }
            }
        }else if(event.getReactionEmote().getName().equals("\u274e")){
            for (CheckState checker : backlog){
                if(event.getUser().getId().contains(checker.getSender()) && event.getMessageId().contains(checker.getmID())){
                    int index = backlog.indexOf(checker);
                    backlog.get(index).respondNo();
                }
            }
        }
    }

    public String waitForReaction(String importID, String importSender){
        CheckState checker = new CheckState(importID, importSender);
        LocalTime endTime = LocalTime.now().plusSeconds(30);
        backlog.add(checker);
        while(!checker.isSelectedNo() && !checker.isSelectedYes() && endTime.isAfter(LocalTime.now())){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if(checker.isSelectedYes()){
            backlog.remove(checker);
            return "yes";
        }else if(checker.isSelectedNo()){
            backlog.remove(checker);
            return "no";
        }else {
            backlog.remove(checker);
            return null;
        }
    }

    private class CheckState{
        private String mID;
        private String sender;
        private boolean selectedYes = false;
        private boolean selectedNo = false;

        public CheckState(String ID, String iSender){
            mID = ID;
            sender = iSender;
        }

        public void respondYes(){
            selectedYes = true;
        }

        public void respondNo(){
            selectedNo = true;
        }

         public String getmID() {
             return mID;
         }

         public String getSender() {
             return sender;
         }

         public boolean isSelectedNo() {
             return selectedNo;
         }

         public boolean isSelectedYes() {
             return selectedYes;
         }
     }

}
