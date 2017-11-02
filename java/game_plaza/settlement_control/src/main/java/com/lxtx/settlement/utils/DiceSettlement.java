package com.lxtx.settlement.utils;

public class DiceSettlement {
	private static BaseDiceType[] sumDiceTypes;

	public static final int DICE_TYPE_LITTLE_OPTION = 1;// 小
	public static final int DICE_TYPE_LARGE_OPTION = 2;
	public static final int DICE_TYPE_BAO_ZI_TOTAL_OPTION = 3;
	public static final int DICE_TYPE_BAO_ZI_SINGLE_OPTION_BEGIN = 4;
	public static final int DICE_TYPE_DUI_ZI_SINGLE_OPTION_BEGIN = 10;
	public static final int DICE_TYPE_SUM_VALUE_OPTION_BEGIN = 16;
	public static final int DICE_TYPE_SINGLE_OPTION_BEGIN = 30;
	public static final int SUM_VALUE_VALUE_MIN = 4;
	public static final int SUM_VALUE_VALUE_MAX = 17;

	public static final double DICE_TYPE_LITTLE_MULTIPLE = 1.96f;
	public static final double DICE_TYPE_LARGE_MULTIPLE = 1.96f;
	public static final double DICE_TYPE_BAO_ZI_TOTAL_MULTIPLE = 36;
	public static final double DICE_TYPE_BAO_ZI_SINGLE_MULTIPLE = 200;
	public static final double DICE_TYPE_DUI_ZI_SINGLE_MULTIPLE = 10;
	public static final double[] DICE_TYPE_SUM_VALUE_MULTIPLES = { 50, 18, 14, 12, 8, 6, 6, 6, 6, 8, 12, 14, 18, 50 };// 4-17
	public static final double[] DICE_TYPE_SINGLE_MULTIPLES = { 0, 1, 2, 3 };// 押中1，2，3分别的赔率

	static {
		loadSumDiceTypes();
	}

	private static void setDiceType(BaseDiceType diceType) {
		sumDiceTypes[diceType.getIndex() - 1] = diceType;
	}

	private static void setDiceType(BaseDiceType[] diceTypes) {
		for (BaseDiceType type : diceTypes) {
			setDiceType(type);
		}
	}

	private static void loadSumDiceTypes() {
		sumDiceTypes = new BaseDiceType[35];
		setDiceType(new DiceTypeLittle());
		setDiceType(new DiceTypeLarge());
		setDiceType(new DiceTypeBaoziTotal());
		setDiceType(DiceTypeBaoziSingle.getSum());
		setDiceType(DiceTypeDuiZiSingle.getSum());
		setDiceType(DiceTypeSumValue.getSum());
		setDiceType(DiceTypeSingle.getSum());
	}

	public static BaseDiceType getDiceType(int option) {
		return sumDiceTypes[option - 1];
	}

	public static double[] getAllTypeMultiples(int[] dices) {
		double[] multiples = new double[sumDiceTypes.length];
		for (int i = 0; i < sumDiceTypes.length; i++) {
			multiples[i] = sumDiceTypes[i].getDicesMultiple(dices);
		}
		return multiples;
	}

	public static abstract class BaseDiceType {

		private int index;
		private double multiple;

		public BaseDiceType(int index, double multiple) {
			super();
			this.index = index;
			this.multiple = multiple;
		}

		protected abstract boolean isConform(int[] dices);

		public int getIndex() {
			return index;
		}

		public double getMultiple() {
			return multiple;
		}

		public double getDicesMultiple(int[] dices) {
			if (this.isConform(dices)) {
				return multiple;
			} else {
				return 0;
			}
		}
	}

	public static class DiceTypeLittle extends BaseDiceType {

		public DiceTypeLittle() {
			super(DICE_TYPE_LITTLE_OPTION, DICE_TYPE_LITTLE_MULTIPLE);
		}

		@Override
		protected boolean isConform(int[] dices) {
			boolean baozi = dices[0] == dices[1] && dices[1] == dices[2];
			if (baozi) {
				return false;
			} else {
				return (dices[0] + dices[1] + dices[2]) <= 10;
			}
		}
	}

	public static class DiceTypeLarge extends BaseDiceType {

		public DiceTypeLarge() {
			super(DICE_TYPE_LARGE_OPTION, DICE_TYPE_LARGE_MULTIPLE);
		}

		@Override
		protected boolean isConform(int[] dices) {
			boolean baozi = dices[0] == dices[1] && dices[1] == dices[2];
			if (baozi) {
				return false;
			} else {
				return (dices[0] + dices[1] + dices[2]) > 10;
			}
		}
	}

	public static class DiceTypeBaoziTotal extends BaseDiceType {

		public DiceTypeBaoziTotal() {
			super(DICE_TYPE_BAO_ZI_TOTAL_OPTION, DICE_TYPE_BAO_ZI_TOTAL_MULTIPLE);
		}

		@Override
		protected boolean isConform(int[] dices) {
			return dices[0] == dices[1] && dices[1] == dices[2];
		}
	}

	public static class DiceTypeBaoziSingle extends BaseDiceType {
		public static DiceTypeBaoziSingle[] getSum() {
			DiceTypeBaoziSingle[] sum = new DiceTypeBaoziSingle[6];
			for (int i = 1; i <= 6; i++) {
				sum[i - 1] = new DiceTypeBaoziSingle(i);
			}
			return sum;
		}

		private int diceValue;

		private DiceTypeBaoziSingle(int value) {
			super(DICE_TYPE_BAO_ZI_SINGLE_OPTION_BEGIN + value - 1, DICE_TYPE_BAO_ZI_SINGLE_MULTIPLE);// 4-9
			this.diceValue = value;
		}

		@Override
		protected boolean isConform(int[] dices) {
			return dices[0] == diceValue && dices[1] == diceValue && dices[2] == diceValue;
		}
	}

	public static class DiceTypeDuiZiSingle extends BaseDiceType {

		public static DiceTypeDuiZiSingle[] getSum() {
			DiceTypeDuiZiSingle[] sum = new DiceTypeDuiZiSingle[6];
			for (int i = 1; i <= 6; i++) {
				sum[i - 1] = new DiceTypeDuiZiSingle(i);
			}
			return sum;
		}

		private int diceValue;

		private DiceTypeDuiZiSingle(int value) {
			super(DICE_TYPE_DUI_ZI_SINGLE_OPTION_BEGIN + value - 1, DICE_TYPE_DUI_ZI_SINGLE_MULTIPLE);// 10-15
			this.diceValue = value;
		}

		@Override
		protected boolean isConform(int[] dices) {
			int count = 0;
			for (int d : dices) {
				count += d == this.diceValue ? 1 : 0;
			}
			return count >= 2;
		}
	}

	public static class DiceTypeSumValue extends BaseDiceType {
		public static DiceTypeSumValue[] getSum() {
			DiceTypeSumValue[] sum = new DiceTypeSumValue[SUM_VALUE_VALUE_MAX - SUM_VALUE_VALUE_MIN + 1];
			for (int i = SUM_VALUE_VALUE_MIN; i <= SUM_VALUE_VALUE_MAX; i++) {
				sum[i - SUM_VALUE_VALUE_MIN] = new DiceTypeSumValue(i,
						DICE_TYPE_SUM_VALUE_MULTIPLES[i - SUM_VALUE_VALUE_MIN]);
			}
			return sum;
		}

		private int sumValue;

		private DiceTypeSumValue(int value, double multiple) {
			super(DICE_TYPE_SUM_VALUE_OPTION_BEGIN + value - SUM_VALUE_VALUE_MIN, multiple);// 16-29
			this.sumValue = value;
		}

		@Override
		protected boolean isConform(int[] dices) {
			return (dices[0] + dices[1] + dices[2]) == this.sumValue;
		}
	}

	public static class DiceTypeSingle extends BaseDiceType {
		public static DiceTypeSingle[] getSum() {
			DiceTypeSingle[] sum = new DiceTypeSingle[6];
			for (int i = 1; i <= 6; i++) {
				sum[i - 1] = new DiceTypeSingle(i);
			}
			return sum;
		}

		private int diceValue;

		private DiceTypeSingle(int value) {
			super(DICE_TYPE_SINGLE_OPTION_BEGIN + value - 1, DICE_TYPE_SINGLE_MULTIPLES[3]);// 30-35
			this.diceValue = value;
		}

		@Override
		protected boolean isConform(int[] dices) {
			return dices[0] == this.diceValue || dices[1] == this.diceValue || dices[2] == this.diceValue;
		}

		@Override
		public double getDicesMultiple(int[] dices) {
			int count = 0;
			for (int d : dices) {
				count += d == this.diceValue ? 1 : 0;
			}
			return DICE_TYPE_SINGLE_MULTIPLES[count];
		}

	}
}
