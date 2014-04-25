package com.example.pickup;

import java.util.Date;


public abstract class DateTimeParser
{
	// Get String of Time
	static String time(Date time) {
		int hour = time.getHours();
		int minute = time.getMinutes();
		
		if (hour < 12) {
			if (hour == 0) {
				hour = 12;
			}
			if (minute < 10)
			{
				return hour + ":0" + minute + " AM";
			}
			else
			{
				return hour + ":" + minute + " AM";
			}
		} else {
			hour = hour - 12;
			if (hour == 0) {
				hour = 12;
			}
			if (minute < 10)
			{
				return hour + ":0" + minute + " PM";
			}
			else
			{
				return hour + ":" + minute + " PM";
			}
		}
	}

	// Get String of Date
	static String date(Date date) {
		int year = date.getYear() + 1900;
		int month = date.getMonth();
		int day = date.getDate();
		
		String m = "None";
		switch ((int) month) {
		case 0:
			m = "Jan";
			break;
		case 1:
			m = "Feb";
			break;
		case 2:
			m = "Mar";
			break;
		case 3:
			m = "Apr";
			break;
		case 4:
			m = "May";
			break;
		case 5:
			m = "Jun";
			break;
		case 6:
			m = "Jul";
			break;
		case 7:
			m = "Aug";
			break;
		case 8:
			m = "Sept";
			break;
		case 9:
			m = "Oct";
			break;
		case 10:
			m = "Nov";
			break;
		case 11:
			m = "Dec";
			break;
		}

		return m + " " + day + ", " + year;
	}
}
