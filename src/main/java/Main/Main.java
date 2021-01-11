package Main;

import Handlers.MessageHandler;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public class Main{

    //static final String tokenSource = "/usr/token";
    static final String tokenSource = "F:\\token";
    public static JDA jda;

    public static void main(String[] args) throws GeneralSecurityException, IOException, InterruptedException {
        BufferedReader tokenReader = new BufferedReader(new FileReader(tokenSource));
        //SheetInformationBuffer.initialize();
        String token = tokenReader.readLine();
        JDABuilder builder = JDABuilder.createDefault(token);
        builder.enableIntents(GatewayIntent.GUILD_MEMBERS);
        builder.setMemberCachePolicy(MemberCachePolicy.ALL);
        builder.setChunkingFilter(ChunkingFilter.ALL);
        System.out.println(token);
        builder.setToken(token);
        jda = builder.build();
        System.out.println("init");
        jda.awaitReady();
        System.out.println("Ready");
        jda.addEventListener(new MessageHandler());
    }
}
