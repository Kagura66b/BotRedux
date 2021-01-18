package Handlers;

import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class DMStatsHandler extends ListenerAdapter {
    PrivateChannel channel;
    int[] dataSet1 = new int[4];
    int[] dataSet2 = new int[4];
    Random rand = new Random();

    public DMStatsHandler(PrivateMessageReceivedEvent event){
        channel = event.getChannel();
        event.getChannel().sendMessage("Send dataset 1: \n\"Dice,Limit,PreEdge,PostEdge\" \n (Number,Number,True/False,True/False)").complete();
        System.out.println(dataSet1[0]);
    }

    /*
    args index
    0 = dice
    1 = limit
    2 = exploding (1 or 0)
    3 = postedge (1 or 0)
     */
    private int[] roller(int[] args){
        int glitch = 0;
        int totalDice = args[0];
        int hitCount = 0;
        int oneCount = 0;
        for(int i = 0;i<args[0];i++){
            int roll = rand.nextInt(6);
            if(roll >= 4) {
                hitCount += 1;
                if (args[2] == 1 && roll == 5) {
                    i--;
                }
            }
            if(roll == 0) {
                oneCount++;
            }
        }
        if(hitCount > args[1]) {
            hitCount = args[1];
        }
        int newDicePool;
        //edge zone. You might glitch after using edge
        if((hitCount < args[1]) && totalDice/2 > oneCount && (args[3] == 1)){
            newDicePool = args[0] - hitCount;
            totalDice += newDicePool;
            oneCount = 0;
            for(int i = 0;i<newDicePool;i++){
                int roll = rand.nextInt(6);
                if(roll >= 4) {
                    hitCount += 1;
                }
                if(roll == 0) {
                    oneCount++;
                }
            }
            if (hitCount > args[1]) {
                hitCount = args[1];
            }
        }
        //check for glitch
        if(totalDice/2 <= oneCount) {
            glitch = 1;
        }
        return new int[]{hitCount, glitch};
    }

    private double[] calculate() {
        int reps = 10000;

        if (dataSet2[1] == -1){
            double hitTotal = 0;
            double netHitTotal = 0;
            double successes = 0;
            double glitchTotal = 0;
            double critGlitchTotal = 0;
            for(int i = 0; i<reps; i++){
                int[] userRollData = roller(dataSet1);
                hitTotal += userRollData[0];
                double netHits = userRollData[0] - dataSet2[0];
                netHitTotal += netHits;
                if (netHits >= 0) {
                    successes += 1;
                }
                if (userRollData[1]==1) {
                    glitchTotal += 1;
                    if (userRollData[0] == 0) {
                        critGlitchTotal += 1;
                    }
                }
            }
            System.out.println(hitTotal);
            return new double[]{hitTotal/reps,netHitTotal/reps,successes*100/reps,glitchTotal*100/reps,critGlitchTotal*100/reps};
        }else{
            double hitTotal= 0;
            double enemyHitTotal = 0;
            double netHitTotal= 0;
            double successes= 0;
            double glitchTotal= 0;
            double critGlitchTotal = 0;
            double enemyGlitchTotal = 0;
            double enemyCritGlitchTotal = 0;
            for(int i = 0; i<reps; i++){
                int[] userRollData = roller(dataSet1);
                int[] enemyRollData = roller(dataSet2);
                hitTotal += userRollData[0];
                enemyHitTotal += enemyRollData[0];
                double netHits = userRollData[0] - enemyRollData[0];
                netHitTotal += netHits;
                if (netHits > 0) {
                    successes += 1;
                }
                if (userRollData[1]==1) {
                    glitchTotal += 1;
                    if (userRollData[0] == 0) {
                        critGlitchTotal += 1;
                    }
                }
                if (enemyRollData[1]==1) {
                    enemyGlitchTotal += 1;
                    if (userRollData[0] == 0) {
                        enemyCritGlitchTotal += 1;
                    }
                }
            }
            return new double[]{hitTotal/reps,enemyHitTotal/reps,netHitTotal/reps,successes*100/reps,glitchTotal*100/reps,critGlitchTotal*100/reps,enemyGlitchTotal*100/reps,enemyCritGlitchTotal*100/reps};
        }
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
                    double[] results = calculate();

                    if(results.length == 5){
                        event.getChannel().sendMessage("" +
                                "Average Expected Hits on Roll: " +  results[0] + "\n" +
                                "Average Expected Net Hits on Roll: " + results[1] + "\n" +
                                "Probability of Success: " +  results[2] + "%\n" +
                                "Probability of Glitch: " + results[3] + "%\n" +
                                "Probability of Critical Glitch: " + results[4] + "%"
                        ).queue();
                    }else{
                        event.getChannel().sendMessage("" +
                                "Average Expected Hits on Roll: " + results[0] + "\n" +
                                "Average Expected Enemy Hits on Roll: " + results[1] + "\n" +
                                "Average Expected Net Hits on Roll: " + results[2] + "\n" +
                                "Probability of Success: " + results[3] + "%\n" +
                                "Probability of Glitch: " + results[4] + "%\n" +
                                "Probability of Critical Glitch: " + results[5] + "%\n" +
                                "Probability of Enemy Glitch: " + results[6] + "%\n" +
                                "probability of Enemy Critical Glitch: " + results[7] + "%"
                        ).queue();
                    }
                }else{
                    event.getChannel().sendMessage("Terminated").queue();
                }
                event.getJDA().removeEventListener(this);
            }
        }
    }
}
