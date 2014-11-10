/*
 * $RCSfile: JulianDate.java,v $
 * $Revision: 1.1.1.1 $
 * $Date: 2002/09/09 13:51:12 $
 */

package com.jivesoftware.util;

import java.util.*;

/**
 * An implementation of a class used represent a Julian date. One very important
 * thing to note about this class is that months ARE NOT zero based (unlike
 * the Java Date class).
 *
 * @author Matt Welsh (matt@matt-welsh.com)
 */
public class JulianDate {

	//----------------------------------------------------------------------------
	// Static methods
	//----------------------------------------------------------------------------

	public static JulianDate getCurrentJD() {
		return new JulianDate();
	}

	//----------------------------------------------------------------------------
	// Constructors
	//----------------------------------------------------------------------------

	/**
	 * Create a JD based on the current system time.
	 */
	public JulianDate() {
		GregorianCalendar currentDate = new GregorianCalendar();
		int currYear = currentDate.get(Calendar.YEAR);
		int currMonth = currentDate.get(Calendar.MONTH) + 1;
		int currDay = currentDate.get(Calendar.DAY_OF_MONTH);
		int currHour = currentDate.get(Calendar.HOUR);
		int currMin = currentDate.get(Calendar.MINUTE);
		int currSec = currentDate.get(Calendar.SECOND);
		jd = computeJD(currYear, currMonth, currDay, currHour, currMin, currSec);
	}

	/**
 	 * Create a JD based on the given time.
	 *
	 * @param iYear The year to use
	 * @param month The month to use (NOT ZERO BASED i.e. Jan = 1)
	 * @param day The day to use
	 */
	public JulianDate(int iYear, int month, int day) {
		this(iYear, month, day, 0, 0 ,0);
	}

	/**
	 * Create a JD based on the given time.
	 *
	 * @param iYear The year to use
	 * @param month The month to use (NOT ZERO BASED i.e. Jan = 1)
	 * @param day The day to use
	 * @param hour The hour to use
	 * @param min The minute to use
	 * @param sec The second to use
	 */
	public JulianDate(int iYear, int month, int day, int hour, int min, int sec) {
		jd = computeJD(iYear, month, day, hour, min, sec);
	}

	/**
	 * Create a JD based on the given time.
	 *
	 * @param jd The julian date as a double
	 */
	public JulianDate(double jd) {
		this.jd = jd;
		double j = jd;
		gregorianDate = computeGregorianDate(j);
		//et = computeET(j);
		//paramU = computeParamU(et);
	}

	//----------------------------------------------------------------------------
	// Public methods
	//----------------------------------------------------------------------------

	/**
	 * Gets the julian date as a double.
	 *
	 * @return The julian date as a double.
	 */
	public double getJulianDate() {
		return jd;
	}


	/**
	 * Computes the number of Julian Centuries from 1900 Jan 0.5
	 *
	 * @return The number of Julian Centuries from 1900 Jan 0.5
	 */
	public double getJulianCentury () {
		return ((jd - 2415020.0) / 36525.0);
	}

	/**
	 * Returns the equivelent date on the Gregorian calendar.
	 *
	 * @return the equivelent date on the Gregorian calendar.
	 */
	public GregorianCalendar getGregorianDate() {
		return gregorianDate;
	}

	/**
	 * Returns the equivelent ephemeris time equivelent of this date. This is a
	 * more stable time scale than UT and is useful for high precision
	 * planetary computations.
	 *
	 * @return The ephemeris time representing this object.
	 */
	public double getEphemerisTime() {
		return et;
	}

	/**
	 * Gets the "U" parameter used in high precision planetary computations.
	 *
	 * @return The "U" parameter.
	 */
	public double getParamU() {
		return paramU;
	}

	/**
	 * Return this object as a String.
	 *
	 * @return A String object representing this object.
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("JulianDate");
		buffer.append("[");
		buffer.append(String.valueOf(jd));
		buffer.append(" ");
		buffer.append(gregorianDate.get(GregorianCalendar.MONTH));
		buffer.append("/");
		buffer.append(gregorianDate.get(GregorianCalendar.DAY_OF_MONTH));
		buffer.append("/");
		buffer.append(gregorianDate.get(GregorianCalendar.YEAR));
		buffer.append(" ");
		buffer.append(gregorianDate.get(GregorianCalendar.HOUR));
		buffer.append(":");
		buffer.append(gregorianDate.get(GregorianCalendar.MINUTE));
		buffer.append(":");
		buffer.append(gregorianDate.get(GregorianCalendar.SECOND));
		buffer.append(']');
		return buffer.toString();
	}

	//----------------------------------------------------------------------------
	// Private methods
	//----------------------------------------------------------------------------
	private double computeJD(int iYear, int month, int day,
	                int hour, int min, int sec) {
		int y;
		int m;
		int B;
		double C;

		if (month <= 2) {
			y = iYear - 1;
			m = month + 12;
		}
		else {
			y = iYear;
			m = month;
		}

		if (y < 0 ) {
			C = -0.75;
		}
		else {
			C = 0;
		}

		double D = ((double)hour / 24.0) + ((double)min / 1440.0) +
			((double)sec / 86400.0);

		if (day + 31L * (month + 12L * iYear) >= IGREG) {
			int A= (int) (y / 100);
			B= 2 - A + (int)(A / 4);
		}
		else B = 0;

		double j = ((long)(365.25* y + C)) + ((long)(30.6001 * (m + 1))) +
		          day + D + 1720994.5 + B;
		gregorianDate = computeGregorianDate(j);
		et = computeET(j);
		paramU = computeParamU(et);
		return j;
	}

	/**
	 * Compute a gregorian calendar date based on the given JD
	 */
	public static GregorianCalendar computeGregorianDate (double jDate) throws IllegalArgumentException {
		double A;

		// Routine doesn't work for negative JDs
		if (jDate<0) {
			throw new IllegalArgumentException("Negative Julian Date in JulianDate::computeGregorianDate");
		}

		jDate += 0.5;

		double integerPart = (long) jDate;             // Z = integerPart
		double fractionalPart = jDate - (integerPart); // F= fractionalPart
		if (integerPart >= 2299161.0) {
			long Alpha= (long) ((integerPart - 1867216.25) / 36524.25);
			A= integerPart + 1.0 + (double)Alpha;
			A -= (long)((double)Alpha/4.0);
		}
		else A= integerPart;

		double B= A + 1524.0;
		long C= (long) ((B - 122.1) / 365.25);
		long D= (long) (365.25 * (double)C);
		long E= (long) (( B - (double)D) / 30.6001);
		double dayWD = B - (double)D + fractionalPart;

		dayWD -= (double) ((long)(30.6001 * (double)E)); // Day with decimals
		int day= (int) dayWD;                          // day= INT only
		dayWD -= (double) day;                         // Fractional part only
		int month= (int)(E - 1);
		if (month > 12) month -= 12;
		int year= (int)(C - 4716);
		if (month <= 2) ++(year);
		if (year == 0) --(year);
		dayWD *= 24.0;           // Convert decimal days to decimal hours
		int hour = (int) dayWD;
		dayWD -= (double)hour;   // Subtract hours leaving decimal minutes
		dayWD *= 60.0;           // and then multiply by 60 for minutes
		int min = (int)dayWD;
		dayWD -= (double)min;    // Subtract Whole minutes leaving decimal secs
		dayWD *= 60.0;           // and multiply by 60 for whole seconds
		int sec = (int) dayWD;
		return new GregorianCalendar(year, month - 1, day, hour, min, sec);
	}

	/**
	 * Compute ephemeris time
	 *
	 * @param the Julian date for which to compute ET
	 */
	private double computeET(double jDate) {
		double d1 = 2378497.0;

		double dt = (jDate - d1);
		dt = dt / 36525.0;
		dt = dt - 0.1;
		dt = dt * dt;
		dt = dt * 32.5;
		dt = -15.0 + dt;
		dt = dt / 86400.0;
		double eTime = jDate + dt;
		return eTime;
	}

	/**
	 * Compute the time from J2000.0 in units of 10,000 julian years.
	 *
	 * @param The ephemeris time of the date in question.
	 */
	private double computeParamU(double eTime) {
		return (eTime - 2451545.0) / 3652500.0;

	}

	private long IGREG = (15 + 31L * (10 + 12L * 1582));

	protected double jd;
	protected GregorianCalendar gregorianDate;
	protected double et;
	protected double paramU;
}