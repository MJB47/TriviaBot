
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jibble.pircbot.PircBot;

public class Bot extends PircBot {
	private HashMap<String, Integer> scores = new HashMap<String, Integer>();
	
	private ArrayList<String> permitted = new ArrayList<String>();
	
	// Give Bot a name
	public Bot (String name) {
		setName(name);
	}
	
	public void onPrivateMessage(String sender, String login, String hostname, String message) {
		if (message.equalsIgnoreCase("!rejoin")) {
			joinChannel("#trivia");
			return;
		}
	}
	
	public void onNotice(String sourceNick, String sourceLogin, String sourceHostname, String target, String notice) {
		
		if (notice.startsWith("Question")) {
			WriteToFile("Questions.txt", notice, true, false);
			return;
		}
	}
	
	// Make Bot respond to commands
	public void onMessage(String channel, String sender, String login, String hostname, String message) {
		String name;

		if (message.equalsIgnoreCase("!hello")) {
			sendMessage(channel, "Hello "+sender); 
			return;
		}
		
		
		if (sender.equals("MJB")) {
			if (message.equalsIgnoreCase("!fuckoff")) {
				System.exit(0);
				return;
			}
		}
		
		if (hasPermission().contains(sender)) {
			
			if (message.startsWith("!addpermission")) {
				
				if (permitted.contains(message.substring(15, message.length()))) {
					sendMessage(channel, message.substring(15, message.length()) + " already has permissions!");
					return;
				}
				
				permitted.add(message.substring(15, message.length()));
				
				sendMessage(channel, message.substring(15, message.length()) + " now has permissions"); 
				return;
			}
			
			if (message.startsWith("!removepermission")) {
				permitted.remove(message.substring(18 , message.length()));
				sendMessage(channel, message.substring(18, message.length()) + " no longer has permissions"); 
				return;
			}
		
			if (message.equalsIgnoreCase("!shutdown")) {
				sendMessage(channel, "Cya l8rs peeps");
				partChannel(channel);
				return;
			}
		
			if (message.startsWith("!point")) {
				if (message.equalsIgnoreCase("!point")) {
					sendMessage(channel, "You didn't give a name, try \"!point name\"");
					return;
				}
				name = message.substring(7, message.length()).toLowerCase();
				name = name.replaceAll(" ","");
				if (name.equals("")) {
					sendMessage(channel, "You didn't give a name, try \"!point name\"");
					return;
				} else {
					sendMessage(channel, "giving a point to: " + name);
				}
			
				if (!scores.containsKey(name)) {
					scores.put(name, new Integer(1));
				} else {
					Integer score = ((Integer)scores.get(name)).intValue();
					scores.put(name, new Integer(score + 1));
				}
				
				// write score to text file to be reloaded.
				WriteToFile("Scores.txt", name, true, false);
			
				return;
			}
			
			if (message.startsWith("!remove")) {
				if (message.equalsIgnoreCase("!remove")) {
					sendMessage(channel, "You didn't give a name, try \"!remove name\"");
					return;
				}
				name = message.substring(8, message.length()).toLowerCase();
				name = name.replaceAll(" ","");
				
				if (!scores.containsKey(name)) {
					sendMessage(channel, name + " doesn't have any points!");
					return;
				}
				Integer score = ((Integer)scores.get(name)).intValue();
				
				if (name.equals("")) {
					sendMessage(channel, "You didn't give a name, try \"!remove name\"");
					return;
				} else if (score.intValue() == 0) {
					sendMessage(channel, name + " doesn't have any points!");
					return;
				} else {
					sendMessage(channel, "removing a point from: " + name);
					scores.put(name, new Integer(score - 1));
					
					// write score removal to file, denoting with "NULL"
					WriteToFile("Scores.txt", "NULL" + name, true, false);

					return;
				}
			}
			
			
			if (message.equalsIgnoreCase("!score")) {
                sendMessage(channel, "~~~~ Scoreboard ~~~~");
                Map<String, Integer> sortedScores = new HashMap<String, Integer>();
                sortedScores = sortByValue(scores);
                Set<Entry<String, Integer>> scoresSet = sortedScores.entrySet();

                if (scores.isEmpty()) {
                    sendMessage(channel, "No scores...");
                } else {
                    for (Entry<String, Integer> score : scoresSet) {
                    	
                    	if (score.getValue() != 0) {
                    		sendMessage(channel, score.getKey() + ": " + score.getValue());
                    	
                    	}
                    }

				
                }
                sendMessage(channel, "~~~~ Scoreboard ~~~~");
                return;
			}
                
            if (message.equalsIgnoreCase("!newgame")) {
            	sendMessage(channel, "starting a new game!");
            	scores.clear();
            	WriteToFile("Scores.txt", "", false, true);
            	WriteToFile("Questions.txt", "", false, true);
            	return;
            }
            
            if (message.equalsIgnoreCase("!loadscores")) {
            	//BufferedReader br = new BufferedReader(new FileReader("Scores.txt"));
            	boolean load = false;
            	
            	Reader br;
            	
            	if (!scores.isEmpty()) {
            		sendMessage(channel, "There are already scores in use!");
            		return;
            	}
            	
            	sendMessage(channel, "Attempting to load scores");
            	
                try {
                	br = new BufferedReader(new FileReader("Scores.txt"));
                	StreamTokenizer stok = new StreamTokenizer(br);
                    //String line = br.readLine();
                	
                	stok.nextToken();

                    while (stok.ttype != StreamTokenizer.TT_EOF) {
                    	name = stok.sval;
                    	//Integer score = ((Integer)scores.get(name)).intValue();
                    	
                    	if (!name.startsWith("NULL")) {
                    		if (!scores.containsKey(name)) {
                    			scores.put(name, new Integer(1));
                    		} else {
                    			Integer score = ((Integer)scores.get(name)).intValue();
                    			scores.put(name, new Integer(score + 1));
                    		}
                    	} else {
                    		name = name.substring(4, name.length());
                    		Integer score = ((Integer)scores.get(name)).intValue();
                    		scores.put(name, new Integer(score - 1));
                    	}
        				stok.nextToken();
        			}
                    br.close();
                    load = true;
                } catch (IOException e){ e.printStackTrace(); }
                
                if (load == true) {
                	sendMessage(channel, "Scores secessfully loaded :)");
                } else {
                	sendMessage(channel, "Failed to load scores :(");
                }
                load = false;
                
                return;
            }
            
            if (message.equalsIgnoreCase("!questions")) {
            	File file = new File ("Questions.txt"); 
            	dccSendFile(file, sender, 120000);
            	return;
            }
            
            if (message.equalsIgnoreCase("!help")) {
            	sendMessage(channel, "~~~~commands~~~~");
            	sendMessage(channel, "!point name - gives a point to the user");
            	sendMessage(channel, "!remove name - removes a point to the user");
            	sendMessage(channel, "!score - displays the current scores");
            	sendMessage(channel, "!newgame - resets all scores");
            	sendMessage(channel, "!shutdown - disconnects and shuts down bot");
            	sendMessage(channel, "!addpermission name - gives the user permissions to use this bot (until it is next restarted) - Case sensitive");
            	sendMessage(channel, "!removepermission name - removes permissions from a user - Case sensitive");
            	sendMessage(channel, "!rejoin - (only works in PM) rejoins #trivia");
            	sendMessage(channel, "!loadscores - loads scores from latest trivia");
            	sendMessage(channel, "!questions - send a .txt file containing the quiestions");
            	sendMessage(channel, "Made by MJB, pm if there are issues. :)");
            	sendMessage(channel, "~~~~commands~~~~");
            	
            	return;
            }

            
            // add new messages here
		}
	
	}
	
	public static Map<String, Integer> sortByValue(HashMap<String, Integer> map) {
        List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>(map.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {

            public int compare(Map.Entry<String, Integer> m1, Map.Entry<String, Integer> m2) {
                return (m2.getValue()).compareTo(m1.getValue());
            }
        });

        Map<String, Integer> result = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
	
	public void WriteToFile(String FileName, String name, boolean appendValue, boolean ClearScores) {
		
		try {
			FileWriter outfile = new FileWriter(FileName, appendValue);
			PrintWriter out = new PrintWriter(outfile);
			
			if (ClearScores == false) {
				out.println(name);
			} else {
				out.print("");
			}
			
			out.close();
			
		} catch (IOException e){ e.printStackTrace(); }
	}
	
	public ArrayList<String> hasPermission() {
		
		permitted.add("MJB");
		permitted.add("GreatSage");
		permitted.add("Hugendugen");
		permitted.add("zfs");
		
		return permitted;
	}
}