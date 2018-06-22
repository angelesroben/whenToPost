import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkAdapter;
import net.dean.jraw.http.OkHttpNetworkAdapter;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;
import net.dean.jraw.models.SubredditSort;
import net.dean.jraw.models.TimePeriod;
import net.dean.jraw.oauth.Credentials;
import net.dean.jraw.oauth.OAuthHelper;
import net.dean.jraw.pagination.DefaultPaginator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class connect {

    public static void main(String[] args){
        String subredditName = "";
        String userPassword = "";
        String userName = "";
        String appID = "";

        //Authentication
        Credentials credentials = Credentials.script(userName, userPassword, "4o0vrAzhAeegxg", "G5VAb9hO1aYdMrl16tN_VZ3sNRI");
        UserAgent userAgent = new UserAgent("bot", appID, "v1.0", userName);
        NetworkAdapter adapter = new OkHttpNetworkAdapter(userAgent);
        RedditClient reddit = OAuthHelper.automatic(adapter, credentials);

        //Top 300 Posts for the last year (300 = 25 (front page max) * 12 (months))
        DefaultPaginator<Submission> paginator = reddit.subreddit(subredditName).posts()
                .sorting(SubredditSort.TOP)
                .timePeriod(TimePeriod.YEAR)
                .limit(25)
                .build();
        List<Listing<Submission>> pageList = paginator.accumulate(12);

        int[] hourArr = new int[24];
        HashMap<String, Integer> timeMap = new HashMap<>();
        timeMap.put("Morning", 0);
        timeMap.put("Afternoon", 0);
        timeMap.put("Night", 0);

        //Iterate through all the submissions in each page and increment time of day hashmap and the specific hour array
        for(int i = 0; i < pageList.size(); i++){
            Listing<Submission> pages = pageList.get(i);
            for(int j = 0; j < pages.size(); j++){
                Submission s = pages.get(j);
                Date thisDate = s.getCreated();
                SimpleDateFormat localDateFormat = new SimpleDateFormat("HH");
                String sTime = localDateFormat.format(thisDate);
                int iTime = Integer.parseInt(sTime);
                hourArr[iTime]++;
                if(iTime < 12) timeMap.put("Morning", timeMap.get("Morning") + 1);
                else if(iTime >= 12 && iTime < 17) timeMap.put("Afternoon", timeMap.get("Afternoon") + 1);
                else timeMap.put("Night", timeMap.get("Night") + 1);
            }
        }


        //Printing out
        System.out.println("Top Post Statistics for r/" + subredditName);
        for(HashMap.Entry<String, Integer> entry : timeMap.entrySet()){
            System.out.println(entry.getKey() + " time had " + entry.getValue() + " of the highest posts in the last year.");
        }

        System.out.println("HOUR BREAKDOWN:");
        System.out.println("Morning: ");
        for(int i = 0; i < hourArr.length; i++){
            if(i == 12) System.out.println("Afternoon: ");
            if(i == 17) System.out.println("Night: ");
            System.out.println("Hour: " + i + ". #: " + hourArr[i]);


        }

    }
}
