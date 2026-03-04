package com.yukthitech.transform.event;

public enum TransformEventType
{
    /**
     * Condition evaluated. Result is boolean.
     */
	CONDITION_EVALUATED,

    /**
     * Switch condition evaluated. Result is boolean.
     */
    SWITCH_CONDITION_EVALUATED,

    /**
     * Switch value evaluated. Result is object.
     */
    SWITCH_VALUE_EVALUATED,

    /**
     * For each condition evaluated. Result is boolean.
     */
    FOR_EACH_CONDITION_EVALUATED,

    /**
     * List expression evaluated. Result is collection.
     */
    LIST_EXPRESSION_EVALUATED,

    /**
     * Key/name expression evaluated. Result is string.
     */
    KEY_EXPRESSION_EVALUATED,

    /**
     * Loop evaluated. Result list of loop evaluation.
     */
    LOOP_EVALUATED,

    /**
     * False value evaluated. Result is object.
     */
    FALSE_VALUE_EVALUATED,

    /**
     * Value evaluated. Result is object.
     */
    VALUE_EVALUATED,

    /**
     * Resource loaded. Result is object.
     */
    RESOURCE_LOADED,

    /**
     * Include processed. Result is object.
     */
    INCLUDE_PROCESSED,

    /**
     * Set variable evaluated. Result is object.
     */
    SET_VARIABLE_EVALUATED,

    /**
     * Replace entry evaluated. Result is object.
     */
    REPLACE_ENTRY_EVALUATED,

    /**
     * Key replaced. Result is new key name.
     */
    KEY_REPLACED,

    /**
     * Key value set. Result is new key name and value.
     */
    KEY_VALUE_SET,

    /**
     * Expression evaluated. Result is object.
     */
    FMARKER_EXPRESSION_EVALUATED,

    /**
     * Xpath expression evaluated. Result is object.
     */
    XPATH_EXPRESSION_EVALUATED,

    /**
     * Xpath multi expression evaluated. Result is object.
     */
    XPATH_MULTI_EXPRESSION_EVALUATED,

    /**
     * JSON path expression evaluated. Result is object.
     */
    JSON_PATH_EXPRESSION_EVALUATED,

    /**
     * JSON path multi expression evaluated. Result is object.
     */
    JSON_PATH_MULTI_EXPRESSION_EVALUATED,

    /**
     * Template expression evaluated. Result is object.
     */
    TEMPLATE_EXPRESSION_EVALUATED,

    /**
     * String expression evaluated. Result is string.
     */
    STRING_EXPRESSION_EVALUATED,
    ;
}
