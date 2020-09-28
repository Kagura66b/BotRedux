package Main;

import GoogleHandlers.SheetInformationBuffer;
import GoogleHandlers.SheetsBuilder;
import Handlers.Monitor;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Message;

import javax.security.auth.login.LoginException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public class Main{

    static final String dataTableSource = "src/main/resources/MasterList.csv";
    static final String tokenSource = "/usr/token";
    //static final String tokenSource = "F:\\testToken";
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
        /*
        Retriever retriever = new Retriever(jda);
        List<List<Message>> messageHistoryByUser = retriever.getHistory();
        List<List<Object>> export = retriever.convertToArchive(messageHistoryByUser);
        SheetInformationBuffer.writeToSheet(SheetInformationBuffer.messageHistory, "PrimaryLog", export);
        jda.shutdownNow();
        */

        jda.addEventListener(new Monitor());
    }
}
