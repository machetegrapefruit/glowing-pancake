package utils;

/**
 * La classe MatchedElement contiene tutte le informazioni relative ad un'entità o proprietà che
 * è stata inserita o eliminata dal sistema. Queste informazioni comprendono l'id e la label
 * dell'entità o della proprietà riconosciuta, e il rating associato (o -1 se l'elemento
 * associato è stato eliminato).
 * @author isz_d
 *
 */
public class MatchedElement {
	private Alias element;
	private int rating;
	public MatchedElement(Alias element, int rating) {
		super();
		this.element = element;
		this.rating = rating;
	}
	public Alias getElement() {
		return element;
	}
	public int getRating() {
		return rating;
	}
	@Override
	public String toString() {
		return "AddedElement [element=" + element + ", rating=" + rating + "]";
	}
	public char getRatingSymbol() {
		if (rating == 1) {
			return '+';
		} else if (rating == 0) {
			return '-';
		} else {
			return '/';
		}
	}
	
}