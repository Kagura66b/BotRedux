package Main;

import GoogleHandlers.SheetInformationBuffer;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.GeneralSecurityException;

public class Main{

    //static final String tokenSource = "/usr/token";
    static final String tokenSource = "F:\\token";
    public static JDA jda;

    public static void main(String[] args) throws GeneralSecurityException, IOException, InterruptedException {
        JDABuilder builder = new JDABuilder(AccountType.BOT);
        //dataTable = new ItemDataTable("/home/ubuntu/Documents/export2.csv");
        BufferedReader tokenReader = new BufferedReader(new FileReader(tokenSource));
        SheetInformationBuffer.initialize();
        String token = tokenReader.readLine();
        System.out.println(token);
        builder.setToken(token);
        jda = builder.build();
        jda.awaitReady();
    }
}
