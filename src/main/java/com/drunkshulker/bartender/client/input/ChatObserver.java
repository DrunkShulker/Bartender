package com.drunkshulker.bartender.client.input;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.drunkshulker.bartender.Bartender;
import com.drunkshulker.bartender.client.gui.clickgui.ClickGuiSetting;
import com.drunkshulker.bartender.client.social.PlayerGroup;
import com.drunkshulker.bartender.util.Config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import scala.actors.threadpool.Arrays;

public class ChatObserver {
	
	public static boolean mooCheck = false;
	public static boolean help = false;
	public static boolean chatFix = false;
	public static boolean afkResponse = false;
	public static boolean allowDrinks = false;
	public static boolean pride = false;
	public static boolean partyTPA = false;
	public static boolean partyJoin = true;
	public static String lastPartyInvite = "";

	public static String commandPrefix = "@";
	public static String botMsgPrefix = "$";
	private long lastDrinks = 0;

	final String pornUrl = "https:/"+"/www.pornhub.com/random";
	private long lastPornhubSearch = 0;
	public static boolean pornhub = false;
	Timer timer;
	private String lastSafeChatMessage="";
	
	final String[] allDrinks = {
			"beer",
			"martini",
			"vodka",
			"red wine",
			"milk",
			"monster energy",
			"tea",
			"apple juice",
			"water",
			"coke",
			"potion of poison",
			"tequila",
			"red bull"
	};
	
	@SubscribeEvent
	public void onServerChatEvent(ClientChatReceivedEvent event){
		String message = event.getMessage().getUnformattedText();
		
		
		if(message.charAt(0) == '<') {
			String sender = getMessageSender(message);
			String text = getMessageText(sender, message);

			
			if(allowDrinks) {
				if(orderDrinks(message, sender, text)) return;
			}
			
			if(help) {
				if(execHelp(message, sender, text)) return;
			}
			
			if(partyJoin) {
				listenPartyJoin(message, sender, text);
			}
			
			if(pornhub) {
				pornSearch(message, sender);
			}
			return;
		}
		
		
		if(partyTPA) { 
			partyTpaccept(message);
			if(PlayerGroup.groupAcceptTpaHere) PlayerGroup.tpaccept(message);
		}
		else PlayerGroup.tpaccept(message);
		
		
		if(afkResponse) afk(message);
	}

	private void listenPartyJoin(String message, String sender, String text) {
		if(message.contains("Party time! Type /tpa")){
			if(message.contains(sender)){
				lastPartyInvite = sender;
				Bartender.msg("type /join for " +sender+"'s party!");
			}
		}
	}

	private boolean execHelp(String message, String sender, String text) {
		if(message.indexOf(commandPrefix+"help")==(sender.length()+3)&&text.length()<=5) {
			safeSendPublicChatMessage(botMsgPrefix+" "+Bartender.NAME+" "+Bartender.VERSION+" by DrunkShulker");
			return true;
		}
		return false;
	}

	private String getMessageText(String sender, String message) {
		return message.substring(sender.length()+3);
	}

	private String getMessageSender(String message) {
		
		String sender = "";
		for (int i = 1; i < message.length(); i++){
		    char c = message.charAt(i);        
		    if(c=='>') break;
		    else sender+=c;
		}

		if(sender.length()>2&& !Minecraft.getMinecraft().isSingleplayer()) sender = sender.substring(0, sender.length()-2);
		
		
		return sender;
	}
	
	public void safeSendPublicChatMessage(String messageText) {
		if(messageText.equals(lastSafeChatMessage)) {
			Minecraft.getMinecraft().player.sendMessage(new TextComponentString("<"+Bartender.NAME+"> Someone is spamming you!"));
			return;
		}
		lastSafeChatMessage = messageText;
		Minecraft.getMinecraft().player.sendChatMessage(messageText);
	}
	
	public void pornSearch(String message, String sender) {
		if(lastPornhubSearch>0L&&System.currentTimeMillis()-lastPornhubSearch<2000) return; 
		if(message.contains("s PornHub search: ")) return; 

		if(message.indexOf(commandPrefix+"sex")==(sender.length()+3+2)) {

			final String senderf = sender;	
			
			
			
			if(lastPornhubSearch>0L&&System.currentTimeMillis()-lastPornhubSearch<15000) {
				String messageText = botMsgPrefix+" You have to wait 15 seconds before executing "+commandPrefix+"sex again";
				safeSendPublicChatMessage(messageText);
				return;
			}

			lastPornhubSearch = System.currentTimeMillis();
	    	timer = new Timer();
	    	timer.schedule(new TimerTask()
	    	{
	    	    public void run()
	    	    {  	 	    	  	    	
	    	    	try {
	    	    	URL obj = new URL(pornUrl);
	    			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
	    			con.setRequestMethod("GET");
	    			con.setRequestProperty("User-Agent", "Mozilla/5.0");
	    			int responseCode = con.getResponseCode();
	    			System.out.println("GET Response Code :: " + responseCode);
	    			if (responseCode == HttpURLConnection.HTTP_OK) { 
	    				BufferedReader in = new BufferedReader(new InputStreamReader(
	    						con.getInputStream()));
	    				String inputLine;
	    				StringBuffer response = new StringBuffer();
	
	    				while ((inputLine = in.readLine()) != null) {
	    					response.append(inputLine);
	    				}
	    				in.close();
	    				
	    				
	    				String r = botMsgPrefix+" "+senderf+"'s PornHub search: "+ response.substring(response.indexOf("<title>") + 7, response.indexOf("</title>"));
						r = r.replaceAll("[^A-Za-z0-9()$'!|#+*%&^£?:.,_> \\[\\]]", "");
	    				safeSendPublicChatMessage(r.substring(0, r.length()-13));
	    			} else {
	    				Minecraft.getMinecraft().player.sendMessage(new TextComponentString("<"+Bartender.NAME+"> "+commandPrefix+"sex failed due to pornhub api."));
	    			}}catch (Exception e) {Minecraft.getMinecraft().player.sendMessage(new TextComponentString("<"+Bartender.NAME+"> "+commandPrefix+"sex failed with exception!"));}
	    	    	timer.cancel();
	    	    	timer=null;
	    	    }
	    	}, 40, 40); 
		}
	}
	
	private void partyTpaccept(String message) {
		
		String toYou = "has requested to teleport to you.";
		if(message.contains(toYou)) {
			Minecraft.getMinecraft().player.sendChatMessage("/tpaccept");		
		}
	}

	private boolean orderDrinks(String message, String sender, String msgOnly) {
		if(lastDrinks >0L&&System.currentTimeMillis()-lastDrinks<2000) return false; 
		if(message.length()>60) return false; 
		
		
		if(message.indexOf(commandPrefix+"order")==(sender.length()+3+2)) {
			String toPlayer = null;
			
			if(msgOnly.length()>8&&msgOnly.length()<=25) {
				String subs = msgOnly.substring(7);
				if(subs.length()>2) {
					toPlayer = subs;
				}
			}
			
			Random coin = new Random();
			int toss = coin.nextInt(allDrinks.length);
			int amount = coin.nextInt(6);
			if(amount<1) amount=1;
			lastDrinks = System.currentTimeMillis();

			if(toPlayer!=null){
				String msg = botMsgPrefix+" "+sender+" just ordered "+amount+ " "+allDrinks[toss].toLowerCase()+" to their friend "+ toPlayer +"!";
				msg = msg.replaceAll("[^A-Za-z0-9()$'!|#+*%&^£?:.,_> \\[\\]]", "");
				msg = msg.replace(" r ", " ");
				safeSendPublicChatMessage(msg);
			}else {
				String msg = botMsgPrefix+" "+sender+" just ordered "+amount+ " "+allDrinks[toss].toLowerCase()+"!";
				msg = msg.replaceAll("[^A-Za-z0-9()$'!|#+*%&^£?:.,_> \\[\\]]", "");
				safeSendPublicChatMessage(msg);
			}
			
			return true;
		}
		return false;
	}

	private void afk(String message) {
		String m = "Im currently afk";
		if(!message.contains(" whispers:") ||message.contains(m)) return;
		Minecraft mc = Minecraft.getMinecraft();
		if(mc.isGamePaused()) return;
		
		for (String member : PlayerGroup.members) {
			String predicate = member+" whispers:";
			if(message.length()<predicate.length()) return;
			if(message.startsWith(predicate)) return;
		}
		
		mc.player.sendChatMessage("/r "+m);
	}

	public boolean messageIsCommand(String msg) {

		if(pride&&msg.charAt(0)=='%'&&msg.charAt(1)=='%') return false;

		final char[] cs = {
				'/', ',', '.', '-', ';', '?', '*', '^', '&', '%', '#', '$', '!', commandPrefix.charAt(0), '>', '|', '+'
		};
		for (char c : cs) {
			if(c==msg.charAt(0)) return true;
		}
		return false;
	}
	
	@SubscribeEvent
	public void onClientChatEvent(ClientChatEvent event){
		
		if(mooCheck) {
			if(event.getMessage().charAt(0)=='!') {
				if(!PlayerGroup.isPlayerOnline("moooomoooo")) {
					event.setCanceled(true);
					Bartender.msg("moooomoooo is offline. Command canceled.");
					return;
				}
			}
		}

		
		if(partyTPA&&event.getMessage().equals("/back")){
			Bartender.msg("Party TPA turned off because you typed /back");
			ClickGuiSetting.handleClick(ClickGuiSetting.fromString("chat->party tpa"),false);
		}

		
		if(pride&&!messageIsCommand(event.getMessage())){
			String[] pridePattern = new String[]{"%%","$$","<<",">","--"};
			String[] msg = event.getMessage().split("");
			String newMsg = "";
			for (int i = 0, y = 0; i < msg.length; i++) {
				newMsg += pridePattern[y] + msg[i];

				if(y>=pridePattern.length-1) y = -1;
				y++;
			}
			event.setMessage(newMsg);
		}

		
		if(chatFix) {
			if(messageIsCommand(event.getMessage())) return;
			if(Config.CHAT_POST_FIX.equals("")) return;

			String fix = Config.CHAT_POST_FIX;
			if(event.getMessage().contains(fix)) return; 
			event.setMessage(event.getMessage()+" "+fix);
		}
	}

	public static void applyPreferences(ClickGuiSetting[] contents) {
		for (ClickGuiSetting setting : contents) {
			if(setting.title.equals("moo check")) {
				mooCheck = setting.value == 0;
			}
			if(setting.title.equals("help")) {
				help = setting.value == 0;
			}
			else if(setting.title.equals("chatfix")) {
				chatFix = setting.value == 0;
			}
			else if(setting.title.equals("AFK reply")) {
				afkResponse = setting.value == 0;
			}
			else if(setting.title.equals("drinks")) {
				allowDrinks = setting.value == 0;
			}
			else if(setting.title.equals("pride")) {
				pride = setting.value == 0;
			}
			else if(setting.title.equals("party tpa")) {
				partyTPA = setting.value == 0;
			}
			else if(setting.title.equals("pornhub")) {
				pornhub = setting.value == 0;
			}
			else if(setting.title.equals("bot prefix")) {
				botMsgPrefix = setting.values.get(setting.value).getAsString();
			}
			else if(setting.title.equals("command prefix")) {
				commandPrefix = setting.values.get(setting.value).getAsString();
			}
		}
	}

	public static void clickAction(String action) {
		EntityPlayerSP p = Minecraft.getMinecraft().player;
		if(p==null) return;
		switch (action) {
		case "drinks ad":
			advertiseDrinks();
			break;
		case "party ad":
			advertiseParty();
			break;
		case "pornhub ad":
			advertisePornhub();
			break;
		default:
			break;
		}
	}

	private static void advertisePornhub() {
		if(!pornhub) {
			Minecraft.getMinecraft().player.sendMessage(new TextComponentString("<"+Bartender.NAME+"> Pornhub is not enabled!"));
			return;
		}
		else {
			Minecraft.getMinecraft().player.sendChatMessage(botMsgPrefix+" Type "+commandPrefix+"sex to execute a random PornHub search!");
		}
	}

	private static void advertiseParty() {
		if(!partyTPA) {
			Minecraft.getMinecraft().player.sendMessage(new TextComponentString("<"+Bartender.NAME+"> Party tpa is not enabled!"));
			return;
		}
		else {
			Minecraft.getMinecraft().player.sendChatMessage(botMsgPrefix+" Party time! Type /tpa "+Minecraft.getMinecraft().player.getDisplayNameString());
		}
	}

	private static void advertiseDrinks() {
		if(!allowDrinks) {
			Minecraft.getMinecraft().player.sendMessage(new TextComponentString("<"+Bartender.NAME+"> Drinks command is not enabled!"));
			return;
		}
		else {
			Minecraft.getMinecraft().player.sendChatMessage(botMsgPrefix+" Need a drink? Type "+commandPrefix+"order or "+commandPrefix+"order (player)");
		}
		
	}
}
