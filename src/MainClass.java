public class MainClass {

	public static void main(String[] args){
		Bot bot = new Bot("MJBs_Trivia_Bot");
		//Bot bot=new Bot("MJBot");

		try{
			bot.setVerbose(true);
			bot.connect("irc.synirc.net");
			// bot.sendMessage("nickserv","IDENTIFY <your password>" );
			bot.joinChannel("#trivia");
			//bot.joinChannel("#uk");
		}
	catch (Exception e){e.printStackTrace();}
	}
	
}