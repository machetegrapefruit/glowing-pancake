package utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Mappa di utilità contenente i codici delle emoji utilizzate.
 * @author Altieri
 *
 */
public abstract class EmojiCodes {

	public static final String ANGER = "anger";
	public static final String ARROW_RIGHT = "arrow_right";
	public static final String BACKARROW = "backarrow";
	public static final String BLACK_SQUARE_BUTTON = "black_square_button";
	public static final String BLUE_BOOK = "blue_book";
	public static final String BLUE_CIRCLE = "blue_circle";
	public static final String BOOKMARK_TAGS = "bookmark_tags";
	public static final String BOOKS = "books";
	public static final String BRIEFCASE = "briefcase";
	public static final String CALENDAR = "calendar";
	public static final String CAMERA = "camera";
	public static final String CHECK_MARK = "check_mark";
	public static final String CLAPPER = "clapper";
	public static final String CLIPBOARD = "clipboard";
	public static final String CLOCKFLAT = "clockflat";
	public static final String CONFUSED = "confused";
	public static final String CYCLONE = "cyclone";
	public static final String EXPRESSIONLESS = "expressionless";
	public static final String FILM_FRAME = "film_frame";
	public static final String GEAR = "gear";
	public static final String GLOBE_WITH_MERIDIANS = "globe_with_meridians";
	public static final String GREEN_BOOK = "green_book";
	public static final String HEAVY_X = "heavy_x";
	public static final String MAGNIFYING_GLASS = "magnifying_glass";
	public static final String MAN_LEVITATING = "man_levitating";
	public static final String MEGAPHONE = "megaphone";
	public static final String MONEY_BAG = "money_bag";
	public static final String MOVIE_CAMERA = "movie_camera";
	public static final String MUSICAL_SCORE = "musical_score";
	public static final String NO_ENTRY_SIGN = "no_entry_sign";
	public static final String NOTEBOOK_WITH_DECORATIVE_COVER = "notebook_with_decorative_cover";
	public static final String NOTES = "notes";
	public static final String OFFICE = "office";
	public static final String ORANGE_BOOK = "orange_book";
	public static final String PEN = "pen";
	public static final String POINT_LEFT = "point_left";
	public static final String POINT_RIGHT = "point_right";
	public static final String POPCORN = "popcorn";
	public static final String PROJECTOR = "projector";
	public static final String RED_CIRCLE = "red_circle";
	public static final String SILHOUETTE = "silhouette";
	public static final String SLIGHT_SMILE = "slight_smile";
	public static final String SLIGHTLY_SAD = "slightly_sad";
	public static final String SMILEY = "smiley";
	public static final String STAR = "star";
	public static final String STUDIO_MIC = "studio_mic";
	public static final String THINKING = "thinking";
	public static final String THUMBSDOWN = "thumbsdown";
	public static final String THUMBSUP = "thumbsup";
	public static final String TROPHY = "trophy";
	public static final String VHS = "vhs";
	public static final String WASTE_BASKET = "waste_basket";
	public static final String WHITE_SQUARE_BUTTON = "white_square_button";
	public static final String WINK = "wink";
	
	/**
	 * Contiene i codici delle emoji validi per poter essere inseriti in un oggetto JSON.
	 */
	public static Map<String, String> hexHtmlSurrogatePairs = new HashMap<String, String>();
	
	static {
		fillHexHtmlSurrogatePairs();
	}
	
	public static Map<String, String> getEmojis() {
		return hexHtmlSurrogatePairs;
	}
	
	private static void fillHexHtmlSurrogatePairs() {
		hexHtmlSurrogatePairs.put(ANGER, "\ud83d\udca2");
		hexHtmlSurrogatePairs.put(ARROW_RIGHT, "\u27a1");
		hexHtmlSurrogatePairs.put(BACKARROW, "\ud83d\udd19");
		hexHtmlSurrogatePairs.put(BLACK_SQUARE_BUTTON, "\ud83d\udd32");
		hexHtmlSurrogatePairs.put(BLUE_BOOK, "\ud83d\udcd8");
		hexHtmlSurrogatePairs.put(BLUE_CIRCLE, "\ud83d\udd35");
		hexHtmlSurrogatePairs.put(BOOKMARK_TAGS, "\ud83d\udcd1");
		hexHtmlSurrogatePairs.put(BOOKS, "\ud83d\udcda");
		hexHtmlSurrogatePairs.put(BRIEFCASE, "\ud83d\udcbc");
		hexHtmlSurrogatePairs.put(CALENDAR, "\ud83d\uddd3");
		hexHtmlSurrogatePairs.put(CAMERA, "\ud83d\udcf7");
		hexHtmlSurrogatePairs.put(CHECK_MARK, "\u2714");
		hexHtmlSurrogatePairs.put(CLAPPER, "\ud83c\udfac");
		hexHtmlSurrogatePairs.put(CLIPBOARD, "\ud83d\udccb");
		hexHtmlSurrogatePairs.put(CLOCKFLAT, "\ud83d\udd70");
		hexHtmlSurrogatePairs.put(CONFUSED, "\ud83d\ude15");
		hexHtmlSurrogatePairs.put(CYCLONE, "\ud83c\udf00");
		hexHtmlSurrogatePairs.put(EXPRESSIONLESS, "\ud83d\ude11");
		hexHtmlSurrogatePairs.put(FILM_FRAME, "\ud83c\udf9e");
		hexHtmlSurrogatePairs.put(GEAR, "\u2699");
		hexHtmlSurrogatePairs.put(GLOBE_WITH_MERIDIANS, "\ud83c\udf10");
		hexHtmlSurrogatePairs.put(GREEN_BOOK, "\ud83d\udcd7");
		hexHtmlSurrogatePairs.put(HEAVY_X, "\u2716");
		hexHtmlSurrogatePairs.put(MAGNIFYING_GLASS, "\ud83d\udd0e");
		hexHtmlSurrogatePairs.put(MAN_LEVITATING, "\ud83d\udd74");
		hexHtmlSurrogatePairs.put(MEGAPHONE, "\ud83d\udce3");
		hexHtmlSurrogatePairs.put(MONEY_BAG, "\ud83d\udcb0");
		hexHtmlSurrogatePairs.put(MOVIE_CAMERA, "\ud83c\udfa5");
		hexHtmlSurrogatePairs.put(MUSICAL_SCORE, "\ud83c\udfbc");
		hexHtmlSurrogatePairs.put(NO_ENTRY_SIGN, "\ud83d\udeab");
		hexHtmlSurrogatePairs.put(NOTEBOOK_WITH_DECORATIVE_COVER, "\ud83d\udcd4");
		hexHtmlSurrogatePairs.put(NOTES, "\ud83c\udfb6");
		hexHtmlSurrogatePairs.put(OFFICE, "\ud83c\udfe2");
		hexHtmlSurrogatePairs.put(ORANGE_BOOK, "\ud83d\udcd9");
		hexHtmlSurrogatePairs.put(PEN, "\ud83d\udd8a");
		hexHtmlSurrogatePairs.put(POINT_LEFT, "\ud83d\udc48");
		hexHtmlSurrogatePairs.put(POINT_RIGHT, "\ud83d\udc49");
		hexHtmlSurrogatePairs.put(POPCORN, "\ud83c\udf7f");
		hexHtmlSurrogatePairs.put(PROJECTOR, "\ud83d\udcfd");
		hexHtmlSurrogatePairs.put(RED_CIRCLE, "\ud83d\udd34");
		hexHtmlSurrogatePairs.put(SILHOUETTE, "\ud83d\udc64");
		hexHtmlSurrogatePairs.put(SLIGHT_SMILE, "\ud83d\ude42");
		hexHtmlSurrogatePairs.put(SLIGHTLY_SAD, "\ud83d\ude41");
		hexHtmlSurrogatePairs.put(SMILEY, "\ud83d\ude03");
		hexHtmlSurrogatePairs.put(STAR, "\u2b50");
		hexHtmlSurrogatePairs.put(STUDIO_MIC, "\ud83c\udf99");
		hexHtmlSurrogatePairs.put(THINKING, "\ud83e\udd14");
		hexHtmlSurrogatePairs.put(THUMBSDOWN, "\ud83d\udc4e");
		hexHtmlSurrogatePairs.put(THUMBSUP, "\ud83d\udc4d");
		hexHtmlSurrogatePairs.put(TROPHY, "\ud83c\udfc6");
		hexHtmlSurrogatePairs.put(VHS, "\ud83d\udcfc");
		hexHtmlSurrogatePairs.put(WASTE_BASKET, "\ud83d\uddd1");
		hexHtmlSurrogatePairs.put(WHITE_SQUARE_BUTTON, "\ud83d\udd33");
		hexHtmlSurrogatePairs.put(WINK, "\ud83d\ude09");
	}

}
