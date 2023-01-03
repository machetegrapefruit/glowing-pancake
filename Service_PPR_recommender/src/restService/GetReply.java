package restService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import entity.AuxAPI;
import entity.Message;
import entity.ReplyMarkup;
import functions.InvalidMessageException;
import functions.UserMessageHandler;
import keyboards.KeyboardMarkup;
import replies.JsonReply;
import replies.Reply;
import utils.FormatUtils;

/**
 * Questa classe contiene gli endpoint principali per la comunicazione client-server.
 * Riceve un messaggio testuale dall'utente e gli risponde.
 * Il client ha bisogno di chiedere al server come deve rispondere all'utente.
 * @author Altieri
 *
 */
@Path ("/getReply")
public class GetReply {
	
	private String userID;
	private String messageID;
	private String timeStamp;
	private String text;
	private String firstname;
	private String lastName;
	private String userName;
	private String botName;
	
	/**
	 * L'endpoint principale per la comunicazione client-server.
	 * Per ottenere il messaggio di risposta da inviare all'utente, il client deve mandare
	 * una richiesta HTTP GET a questo endpoint, 
	 * con i parametri specificati in seguito.
	 * Otterrà un oggetto JSON contenente il messaggio di risposta e la keyboard da fornire all'utente,
	 * per permettergli di continuare l'interazione tramite pulsanti.
	 * La struttura del JSON è descritta in seguito.
	 * La risposta può contenere anche più messaggi, ma una sola keyboard.
	 * @param userID L'ID dell'utente, o della chat, a seconda dell'implementazione.
	 * Deve avere al massimo 9 cifre, altrimenti verrà troncato a 9 cifre e
	 * in tal caso la comunicazione client-server
	 * tratterà quell'userID come se avesse davvero 9 cifre da quel momento in poi.
	 * @param messageID L'ID del messaggio inviato dall'utente.
	 * @param timeStamp Il tempo espresso come UNIX Epoch in millisecondi.
	 * @param text Il testo del messaggio inviato dall'utente.
	 * @param firstname Il nome dell'utente da utilizzare quando il bot vuole riferirsi all'utente per nome.
	 * @param botName Il nome del bot.
	 * @return Oggetto JSON contenente informazioni riguardo il messaggio da inviare, nel formato seguente:
{
    "messages": [
        {
            "text": "Reply Text"
        },
        {
            "text": "Another Reply Text",
            "photo": ""
        },
        {
            "text": "Trailer",
            "link": "http://www.example.com/trailer?entity=Example"
        }
    ],
	"reply_markup": {
		"keyboard": [
			[
				"One option"
			],
			[
				"Another option"
			],
			[
				[
					"Three"
				],
				[
					"Options"
				],
				[
					"Same row"
				]
			]
		],
		"resize_keyboard": true,
		"one_time_keyboard": false
	},
	"auxAPI": {
	    "apiURL": "http://193.204.187.192:8091/movierecsysservice/restService/exampleAuxAPI",
	    "messageID": "mid.$cAAeMCT36TB",
	    "parameters": {
	        "displayText": "text",
	        "data": {
	            "image": "www.example.com/image.jpg",
	            "imageCaption": "This is the movie poster",
	            "postImageSpeech": "What's next?",
	            "messageID": "mid.$cAAeMCT36TB"
	        }
	    }
	}
}
    Gli oggetti "photo" e "reply_markup" sono opzionali.
    L'oggetto "text" può contenere i caratteri '*' e '_' che indicano che il testo
    al loro interno dovrebbe essere mostrato rispettivamente in grassetto e in
    corsivo.
    Il campo auxAPI è opzionale, e indica al bot che deve effettuare 
    un'altra chiamata perchè la risposta non è conclusa; 
    prima di farla, può già mostrare il messaggio intermedio.
    Se auxAPI non contiene il campo "parameters", la richiesta da effettuare è di tipo
    HTTP GET verso l'URL specificato da ,"apiURL", altrimenti è HTTP POST e deve anche
    contenere come body i dati contenuti in "parameters".
    Notare che il campo messageID sarà contenuto in apiURL per richieste GET e in "parameters"
    per richieste POST.
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String sendMessageToServer(
			@QueryParam("userID") String userID,
			@QueryParam("messageID") String messageID,
			@QueryParam("timeStamp") String timeStamp,
			@QueryParam("text") String text,
			@QueryParam("firstname") String firstname,
			@QueryParam("botName") String botName,
			@QueryParam("lastName") String lastName,
			@QueryParam("userName") String userName) {
		
		this.userID = userID;
		this.messageID = messageID;
		this.timeStamp = timeStamp;
		this.text = text;
		this.firstname = firstname;
		this.botName = botName;
		this.lastName = lastName;
		this.userName = userName;
		
		
//		this.userID = FormatUtils.truncateID(this.userID);
		
		Reply reply = handleMessage();
		
		System.out.println("/?userID=" + userID + "&messageID=" + messageID + "&timeStamp="
				+ timeStamp + "&text=" + text + "&firstname=" + firstname + "&botName=" + botName);
		
		String json = null;
		
		if (reply != null) {
			Message[] replyMessages = reply.getMessages();
			ReplyMarkup replyMarkup = reply.getReplyMarkup();
			AuxAPI auxAPI = reply.getAuxAPI();

			KeyboardMarkup replyKeyboard = null;
			if (replyMarkup != null) {
				String[][] stringKeyboard = reply.getReplyMarkup().getKeyboard().getOptions();
				boolean resizeKeyboard = reply.getReplyMarkup().getResizeKeyboard();
				boolean oneTimeKeyboard = reply.getReplyMarkup().getOneTimeKeyboard();
			    replyKeyboard = new KeyboardMarkup(resizeKeyboard, oneTimeKeyboard, stringKeyboard);
			}
			
			JsonReply jsonData = new JsonReply(replyMessages, replyKeyboard, auxAPI);
			json = jsonData.toJson();
		} else {
			json = "Duplicate message ID";
		}
		
		return json;
	}
	
	/*
	 * Gestisce il messaggio dell'utente utilizzando le varie funzioni del server
	 * e restituisce la Reply.
	 */
	private Reply handleMessage() {
		
		Reply reply = null;
		
		UserMessageHandler userMessageHandler = new UserMessageHandler(userID, firstname, lastName, userName, botName);
		try {
			userMessageHandler.setMessage(messageID, text, timeStamp);
			reply = userMessageHandler.handle();
		} catch (InvalidMessageException e) {
			System.err.println(e.getMessage());
		} catch (Exception e) {
			System.err.println("[MessageFromUser] " + e.getMessage());
			e.printStackTrace();
		}

		return reply;
	}
	
	

}
