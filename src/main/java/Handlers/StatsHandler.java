package Handlers;

import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.react.PrivateMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import static java.lang.Thread.sleep;

public class StatsHandler extends ListenerAdapter {
    PrivateChannel channel;
    int dataSet1[] = new int[4];
    int dataSet2[] = new int[4];

    public StatsHandler(PrivateMessageReceivedEvent event){
        channel = event.getChannel();
        event.getChannel().sendMessage("Send dataset 1: \n\"Dice,Limit,PreEdge,PostEdge\" \n (Number,Number,True/False,True/False)").complete();
        System.out.println(dataSet1[0]);
    }

    private double[] calculate(){
        double[] end = {0,0,0,0};
        return end;
    }

    @Override
    public void onPrivateMessageReceived(@NotNull PrivateMessageReceivedEvent event) {
        super.onPrivateMessageReceived(event);
        if(event.getChannel().equals(channel)&&!event.getAuthor().isBot()){
            if(dataSet1[0] == 0){
                String[] args = event.getMessage().getContentRaw().replace(" ", "").replace("(", "").replace(")", "").replace("\"", "").split(",");
                if(args.length!=4){
                    event.getChannel().sendMessage("Invalid Input").queue();
                    event.getJDA().removeEventListener(this);
                }else{
                    try {
                        dataSet1[0] = Integer.parseInt(args[0]);
                        dataSet1[1] = Integer.parseInt(args[1]);
                    } catch(NumberFormatException e){
                        event.getChannel().sendMessage("Invalid Input").queue();
                        event.getJDA().removeEventListener(this);
                        return;
                    }
                    if(args[2].equals("True") || args[2].equals("true")){
                        dataSet1[2] = 1;
                    }
                    if(args[3].equals("True") || args[3].equals("true")){
                        dataSet1[3] = 1;
                    }
                    boolean one = dataSet1[2]==1;
                    boolean two = dataSet1[3]==1;
                    event.getChannel().sendMessage("Dice: " + args[0] + "\nLimit: " + args[1] + "\nExploding: " + one + "\nPostEdge: " + two + "\n\nSend dataset 2: \n\"Dice,Limit,PreEdge,PostEdge\" \n (Number,Number,True/False,True/False)").queue();
                }
            }else if(dataSet2[0] == 0){
                String[] args = event.getMessage().getContentRaw().replace(" ", "").replace("(", "").replace(")", "").replace("\"", "").split(",");
                if(args.length!=4){
                    event.getChannel().sendMessage("Invalid Input").queue();
                    event.getJDA().removeEventListener(this);
                }else{
                    try {
                        dataSet2[0] = Integer.parseInt(args[0]);
                        dataSet2[1] = Integer.parseInt(args[1]);
                    } catch(NumberFormatException e){
                        event.getChannel().sendMessage("Invalid Input").queue();
                        event.getJDA().removeEventListener(this);
                        return;
                    }
                    if(args[2].equals("True") || args[2].equals("true")){
                        dataSet2[2] = 1;
                    }
                    if(args[3].equals("True") || args[3].equals("true")){
                        dataSet2[3] = 1;
                    }
                    boolean one = dataSet2[2]==1;
                    boolean two = dataSet2[3]==1;
                    event.getChannel().sendMessage("Dice: " + args[0] + "\nLimit: " + args[1] + "\nExploding: " + one + "\nPostEdge: " + two + "\n\nConfirm(Y/N)").queue();
                }
            }else{
                if(event.getMessage().getContentRaw().equals("y") || event.getMessage().getContentRaw().equals("Y")){
                    calculate();
                    event.getChannel().sendMessage(dataSet1[0] + ", " + dataSet1[1] + ", " + dataSet1[2] + ", " + dataSet1[3] + "\n" +
                            dataSet2[0] + ", " + dataSet2[1] + ", " + dataSet2[2] + ", " + dataSet2[3]).queue();
                    event.getJDA().removeEventListener(this);
                }else{
                    event.getChannel().sendMessage("Terminated").queue();
                    event.getJDA().removeEventListener(this);
                }
            }
        }
    }
}
