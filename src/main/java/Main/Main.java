package Main;

import Handlers.ResponseHandler;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import javax.security.auth.login.LoginException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main{

    static final String dataTableSource = "src/main/resources/MasterList.csv";
    static final String tokenSource = "/home/ubuntu/token";
    //static final String tokenSource = "F:\\testToken";
    public static ResponseHandler responseHandler;
    public static JDA jda;

    public static void main(String[] args) throws LoginException, IOException, InterruptedException {
        JDABuilder builder = new JDABuilder(AccountType.BOT);
        //dataTable = new ItemDataTable("/home/ubuntu/Documents/export2.csv");
        BufferedReader tokenReader = new BufferedReader(new FileReader(tokenSource));
        String token = tokenReader.readLine();
        System.out.println(token);
        ItemDataTable dataTable = new ItemDataTable(dataTableSource);
        builder.setToken(token);
        jda = builder.build();
        jda.awaitReady();
        jda.addEventListener(responseHandler = new ResponseHandler());
        jda.addEventListener(new MessageHandler(dataTable, responseHandler, jda));
    }
}
