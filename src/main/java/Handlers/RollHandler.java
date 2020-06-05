package Handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class RollHandler {
    java.util.List<String> output;
    Random rand;
    public RollHandler(){
        output = new ArrayList<>();
        rand = new Random();
    }

    public void processCommand(String command, MessageReceivedEvent event){
        String[] argArray = command.split(" ");
        List<String> args = new ArrayList();
        for(String arg:argArray){
            args.add(arg);
        }
        for(String out:args){
            System.out.println(out);
        }
        args.remove(0);
        if(args.contains("-rr")){
            repeatRoll(args, event);
        }else{
            roll(args, event);
        }
    }


    private void repeatRoll(List<String> args, MessageReceivedEvent event){
        int index = args.indexOf("-rr");
        int repeats = Integer.parseInt(args.get(index+1));
        args.remove(index+1);
        args.remove(index);
        System.out.println(args.toString());
        for(int i=0;i<repeats;i++){
            roll(args, event);
        }
    }

    private void roll(List<String> args, MessageReceivedEvent event){
        boolean min;
        int minTotal;
        boolean max;
        int maxTotal;
        boolean keep;
        int keepCount;
        if(args.contains("-min")){
            min=true;
            int index = args.indexOf("-min");
            minTotal = Integer.valueOf(args.get(index+1));
            args.remove(index);
            args.remove(index);
        }if(args.contains("-max")){
            max=true;
            int index = args.indexOf("-max");
            maxTotal = Integer.parseInt(args.get(index+1));
            args.remove(index);
            args.remove(index);
        }if(args.contains("-k")){
            keep=true;
            int index = args.indexOf("-keep");
            keepCount = Integer.parseInt(args.get(index+1));
            args.remove(index);
            args.remove(index);
        }else if(args.contains("-keep")){
            keep=true;
            int index = args.indexOf("-keep");
            keepCount = Integer.parseInt(args.get(index+1));
            args.remove(index);
            args.remove(index);
        }
        event.getChannel().sendMessage(args.toString()).queue();
        if(args.size()>1){
            event.getChannel().sendMessage("Invalid Arguements").queue();
            return;
        }
        String rollStr = args.get(0);
        /*
        remove +
        remove -
        remove *
        remove /
         */
        String[] rollArgs1 = rollStr.split("\\+");
        StringBuilder build1 = new StringBuilder();
        for(String arg1:rollArgs1){
            String[] rollArgs2 = arg1.split("-");
            StringBuilder build2 = new StringBuilder();
            for(String arg2:rollArgs2){
                String[] rollArgs3 = arg1.split("\\*");
                StringBuilder build3 = new StringBuilder();
                for(String arg3:rollArgs3){
                    String[] rollArgs4 = arg1.split("/");
                    StringBuilder build4 = new StringBuilder();
                    for(String arg4:rollArgs4){
                        int res=0;
                        if(arg4.contains("d")){
                            int die = Integer.parseInt(arg4.split("d")[1]);
                            int itt = Integer.parseInt(arg4.split("d")[0]);
                            for(int i=0;i<itt;i++){
                                res += rand.nextInt(die)+1;
                            }
                        }else{
                            res+=Integer.parseInt(arg4);
                        }
                        arg4 = String.valueOf(res);
                        build4.append(arg4);
                    }
                    arg3 = build4.toString();
                    build3.append(arg3);
                }
                arg2 = build3.toString();
                build2.append(arg2);
            }
            arg1 = build2.toString();
            build1.append(arg1);
        }
        String calc = build1.toString();
    }
}