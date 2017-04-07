package com.yukthitech.autox.test.ui.common;

/**
 * Form field types.
 * @author akiran
 */
public enum FormFieldType
{
	/**
	 * Simple text field.
	 */
	TEXT(new SimpleFieldAccessor()),
	
	/**
	 * An int field.
	 */
	INT(new SimpleFieldAccessor()),
	
	/**
	 * Date field.
	 */
	DATE(new SimpleFieldAccessor()),
	
	/**
	 * Password field.
	 */
	PASSWORD(new SimpleFieldAccessor()),
	
	/**
	 * Multi line text field.
	 */
	MULTI_LINE_TEXT(new SimpleFieldAccessor()),
	
	/**
	 * Drop down field.
	 */
	DROP_DOWN(new SelectFieldAccessor()),
	
	/**
	 * Check box field.
	 */
	CHECK_BOX(new CheckboxFieldAccessor()),
	
	/**
	 * Radio button field. 
	 */
	RADIO_BUTTON(new CheckboxFieldAccessor());
	
	/**
	 * Accessor for current field type.
	 */
	private IFieldAccessor fieldAccessor;

	/**
	 * Instantiates a new form field type.
	 *
	 * @param fieldAccessor the field accessor
	 */
	private FormFieldType(IFieldAccessor fieldAccessor)
	{
		this.fieldAccessor = fieldAccessor;
	}
	
	/**
	 * Fetches field accessor for current type.
	 * @return Field accessor for current type.
	 */
	public IFieldAccessor getFieldAccessor()
	{
		return fieldAccessor;
	}
}
