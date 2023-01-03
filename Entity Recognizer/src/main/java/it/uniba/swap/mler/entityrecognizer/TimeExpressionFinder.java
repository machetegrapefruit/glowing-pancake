package it.uniba.swap.mler.entityrecognizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.joda.time.Partial;

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.time.TimeAnnotations;
import edu.stanford.nlp.time.TimeAnnotator;
import edu.stanford.nlp.time.TimeExpression;
import edu.stanford.nlp.time.SUTime.Temporal;
import edu.stanford.nlp.util.CoreMap;
import it.uniba.swap.mler.exception.TimexParsingException;

public class TimeExpressionFinder {
	
	public static it.uniba.swap.mler.entityrecognizer.TimeExpression fromAnnotation(List<CoreMap> timexAnnsAll, DateTime reference) throws TimexParsingException {
		List<DateTime> dateTimes = new ArrayList<DateTime>();
		String timeEvent = null;
		for (CoreMap cm : timexAnnsAll) {
			Temporal temporal = cm.get(TimeExpression.Annotation.class).getTemporal();
			String timexType = temporal.getTimexType().name();
			String timex = temporal.getTimexValue();
			Instant instant = null;
			Partial partial = null;
			if (temporal.getTime() != null) {
				instant = temporal.getTime().getJodaTimeInstant();
				partial = temporal.getTime().getJodaTimePartial();
			}
			System.out.println("Token text : " + cm.toString());
			System.out.println("Temporal Value : " + temporal.toString());
			System.out.println("Temporal Value ISO: " + temporal.toISOString());
			System.out.println("Timex : " + temporal.getTimexValue());
			System.out.println("Timex type : " + temporal.getTimexType().name());
			System.out.println("Timex value: " + temporal.getTimexValue());
			System.out.println("Time: " + instant);
			System.out.println("Partial: " + partial);
			System.out.println("Duration: " + temporal.getDuration());
			System.out.println("Period: " + temporal.getPeriod());
			if (temporal.getRange() != null) {
				System.out.println("Range: " + temporal.getRange().begin() + " " + temporal.getRange().end());
			}
			
			DateTime time = reference;
			boolean weekDaySpecified = false;
			boolean dayOfMonthSpecified = false;
			
			if (timexType.equals("DATE") || timexType.equals("TIME")) {
				String[] tvSplit = temporal.toString().split("\\s");
				int i = 0;
				while (i < tvSplit.length) {
					String tvFragment = tvSplit[i];
					if (tvFragment.equals("THIS")) {
						//For time expressions of form "this day/week/month"
						//We set the date to the start of the corresponding day/week/month.
						i++;
						tvFragment = tvSplit[i];
						time = handleThis(tvFragment, time);
					} else if (tvFragment.equals("OFFSET")) {
						//We need to offset the previously defined date. For example, P-1D means that 
						//the date must be moved 1 day backwards.
						i++;
						tvFragment = tvSplit[i];
						time = handleOffset(tvFragment, time);
					} else if (tvFragment.equals("START_CONTRACT")) {
						timeEvent = "START_CONTRACT";
					} else {
						//It's a date
						try {
							HandleDateResponse hdr = handleDate(tvFragment, time);
							time = hdr.time;
							weekDaySpecified = hdr.weekDaySpecified;
							dayOfMonthSpecified = hdr.dayOfMonthSpecified;
						} catch (TimexParsingException e) {
							throw new TimexParsingException("Timex format cannot be handled: " + timex.toString());
						}
					}
					i++;
				}
			} else if (timexType.equals("DURATION")) {
				time = handleDuration(temporal.toString(), time);
			} else {
				throw new TimexParsingException("Timex format cannot be handled: " + timex.toString());
			}
			
			/* Check if the resulting DateTime objects points to an instant in the future.
			 * If it does, we need to check if:
			 * 1. The weekday has been specified. For example: "Thursday", and today it is Wednesday. In this case
			 *    we need to move the date a week backward.
			 * 2. The month has been specified. For example: "June", and today it is May. in this case we need to
			 *    move the date an year backward.
			 * This also makes sure that "Thursday" and "Last thursday", and "June" and "Last june" have the same
			 * effect.
			 */
			if (weekDaySpecified && time.isAfter(reference.getMillis())) {
				time = time.minusWeeks(1);
			} else if (dayOfMonthSpecified && time.isAfter(reference.getMillis())) {
				time = time.minusYears(1);
			}
			
			dateTimes.add(time);
		}
		
		if (timeEvent != null) {
			//Special time events override dates
			return new it.uniba.swap.mler.entityrecognizer.TimeExpression(timeEvent, reference);
		} else if (dateTimes.size() > 1) {
			Collections.sort(dateTimes);
			return new it.uniba.swap.mler.entityrecognizer.TimeExpression(dateTimes.get(0), dateTimes.get(dateTimes.size() - 1));
		} else if (dateTimes.size() == 1) {
			return new it.uniba.swap.mler.entityrecognizer.TimeExpression(dateTimes.get(0), reference);
		}
		return null;
	}
	
	private static HandleDateResponse handleDate(String tvFragment, DateTime time) throws TimexParsingException {
		boolean dayOfMonthSpecified = false;
		boolean weekDaySpecified = false;
		String[] dateSplit = tvFragment.split("-");
		if (dateSplit.length > 1) {
			
		} else {
			throw new TimexParsingException("Timex format cannot be handled");
		}
		//First part is the year
		try {
			int year = Integer.parseInt(dateSplit[0]);
			time = time.withYear(year);
			time = time.withDayOfYear(1);
		} catch (NumberFormatException e) {
			//We assume that it's the current year
		}
		//Second part could be month or week
		char monthOrWeek = dateSplit[1].charAt(0);
		boolean isWeek = monthOrWeek == 'W';
		if (isWeek) {
			//It's the week of year
			try {
				int week = Integer.parseInt(dateSplit[1].substring(1));
				time = time.withWeekOfWeekyear(week);
			} catch (NumberFormatException e) {}
		} else {
			//It's the month
			try {
				int month = Integer.parseInt(dateSplit[1]);
				time = time.withMonthOfYear(month);
				time = time.withDayOfMonth(1);
				dayOfMonthSpecified = true;
			} catch (NumberFormatException e) {}
		}
		//Check if the date object has two or three parts
		if (dateSplit.length == 3) {
			if (isWeek) {
				//The third part is the weekday
				try {
					int weekDay = Integer.parseInt(dateSplit[2]);
					time = time.withDayOfWeek(weekDay);
					weekDaySpecified = true;
				} catch (NumberFormatException e) {}
			} else {
				//The third part is the day of month
				try {
					int dayOfMonth = Integer.parseInt(dateSplit[2]);
					time = time.withDayOfMonth(dayOfMonth);
				} catch (NumberFormatException e) {}
			}
		}
		return new HandleDateResponse(time, weekDaySpecified, dayOfMonthSpecified);
	}
	
	private static DateTime handleThis(String tvFragment, DateTime time) {
		char offsetType = tvFragment.charAt(tvFragment.length() - 1);
		switch (offsetType) {
		case 'W':
			time = time.withDayOfWeek(1);
			break;
		case 'M':
			time = time.withDayOfMonth(1);
			break;
		case 'Y':
			time = time.withDayOfYear(1);
			break;
		}
		return time;
	}
	
	private static DateTime handleOffset(String tvFragment, DateTime time) {
		//We need to offset the previously defined date. For example, P-1D means that 
		//the date must be moved 1 day backwards.
		char offsetType = tvFragment.charAt(tvFragment.length() - 1);
		int offsetValue = Integer.parseInt(tvFragment.substring(1, tvFragment.length() - 1));
		switch (offsetType) {
		case 'D':
			time = time.plusDays(offsetValue);
			break;
		case 'W':
			time = time.plusWeeks(offsetValue);
			break;
		case 'M':
			time = time.plusMonths(offsetValue);
			break;
		case 'Y':
			time = time.plusYears(offsetValue);
			break;
		}
		return time;
	}
	
	private static DateTime handleDuration(String timex, DateTime time) {
		/* If SUTime recognizes a duration rather than a date
		 * We assume that it refers to a date that represents the start of the interval,
		 * and that the end of the interval is today. For example: "in the last three weeks"
		 * and today is 2018-10-09. The resulting date should be 2018-08-01.
		 */
		String[] durationSplit = timex.substring(1, timex.toString().length() - 1).split(",");
		String durationString = durationSplit[2];
		char offsetType = durationString.charAt(durationString.length() - 1);
		int offsetValue = Integer.parseInt(durationString.substring(1, durationString.length() - 1));
		switch (offsetType) {
		case 'D':
			time = time.minusDays(offsetValue - 1);
			break;
		case 'W':
			time = time.minusWeeks(offsetValue - 1);
			time = time.withDayOfWeek(1);
			break;
		case 'M':
			time = time.minusMonths(offsetValue - 1);
			time = time.withDayOfMonth(1);
			break;
		case 'Y':
			time = time.minusYears(offsetValue - 1);
			time = time.withDayOfYear(1);
			break;
		}
		return time;
	}
	
	private static class HandleDateResponse {
		public DateTime time;
		public boolean weekDaySpecified;
		public boolean dayOfMonthSpecified;
		public HandleDateResponse(DateTime time, boolean weekDaySpecified, boolean dayOfMonthSpecified) {
			super();
			this.time = time;
			this.weekDaySpecified = weekDaySpecified;
			this.dayOfMonthSpecified = dayOfMonthSpecified;
		}
	}
}
