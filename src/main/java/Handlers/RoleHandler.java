package Handlers;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;

import java.util.HashMap;

public class RoleHandler {
    public static HashMap<String, String> roleMap = new HashMap<>();

    public static void addRole(MessageReactionAddEvent event){
        String id;
        if(event.getReactionEmote().isEmote()){
            id = event.getReactionEmote().getEmote().getId();
        }else{
            id = event.getReactionEmote().getEmoji();
        }
        Role role = event.getGuild().getRoleById(roleMap.get(id));
        if(!event.getMember().getRoles().contains(role)){
            event.getGuild().addRoleToMember(event.getMember(), role).queue();
            System.out.println(role.getName() + " added to " + event.getMember().getEffectiveName());
        }
    }

    public static void removeRole(MessageReactionRemoveEvent event){
        String id;
        if(event.getReactionEmote().isEmote()){
            id = event.getReactionEmote().getEmote().getId();
        }else{
            id = event.getReactionEmote().getEmoji();
        }
        Role role = event.getGuild().getRoleById(roleMap.get(id));
        if(event.getMember().getRoles().contains(role)){
            event.getGuild().removeRoleFromMember(event.getMember(), role).queue();
            System.out.println(role.getName() + " removed from " + event.getMember().getEffectiveName());
        }
    }

    public static void initHash(){
        roleMap.put("593109019753971733","596029175937237012");//noDM
        roleMap.put("593109475854909460","596029125005672464");//askDM
        roleMap.put("593107540821409802","596029071482421279");//yesDM
        roleMap.put("\uD83D\uDC99", "619546795923472393");//he/him
        roleMap.put("\uD83D\uDC9C","619546858607345674");//she/her
        roleMap.put("\uD83D\uDC9A","619546901905408024");//they/them
        roleMap.put("593181512057356297","596029622014181376");//NB
        roleMap.put("593181429567979560","596029560642994383");//trans
        roleMap.put("593181343848857609","596029521174593546");//cis
        roleMap.put("593181138424561786","596029736157708299");//gay
        roleMap.put("593181185748893798","596029784497324042");//bi/pan
        roleMap.put("593181086830428199","596029677949288453");//straight
        roleMap.put("593113429800386582","596029964110004234");//dom
        roleMap.put("525447354980237312","596029866449698846");//sub
        roleMap.put("593112888785502208","596030047375196190");//switch
        roleMap.put("\uD83D\uDC18","596030128543236106");//africa
        roleMap.put("\uD83D\uDC3C","596030540004458548");//asia
        roleMap.put("\uD83D\uDC3A","596030604894666753");//europe
        roleMap.put("\uD83E\uDD85","596030660569858060");//NA
        roleMap.put("\uD83D\uDC28","596030707885932575");//oceana
        roleMap.put("593909908160643085","596030770275942446");//SA
        roleMap.put("593183258649559072","596030892435046427");//fetlife
        roleMap.put("593238337083670529","596030967890575380");//owner
        roleMap.put("434096579742269441","596031018822270976");//lover
        roleMap.put("\uD83C\uDDE6","596031086513881108");//furry
        roleMap.put("\uD83C\uDDE7","596031138791817216");//gimp
        roleMap.put("\uD83C\uDDE8","596031184027516928");//drone
        roleMap.put("\uD83C\uDDE9","596031228637872134");//kig
        roleMap.put("\uD83C\uDDEA","596031722965958667");//RP
        roleMap.put("\uD83C\uDDEB","596031768839192690");//doll
        roleMap.put("\uD83C\uDDEC","596031811340075008");//maid
        roleMap.put("\uD83C\uDDED","596031866897956872");//inflatable
        roleMap.put("\uD83C\uDDEE","596031922380210176");//fashion
    }
}
