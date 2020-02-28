package GoogleHandlers;

import Handlers.CraftHandler;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

public class SheetTester {
    public static final String ID = "1U4ilMqpE7rINHN3P5EEUmXr_f0hZY-geWLuPReTRWNg";

    private static List<String[]> itemArray;
    public static void writeToSheet(CraftHandler importCrafts) throws GeneralSecurityException, IOException {
        itemArray = importCrafts.getCraftBacklog();
        Sheets sheets = SheetsBuilder.getSheets();

        List<List<Object>> outputToBody = new ArrayList<>();
        for(String[] entryList:itemArray){
            List<Object> builder = new ArrayList<>();
            for(String entry:entryList){
                builder.add(entry);
            }
            outputToBody.add(builder);
        }

        ValueRange body = new ValueRange().setValues(outputToBody);

        AppendValuesResponse result = sheets.spreadsheets().values()
                .append(ID, "A1", body)
                .setValueInputOption("RAW")
                .setInsertDataOption("INSERT_ROWS")
                .setIncludeValuesInResponse(true)
                .execute();

        //sheets.spreadsheets().values().
    }

}
