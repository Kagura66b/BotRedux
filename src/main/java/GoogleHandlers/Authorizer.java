package GoogleHandlers;

import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

public class Authorizer {
    public static HttpRequestInitializer authorize() throws IOException {
        List<String> scopes = Collections.singletonList(SheetsScopes.SPREADSHEETS);
        InputStream in = new FileInputStream("C:\\Users\\kingo\\IdeaProjects\\credentials.json");
        //InputStream in = new FileInputStream("/home/ubuntu/credentials.json");
        GoogleCredentials serviceCredentials = ServiceAccountCredentials.fromStream(in).createScoped(scopes);
        return new HttpCredentialsAdapter(serviceCredentials);
    }
}
