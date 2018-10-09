package store.io;

public class RecordMapper {

	private int dateField;
	private int amountField;
	private int titleField;
	private int categoryField;
	private int commonField;
		
	private String dateFormat;

	private int fields;
	private int firstValidRecord;
	
	public RecordMapper(int dateField, int amountField, int titleField, int categoryField, int commonField,
			String dateFormat, int fields, int firstValidRecord) {
		super();
		this.dateField = dateField;
		this.amountField = amountField;
		this.titleField = titleField;
		this.categoryField = categoryField;
		this.commonField = commonField;
		this.dateFormat = dateFormat;
		this.fields = fields;
		this.firstValidRecord = firstValidRecord;
	}

	public int getFirstValidRecord() {
		return firstValidRecord;
	}

	public int getFields() {
		return fields;
	}

	public int getDateField() {
		return dateField;
	}

	public int getAmountField() {
		return amountField;
	}

	public int getTitleField() {
		return titleField;
	}

	public int getCategoryField() {
		return categoryField;
	}

	public int getCommonField() {
		return commonField;
	}

	public String getDateFormat() {
		return dateFormat;
	}
	
	
}
