package Main;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class Retriever {

    JDA jda;

    public Retriever(JDA jdaImport){
        jda = jdaImport;
    }

    public List<List<Message>> getHistory(){

        //Retrieve full server Message history by channel
        List<List<Message>> messageHistory = new ArrayList<>();
        for(TextChannel channel:jda.getGuildById("262940513190019073").getTextChannels()){
            System.out.println("Scanning " + channel.getName());
            List<Message> messages = new ArrayList<>();
            boolean complete = false;
            String index = channel.getLatestMessageId();
            while(complete != true){
                List<Message> buffer = channel.getHistoryBefore(index, 100).complete().getRetrievedHistory();
                if(buffer.size()<100){
                    complete = true;
                }
                index = buffer.get(buffer.size()-1).getId();
                messages.addAll(buffer);
            }
            messageHistory.add(messages);
            System.out.println("Finished Scanning. Found " + messages.size() + " Messages");
        }

        //Retrieve member based message history
        List<List<Message>> messagesByUser = new ArrayList<>();
        List<Member> members = jda.getGuildById("262940513190019073").getMembers();
        for(Member user:members){
            List<Message> userHistory = new ArrayList<>();
            for(List<Message> channelHistory:messageHistory){
                for(Message message:channelHistory) {
                    if (user.getUser().equals(message.getAuthor())){
                        userHistory.add(message);
                    }
                }
            }
            if(userHistory.size()!=0) {
                messagesByUser.add(userHistory);
                System.out.println("Found " + userHistory.size() + " messages from " + userHistory.get(0).getAuthor().getName());
            }
        }
        return messagesByUser;
    }

    public List<List<Object>> convertToArchive(List<List<Message>> messageList){
        LocalDate today = LocalDate.now();
        List<List<Object>> export = new ArrayList<>();
        for(List<Message> userHistory:messageList){
            User user = userHistory.get(0).getAuthor();
            String userID = user.getId();
            String userName = user.getName();
            boolean isActive=false;
            boolean isSuperActive=false;
            int messageCount = userHistory.size();
            int lastMonth = 0;
            int last3Months = 0;
            int last6Months = 0;
            for(Message message:userHistory){
                if(message.getTimeCreated().toLocalDate().isAfter(today.minus(6, ChronoUnit.MONTHS))) {
                    last6Months++;
                    if (message.getTimeCreated().toLocalDate().isAfter(today.minus(3, ChronoUnit.MONTHS))) {
                        last3Months++;
                        if(message.getTimeCreated().toLocalDate().isAfter(today.minus(1, ChronoUnit.MONTHS))) {
                            lastMonth++;
                        }
                    }
                }
            }

            isActive = jda.getGuildById("262940513190019073").getMember(user).getRoles().contains(jda.getGuildById("262940513190019073").getRoleById("492712890701316106"));
            isSuperActive = jda.getGuildById("262940513190019073").getMember(user).getRoles().contains(jda.getGuildById("262940513190019073").getRoleById("498911307123130370"));

            List<Object> userRecords = new ArrayList<>();
            userRecords.add(userID);
            userRecords.add(userName);
            userRecords.add(String.valueOf(isActive));
            userRecords.add(String.valueOf(isSuperActive));
            userRecords.add(String.valueOf(messageCount));
            userRecords.add(String.valueOf(lastMonth));
            userRecords.add(String.valueOf(last3Months));
            userRecords.add(String.valueOf(last6Months));
            export.add(userRecords);
        }
        return export;
    }
}
