package com.djx.pin.utils;

public class ConstellationUtil {

	public  enum Constellation {
		Capricorn(1, "摩羯�?"), Aquarius(2, "水瓶�?"), Pisces(3, "双鱼�?"), Aries(4,
		"白羊�?"), Taurus(5, "金牛�?"), Gemini(6, "双子�?"), Cancer(7, "巨蟹�?"), Leo(
		8, "狮子�?"), Virgo(9, "处女�?"), Libra(10, "天秤�?"), Scorpio(11, "天蝎�?"), Sagittarius(
		12, "射手�?");

		private Constellation(int code, String chineseName) {
		this.code = code;
		this.chineseName = chineseName;
		}
		private int code;
		private  String chineseName;

		public int getCode() {
		return this.code;
		}
		public String getChineseName() {
		return this.chineseName;
		}
		}

		public static final Constellation[] constellationArr = {
		Constellation.Aquarius, Constellation.Pisces, Constellation.Aries,
		Constellation.Taurus, Constellation.Gemini, Constellation.Cancer,
		Constellation.Leo, Constellation.Virgo, Constellation.Libra,
		Constellation.Scorpio, Constellation.Sagittarius,
		Constellation.Capricorn 
		};

		public static final int[] constellationEdgeDay = { 21, 20, 21, 21, 22, 22,
		23, 24, 24, 24, 23, 22 };

		public static String calculateConstellation(String birthday) {
		if (birthday == null || birthday.trim().length() == 0)
		throw new IllegalArgumentException("the birthday can not be null");
		String[] birthdayElements = birthday.split("-");
		if (birthdayElements.length != 3) 
		throw new IllegalArgumentException(
		"the birthday form is not invalid");
		int month = Integer.parseInt(birthdayElements[1]);
		int day = Integer.parseInt(birthdayElements[2]);
		if (month == 0 || day == 0 || month > 12)
		return "";
		month = day < constellationEdgeDay[month - 1] ? month - 1:month;
		return month > 0 ? constellationArr[month - 1].getChineseName(): constellationArr[11].getChineseName();
		}
}
